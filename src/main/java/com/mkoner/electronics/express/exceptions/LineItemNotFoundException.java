package com.mkoner.electronics.express.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class LineItemNotFoundException extends Exception{
    public LineItemNotFoundException (String message){
        super(message);
    }
}
