package it.bookverse.entity;

import java.util.Objects;

public abstract class User {

    private final String id;
    private String fullName;
    private final String email;
    private String password;

    protected User(
            String id,
            String fullName,
            String email,
            String password
    ) {
        this.id = Objects.requireNonNull(id);
        this.fullName = Objects.requireNonNull(fullName);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setFullName(String fullName) {
        this.fullName = Objects.requireNonNull(fullName);
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password);
    }

    public abstract Role getRole();
}