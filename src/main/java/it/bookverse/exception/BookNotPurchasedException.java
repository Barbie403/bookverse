package it.bookverse.exception;

public class BookNotPurchasedException extends RuntimeException {

    public BookNotPurchasedException() {
        super("You can download only books that you have purchased.");
    }
}