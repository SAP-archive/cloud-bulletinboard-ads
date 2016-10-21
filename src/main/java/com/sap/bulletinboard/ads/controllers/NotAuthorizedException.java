package com.sap.bulletinboard.ads.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAuthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotAuthorizedException(String message) {
        super(message);
    }
}
