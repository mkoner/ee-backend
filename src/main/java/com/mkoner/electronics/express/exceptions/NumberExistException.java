package com.mkoner.electronics.express.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NumberExistException extends Exception{
    public NumberExistException(String message){
        super((message));
    }
}
