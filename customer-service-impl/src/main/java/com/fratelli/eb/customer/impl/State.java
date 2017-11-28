package com.fratelli.eb.customer.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by caniven on 25/11/2017.
 */
@SuppressWarnings("serial")
@Value
@Builder
@JsonDeserialize
public class State implements Jsonable {

  public Optional<Customer> customer;
  public Instant timestamp;

  private static final long serialVersionUID = 1L;

  public static State initial() {
    return new State(Optional.of(Customer.newState()), null);
  }

  public static State empty() {
    return new State(Optional.of(Customer.newState()), null);
  }

  public State(Optional<Customer> customer, Instant timestamp) {
    this.customer = customer;
    this.timestamp = timestamp;
  }
}
