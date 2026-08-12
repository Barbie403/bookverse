package it.bookverse.exception;

public class UnauthorizedBookRemovalException
        extends RuntimeException {

    public UnauthorizedBookRemovalException() {
        super(
                "You can remove only books uploaded by you."
        );
    }
}