package it.bookverse.exception;

public class BookAlreadyPurchasedException
        extends RuntimeException {

    public BookAlreadyPurchasedException() {
        super("You have already purchased this book.");
    }
}