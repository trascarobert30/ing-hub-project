package com.ing.core.services.exceptions;

public class StoreNotFoundException extends RuntimeException
{
    public StoreNotFoundException(String message) {
        super(message);
    }
}
