package com.fratelli.eb.customer.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerEntity extends PersistentEntity<Command, Event, State> {

  @Override
  public Behavior initialBehavior(Optional<State> snapshotState) {

    BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(State.initial()));

    builder.setCommandHandler(Command.CreateCustomer.class, (cmd, ctx) -> {

      Customer customer = new Customer(
          entityId(),
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
