package it.bookverse.session;

import it.bookverse.entity.User;

import java.util.Optional;

public class UserSession {

    private User currentUser;

    public void start(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "The authenticated user cannot be null."
            );
        }

        currentUser = user;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void close() {
        currentUser = null;
    }
}