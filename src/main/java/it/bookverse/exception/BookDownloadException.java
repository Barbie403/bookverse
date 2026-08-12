package it.bookverse.exception;

public class BookDownloadException extends RuntimeException {

    public BookDownloadException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}