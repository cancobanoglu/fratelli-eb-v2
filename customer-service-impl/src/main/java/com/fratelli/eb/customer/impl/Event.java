package com.fratelli.eb.customer.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 * Created by caniven on 25/11/2017.
 */
public interface Event extends Jsonable, AggregateEvent<Event> {

  Customer getCustomer();

  AggregateEventShards<Event> TAG = AggregateEventTag.sharded(Event.class, 4);

  @Override
  default AggregateEventTagger<Event> aggregateTag() {
    return TAG;
  }

  @Value
  @JsonDeserialize
  final class CustomerCreated implements Event {
    public Customer customer;

    public CustomerCreated(Customer customer) {
      this.customer = customer;
    }

    @Override
    public Customer getCustomer() {
      return customer;
    }
  }
}
