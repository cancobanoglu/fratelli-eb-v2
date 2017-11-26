package com.fratelli.eb.customer.impl;

import akka.Done;
import com.fratelli.eb.customer.api.CreateCustomerRequest;
import com.fratelli.eb.customer.api.CustomerService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerServiceImpl implements CustomerService {

  private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
  private final PersistentEntityRegistry registry;

  @Inject
  public CustomerServiceImpl(PersistentEntityRegistry registry) {
    this.registry = registry;
    registry.register(CustomerEntity.class);
  }

  @Override
  public ServiceCall<CreateCustomerRequest, Done> createCustomer() {
    return createCustomerRequest -> {
      log.info("createCustomer returns done...");
      PersistentEntityRef<Command> customerEntityRef = this.registry.refFor(CustomerEntity.class, UUID.randomUUID().toString());

      Command.CreateCustomer command = new Command.CreateCustomer(
          createCustomerRequest.name,
          createCustomerRequest.surname,
          createCustomerRequest.email,
          createCustomerRequest.password
      );

      return customerEntityRef.ask(command);
    };
  }
}
