//Abstract Product A

package it.bookverse.persistence;

import it.bookverse.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    void update(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    List<User> findAll();
}