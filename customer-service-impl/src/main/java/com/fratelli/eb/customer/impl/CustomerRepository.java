package com.fratelli.eb.customer.impl;


import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

@Singleton
public class CustomerRepository {


    private final CassandraSession session;

    @Inject
    public CustomerRepository(CassandraSession session, ReadSide readSide) {
        this.session = session;
        readSide.register(CustomerEventProcessor.class);
    }

    protected CompletionStage<UUID> getUserIdByEmail(String email) {

        return session
                .selectOne(
                        "SELECT * FROM customer_auth  WHERE email = ? " ,
                        email
                )

                .thenApply(rows -> rows.map(CustomerRepository::convertCustomerSummary).get())
                .thenApply(CustomerSummary -> CustomerSummary.getUUID());
    }

    private static Customer convertCustomerSummary(Row user) {
        return new Customer(
                user.getUUID("id"),
                user.getString("name"),
                user.getString("surname"),
                user.getString("email"),
                user.getString("password")
        );
    }

    private static class CustomerEventProcessor extends ReadSideProcessor<Event> {

        private final CassandraSession session;

        private final CassandraReadSide readSide;
        private PreparedStatement writeTitle = null; // initialized in prepare

        private CompletionStage<Done> prepareWriteTitle() {
            return session.prepare("INSERT INTO customer_auth (id, email, password, name, surname) VALUES (?, ?, ?, ?, ?)")
                    .thenApply(ps -> {
                        this.writeTitle = ps;
                        return Done.getInstance();
                    });
        }

        @Inject
        public CustomerEventProcessor(CassandraSession session, CassandraReadSide readSide) {
            this.session = session;
            this.readSide = readSide;
        }

        @Override
        public ReadSideProcessor.ReadSideHandler<Event> buildHandler() {
            // TODO build read side handler
            CassandraReadSide.ReadSideHandlerBuilder<Event> builder = readSide.builder("0");

            builder.setGlobalPrepare(this::createTable);

            //builder.setPrepare()
            builder.setPrepare(tag -> prepareWriteTitle());

            builder.setEventHandler(Event.CustomerCreated.class, this::processPostAdded);

            return builder.build();
        }

        private CompletionStage<List<BoundStatement>> processPostAdded(Event.CustomerCreated event) {

            return completedStatements(Arrays.asList(writeTitle.bind(event.getCustomer().getUUID(),
                    event.getCustomer().getEmail(),
                    event.getCustomer().getPassword(),
                    event.getCustomer().getName(),
                    event.getCustomer().getSurname())));
        }

        @Override
        public PSequence<AggregateEventTag<Event>> aggregateTags() {
            // TODO return the tag for the events
            return Event.TAG.allTags();
        }

        private CompletionStage<Done> createTable() {
            return session.executeCreateTable("CREATE TABLE IF NOT EXISTS customer_auth ( " +
            "id UUID, email TEXT, password TEXT, name TEXT, surname TEXT, PRIMARY KEY (id))");
        }

    }
}