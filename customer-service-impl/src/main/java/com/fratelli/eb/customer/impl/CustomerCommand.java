package com.fratelli.eb.customer.impl;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fratelli.eb.customer.api.Customer;
import com.fratelli.eb.customer.api.CustomerBasic;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

import java.util.Optional;

/**
 * Created by caniven on 25/11/2017.
 */
public interface CustomerCommand {

    @Value
    @JsonDeserialize
    final class CreateCustomer implements CustomerCommand, CompressedJsonable, PersistentEntity.ReplyType<String> {
        public String name;
        public String surname;
        public String email;
        public String password;
        public String activationCode;

        public CreateCustomer(String name, String surname, String email, String password, String activationCode) {
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.password = password;
            this.activationCode = null;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    @JsonDeserialize
    enum GetCustomer implements CustomerCommand, CompressedJsonable, PersistentEntity.ReplyType<Optional<Customer>> {
        INSTANCE
    }


    @Value
    @JsonDeserialize
    final class VerifyActivationCode implements CustomerCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public String code;
    }
}
