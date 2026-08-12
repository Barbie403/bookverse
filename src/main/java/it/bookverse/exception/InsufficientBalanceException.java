package it.bookverse.exception;

public class InsufficientBalanceException
        extends RuntimeException {

    public InsufficientBalanceException() {
        super("Your wallet balance is not sufficient.");
    }
}