package com.mkoner.electronics.express.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AuthFailure extends Exception{
    public AuthFailure(String message){
        super(message);
    }
}
