package com.fratelli.eb.customer.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import java.util.UUID;

/**
 * Created by caniven on 25/11/2017.
 */
@Value
public class Customer {
  private final java.util.UUID UUID;
  private final String name;
  private final String surname;
  private final String email;
  private final String password;

  public static Customer newState() {
    return new Customer();
  }

  @JsonCreator
  public Customer() {
    this.UUID = null;
    this.name = null;
    this.surname = null;
    this.email = null;
    this.password = null;
  }

  public Customer(UUID UUID, String name, String surname, String email, String password) {
    this.UUID = UUID;
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
  }

  public UUID getUUID() {
    return UUID;
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
