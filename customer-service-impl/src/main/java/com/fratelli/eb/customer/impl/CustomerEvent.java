package com.fratelli.eb.customer.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fratelli.eb.customer.api.Customer;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 * Created by caniven on 25/11/2017.
 */
public interface CustomerEvent extends Jsonable, AggregateEvent<CustomerEvent> {

  Customer getCustomer();

  AggregateEventShards<CustomerEvent> TAG = AggregateEventTag.sharded(CustomerEvent.class, 4);

  @Override
  default AggregateEventTagger<CustomerEvent> aggregateTag() {
    return TAG;
  }

  @Value
  @JsonDeserialize
  final class CustomerCreated implements CustomerEvent {
    public Customer customer;

    public CustomerCreated(Customer customer) {
      this.customer = customer;
    }

    @Override
    public Customer getCustomer() {
      return customer;
    }
  }

  @Value
  @JsonDeserialize
  final class CustomerSmsVerified implements CustomerEvent {
    public Customer customer;

    public CustomerSmsVerified(Customer customer) {
      this.customer = customer;
    }

    @Override
    public Customer getCustomer() {
      return customer;
    }
  }
}
