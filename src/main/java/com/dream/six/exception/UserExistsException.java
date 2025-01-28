package com.dream.six.exception;

import java.io.Serial;

public class UserExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserExistsException(String message){
        super(message);
    }
}
