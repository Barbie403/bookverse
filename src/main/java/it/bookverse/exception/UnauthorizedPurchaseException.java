package it.bookverse.exception;

public class UnauthorizedPurchaseException
        extends RuntimeException {

    public UnauthorizedPurchaseException() {
        super("Only readers can purchase books.");
    }
}