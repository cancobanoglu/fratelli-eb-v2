package com.fratelli.eb.customer.impl;

import akka.Done;
import com.fratelli.eb.customer.api.Customer;
import com.fratelli.eb.customer.api.Status;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerEntity extends PersistentEntity<CustomerCommand, CustomerEvent, CustomerState> {
    private final Logger log = LoggerFactory.getLogger(CustomerEntity.class);

    @Override
    public Behavior initialBehavior(Optional<CustomerState> snapshotState) {

        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(CustomerState.initial()));

        builder.setCommandHandler(CustomerCommand.CreateCustomer.class, (cmd, ctx) -> {

            Customer customer = new Customer(
                    UUID.fromString(entityId()),
                    cmd.getName(),
                    cmd.getSurname(),
                    cmd.getEmail(),
                    cmd.getPassword(),
                    Status.PENDING,
                    cmd.activationCode);

            CustomerEvent.CustomerCreated customerCreated = new CustomerEvent.CustomerCreated(customer);
            return ctx.thenPersist(customerCreated, (e) -> ctx.reply(customer.getUUID().toString()));
        });

        builder.setEventHandler(CustomerEvent.CustomerCreated.class,
                (evt) -> new CustomerState(Optional.of(evt.customer), Instant.now()));

        builder.setReadOnlyCommandHandler(CustomerCommand.GetCustomer.class, (cmd, ctx) -> ctx.reply(state().customer));

        return builder.build();
    }
}
