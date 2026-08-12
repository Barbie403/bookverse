package it.bookverse.controller;

import it.bookverse.entity.User;
import it.bookverse.exception.InvalidCredentialsException;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;

public class LoginController {

    private final UserRepository userRepository;
    private final UserSession userSession;

    public LoginController(
            UserRepository userRepository,
            UserSession userSession
    ) {
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            throw new InvalidCredentialsException();
        }

        User user = userRepository
                .findByEmail(email.trim())
                .filter(foundUser ->
                        foundUser.getPassword().equals(password)
                )
                .orElseThrow(InvalidCredentialsException::new);

        userSession.start(user);

        return user;
    }

    public void logout() {
        userSession.close();
    }
}