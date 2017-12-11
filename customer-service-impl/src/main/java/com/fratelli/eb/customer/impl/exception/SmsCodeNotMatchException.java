package com.fratelli.eb.customer.impl.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;

public class SmsCodeNotMatchException extends RuntimeException implements Jsonable {

    public static final SmsCodeNotMatchException SMS_CODE_NOT_MATCH_EXCEPTION
            = new SmsCodeNotMatchException("Sms Code Not Matched Exception");

    private String message;

    @JsonCreator
    public SmsCodeNotMatchException(String message) {
        super(message);
        this.message = message;
    }


    // ------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsCodeNotMatchException that = (SmsCodeNotMatchException) o;

        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }
}
