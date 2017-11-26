package com.fratelli.eb.customer.api;

import akka.Done;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.UUID;

import static com.lightbend.lagom.javadsl.api.Service.named;

/**
 * Created by caniven on 25/11/2017.
 */
public interface CustomerService extends Service {

  ServiceCall<CreateCustomerRequest, Done> createCustomer();

  @Override
  default Descriptor descriptor() {
    return named("customer").withCalls(
        Service.restCall(Method.POST, "/api/v1/customers", this::createCustomer)
    ).withPathParamSerializer(
        UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString)
    ).withAutoAcl(true);
  }
}
