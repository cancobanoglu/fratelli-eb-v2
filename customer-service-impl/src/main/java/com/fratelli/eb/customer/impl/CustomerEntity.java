package com.fratelli.eb.customer.impl;

import akka.Done;
import com.fratelli.eb.customer.api.Customer;
import com.fratelli.eb.customer.api.Status;
import com.fratelli.eb.customer.impl.exception.SmsCodeNotMatchException;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by caniven on 25/11/2017.
 */
public class CustomerEntity extends PersistentEntity<CustomerCommand, CustomerEvent, CustomerState> {
    private final Logger log = LoggerFactory.getLogger(CustomerEntity.class);

    @Override
    public Behavior initialBehavior(Optional<CustomerState> snapshotState) {

        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(CustomerState.initial()));

        if(builder.getState().customer.get().getUUID() == null) {
            return notCreated(builder);
        } else if(builder.getState().customer.get().getStatus() == Status.PENDING) {
            return notVerified(builder);
        }

        return builder.build();
    }

    private Behavior notCreated(BehaviorBuilder b) {

        b.setCommandHandler(CustomerCommand.CreateCustomer.class, (cmd, ctx) -> {

            Customer customer = new Customer(
                    UUID.fromString(entityId()),
                    cmd.getName(),
                    cmd.getSurname(),
                    cmd.getEmail(),
                    cmd.getPassword(),
                    Status.PENDING,
                    cmd.activationCode);

            CustomerEvent.CustomerCreated customerCreated = new CustomerEvent.CustomerCreated(customer);
            return ctx.thenPersist(customerCreated, (e) -> ctx.reply(customer.getUUID().toString()));
        });

        b.setReadOnlyCommandHandler(CustomerCommand.GetCustomer.class, (cmd, ctx) -> ctx.reply(state().customer));


        b.setEventHandler(CustomerEvent.CustomerCreated.class,
                (evt) -> new CustomerState(Optional.of(evt.customer), Instant.now()));

        b.setReadOnlyCommandHandler(CustomerCommand.VerifyActivationCode.class, (cmd, ctx) -> ctx.reply(state().customer.get().getUUID().toString()));

        return b.build();
    }
    private Behavior notVerified(BehaviorBuilder b) {

        b.setReadOnlyCommandHandler(CustomerCommand.CreateCustomer.class, (cmd, ctx) -> {
            ctx.invalidCommand("User already exists.");
        });

        b.setReadOnlyCommandHandler(CustomerCommand.GetCustomer.class, (cmd, ctx) -> ctx.reply(state().customer));

        b.setCommandHandler(CustomerCommand.VerifyActivationCode.class, (cmd, ctx) -> {

            Customer customer = state().customer.get();

            if(customer.getActivationCode().equals(cmd.code)) {

                Customer copyCustomer = new Customer(
                        customer.getUUID(),
                        customer.getName(),
                        customer.getSurname(),
                        customer.getEmail(),
                        customer.getPassword(),
                        Status.ACTIVE,
                        customer.getActivationCode());

                CustomerEvent.CustomerSmsVerified customerSmsVerified = new CustomerEvent.CustomerSmsVerified(copyCustomer);

                return ctx.thenPersist(customerSmsVerified, (e) -> ctx.reply(copyCustomer.getUUID().toString()));
            }

            ctx.commandFailed(SmsCodeNotMatchException.SMS_CODE_NOT_MATCH_EXCEPTION);

            return ctx.done();
        });

        return b.build();
    }
}
