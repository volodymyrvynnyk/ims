package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class UserOrPasswordIncorrectException extends BaseRuntimeException {

    public UserOrPasswordIncorrectException(String message) {
        super(message);
    }

    public UserOrPasswordIncorrectException() {
    }

    public UserOrPasswordIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserOrPasswordIncorrectException(Throwable cause) {
        super(cause);
    }
}
