package com.fratelli.eb.customer.api;

public enum Status {
    PENDING, // sms not verified, but user created
    ACTIVE, // sms verified by user
    LOCKED // user can be locked according to business cases
}
