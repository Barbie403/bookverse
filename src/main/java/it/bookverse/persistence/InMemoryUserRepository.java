//Concrete Product A1

package it.bookverse.persistence;

import it.bookverse.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository
        implements UserRepository {

    private final List<User> users =
            new ArrayList<>();

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public void update(User updatedUser) {
        for (int index = 0;
             index < users.size();
             index++) {

            User existingUser =
                    users.get(index);

            if (existingUser.getId()
                    .equals(updatedUser.getId())) {

                users.set(
                        index,
                        updatedUser
                );

                return;
            }
        }

        throw new IllegalArgumentException(
                "User not found: "
                        + updatedUser.getId()
        );
    }

    @Override
    public Optional<User> findByEmail(
            String email
    ) {
        return users.stream()
                .filter(user ->
                        user.getEmail()
                                .equalsIgnoreCase(email)
                )
                .findFirst();
    }

    @Override
    public Optional<User> findById(
            String id
    ) {
        return users.stream()
                .filter(user ->
                        user.getId().equals(id)
                )
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }
}