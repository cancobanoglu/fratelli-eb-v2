package com.fratelli.eb.customer.impl;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

/**
 * Created by caniven on 25/11/2017.
 */
public interface Command {

  @Value
  @JsonDeserialize
  final class CreateCustomer implements Command, CompressedJsonable, PersistentEntity.ReplyType<Done> {
    public String name;
    public String surname;
    public String email;
    public String password;
  }

}
