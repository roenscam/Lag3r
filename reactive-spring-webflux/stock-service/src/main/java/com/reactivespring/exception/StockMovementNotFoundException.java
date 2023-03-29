package com.reactivespring.exception;

public class StockMovementNotFoundException extends RuntimeException{

    private String message;
    private Throwable ex;

    public StockMovementNotFoundException(String message, Throwable ex) {
        super(message, ex);
        this.message = message;
        this.ex = ex;
    }

    public StockMovementNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
