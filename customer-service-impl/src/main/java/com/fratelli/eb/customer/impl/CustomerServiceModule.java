package com.fratelli.eb.customer.impl;

import com.fratelli.eb.customer.api.CustomerService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerServiceModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(CustomerService.class, CustomerServiceImpl.class);
    bind(CustomerRepository.class);
  }
}
