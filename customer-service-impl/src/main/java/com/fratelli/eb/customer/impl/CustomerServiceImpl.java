package com.fratelli.eb.customer.impl;

import akka.Done;
import akka.NotUsed;
import com.fratelli.eb.customer.api.*;
import com.fratelli.eb.customer.impl.exception.SmsCodeNotMatchException;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.ExceptionMessage;
import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.api.transport.TransportErrorCode;
import com.lightbend.lagom.javadsl.api.transport.TransportException;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final PersistentEntityRegistry registry;
    private final CustomerRepository customerRepository;

    @Inject
    public CustomerServiceImpl(PersistentEntityRegistry registry, CustomerRepository customerRepository) {
        this.registry = registry;
        this.customerRepository = customerRepository;

        registry.register(CustomerEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, GetCustomerResponse> getCustomer(UUID customerId) {
        return request ->
                entityRef(customerId)
                        .ask(CustomerCommand.GetCustomer.INSTANCE)
                        .thenApply(
                                c -> {
                                    GetCustomerResponse response = new GetCustomerResponse();
                                    response.customer = ((Optional<Customer>) c).get();
                                    return response;
                                }
                        );
    }

    @Override
    public ServiceCall<VerifyCustomerSmsRequest, VerifyCustomerSmsResponse> verifySmsCode() {
        return request -> {

            PersistentEntityRef<CustomerCommand> customerEntityRef = entityRef(request.customerId);
            CustomerCommand.VerifyActivationCode command = new CustomerCommand.VerifyActivationCode(request.smsCode);
            return customerEntityRef.ask(command)
                    .handle((userId, exception) -> {
                        if (exception != null) {
                            if (exception.equals(SmsCodeNotMatchException.SMS_CODE_NOT_MATCH_EXCEPTION)) {
                                // don't disclose cause of error.
                                throw new Forbidden("UpdateFailed");
                            } else {
                                throw new TransportException(TransportErrorCode.fromHttp(409),
                                        new ExceptionMessage("VerificationFailed", exception.getMessage()));
                            }
                        } else {
                            return new VerifyCustomerSmsResponse((String)userId);
                        }
                    });

        };
    }

    @Override
    public ServiceCall<CreateCustomerRequest, CreateCustomerResponse> createCustomer() {
        return createCustomerRequest -> {
            log.info("createCustomer returns done...");
            String entityId = UUID.randomUUID().toString();

            // generate sms code

//            log.info("createCustomer returns done..." + id.toString());
            PersistentEntityRef<CustomerCommand> customerEntityRef = this.registry.refFor(CustomerEntity.class, entityId);

            CustomerCommand.CreateCustomer command = new CustomerCommand.CreateCustomer(
                    createCustomerRequest.name,
                    createCustomerRequest.surname,
                    createCustomerRequest.email,
                    createCustomerRequest.password,
                    SmsCodeUtil.generateCode()
            );

            return customerEntityRef.ask(command).thenApply(resp -> new CreateCustomerResponse(entityId));
        };
    }

    private PersistentEntityRef<CustomerCommand> entityRef(UUID id) {
        return entityRef(id.toString());
    }

    private PersistentEntityRef<CustomerCommand> entityRef(String id) {
        return registry.refFor(CustomerEntity.class, id);
    }

}
