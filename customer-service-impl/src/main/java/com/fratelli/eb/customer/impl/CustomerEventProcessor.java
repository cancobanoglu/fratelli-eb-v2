package com.fratelli.eb.customer.impl;


import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

public class CustomerEventProcessor extends ReadSideProcessor<Event> {

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
        BoundStatement bindWriteTitle = writeTitle.bind();
        bindWriteTitle.setString("id", event.getCustomer().getUUID());
        bindWriteTitle.setString("email", event.getCustomer().getEmail());
        bindWriteTitle.setString("password", event.getCustomer().getPassword());
        bindWriteTitle.setString("name", event.getCustomer().getName());
        bindWriteTitle.setString("surname", event.getCustomer().getSurname());

        return completedStatements(Arrays.asList(bindWriteTitle));
    }

    @Override
    public PSequence<AggregateEventTag<Event>> aggregateTags() {
        // TODO return the tag for the events
        return Event.TAG.allTags();
    }

    private CompletionStage<Done> createTable() {
        return session.executeCreateTable("CREATE TABLE IF NOT EXISTS customer_auth ( " +
        "id TEXT, email TEXT, password TEXT, name TEXT, surname TEXT, PRIMARY KEY (id))");
    }
}