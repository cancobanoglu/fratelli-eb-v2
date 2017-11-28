package com.fratelli.eb.customer.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerEntity extends PersistentEntity<Command, Event, State> {
    private final Logger log = LoggerFactory.getLogger(CustomerEntity.class);
  @Override
  public Behavior initialBehavior(Optional<State> snapshotState) {

    BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(State.initial()));

    builder.setCommandHandler(Command.CreateCustomer.class, (cmd, ctx) -> {


      Customer customer = new Customer(
          UUID.fromString(entityId()),
          cmd.getName(),
          cmd.getSurname(),
          cmd.getEmail(),
          cmd.getPassword()
      );

      Event.CustomerCreated customerCreated = new Event.CustomerCreated(customer);
      return ctx.thenPersist(customerCreated, (e) -> ctx.reply(Done.getInstance()));
    });

    builder.setEventHandler(Event.CustomerCreated.class, (evt) -> new State(Optional.of(evt.customer), Instant.now()));

    return builder.build();
  }
}
