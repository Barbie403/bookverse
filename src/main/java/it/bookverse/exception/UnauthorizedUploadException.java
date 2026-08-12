package it.bookverse.exception;

public class UnauthorizedUploadException extends RuntimeException {

    public UnauthorizedUploadException() {
        super("Only writers can upload books.");
    }
}