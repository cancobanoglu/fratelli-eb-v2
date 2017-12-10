package com.fratelli.eb.customer.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import java.util.UUID;

/**
 * Created by caniven on 25/11/2017.
 */
@Value
public class Customer extends CustomerBasic {

    private final Status status;
    private final String activationCode;

    public static Customer newState() {
        return new Customer();
    }

    @JsonCreator
    public Customer() {
        super();
        this.status = Status.PENDING;
        this.activationCode = null;
    }

    public Customer(UUID uuid, String name, String surname, String email, String password, Status status, String activationCode) {
        super(uuid, name, surname, email, password);
        this.status = status;
        this.activationCode = activationCode;
    }
}
