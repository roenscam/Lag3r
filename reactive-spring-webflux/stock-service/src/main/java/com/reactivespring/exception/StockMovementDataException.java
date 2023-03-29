package com.reactivespring.exception;

public class StockMovementDataException extends RuntimeException {
    private String message;
    public StockMovementDataException(String s) {
        super(s);
        this.message=s;
    }
}
