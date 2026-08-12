// Concrete Product A2

package it.bookverse.persistence;

import it.bookverse.entity.Reader;
import it.bookverse.entity.Role;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextFileUserRepository
        implements UserRepository {

    private static final String SEPARATOR = "\\|";

    private final Path filePath;
    private final List<User> users;

    public TextFileUserRepository(
            Path filePath
    ) {
        this.filePath = filePath;
        this.users = new ArrayList<>();

        loadFromFile();
    }

    @Override
    public void save(User user) {
        users.add(user);
        saveToFile();
    }

    @Override
    public void update(
            User updatedUser
    ) {
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

                saveToFile();
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

    private void loadFromFile() {
        users.clear();

        List<String> lines =
                TextFileSupport.readAllLines(
                        filePath
                );

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            users.add(
                    parseUser(line)
            );
        }
    }

    private User parseUser(
            String line
    ) {
        String[] values =
                line.split(
                        SEPARATOR,
                        -1
                );

        if (values.length == 0) {
            throw invalidLine(line);
        }

        Role role;

        try {
            role = Role.valueOf(values[0]);

        } catch (IllegalArgumentException exception) {
            throw invalidLine(line);
        }

        return switch (role) {
            case READER -> parseReader(
                    values,
                    line
            );

            case WRITER -> parseWriter(
                    values,
                    line
            );
        };
    }

    private Reader parseReader(
            String[] values,
            String originalLine
    ) {
        if (values.length != 7) {
            throw invalidLine(originalLine);
        }

        try {
            return new Reader(
                    TextFileSupport.decode(values[1]),
                    TextFileSupport.decode(values[2]),
                    TextFileSupport.decode(values[3]),
                    TextFileSupport.decode(values[4]),
                    LocalDate.parse(values[5]),
                    new BigDecimal(values[6])
            );

        } catch (RuntimeException exception) {
            throw invalidLine(
                    originalLine,
                    exception
            );
        }
    }

    private Writer parseWriter(
            String[] values,
            String originalLine
    ) {
        if (values.length != 9) {
            throw invalidLine(originalLine);
        }

        try {
            Writer.WriterData writerData =
                    new Writer.WriterData(
                            TextFileSupport.decode(values[1]),
                            TextFileSupport.decode(values[2]),
                            TextFileSupport.decode(values[3]),
                            TextFileSupport.decode(values[4]),
                            TextFileSupport.decode(values[5]),
                            TextFileSupport.decode(values[6]),
                            TextFileSupport.decode(values[7]),
                            new BigDecimal(values[8])
                    );

            return new Writer(writerData);

        } catch (RuntimeException exception) {
            throw invalidLine(
                    originalLine,
                    exception
            );
        }
    }

    private void saveToFile() {
        List<String> lines = users.stream()
                .map(this::formatUser)
                .toList();

        TextFileSupport.writeAllLines(
                filePath,
                lines
        );
    }

    private String formatUser(
            User user
    ) {
        if (user instanceof Reader reader) {
            return formatReader(reader);
        }

        if (user instanceof Writer writer) {
            return formatWriter(writer);
        }

        throw new IllegalArgumentException(
                "Unsupported user type: "
                        + user.getClass().getName()
        );
    }

    private String formatReader(
            Reader reader
    ) {
        return String.join(
                "|",
                Role.READER.name(),
                TextFileSupport.encode(
                        reader.getId()
                ),
                TextFileSupport.encode(
                        reader.getFullName()
                ),
                TextFileSupport.encode(
                        reader.getEmail()
                ),
                TextFileSupport.encode(
                        reader.getPassword()
                ),
                reader.getBirthDate().toString(),
                reader.getWallet()
                        .getBalance()
                        .toPlainString()
        );
    }

    private String formatWriter(
            Writer writer
    ) {
        return String.join(
                "|",
                Role.WRITER.name(),
                TextFileSupport.encode(
                        writer.getId()
                ),
                TextFileSupport.encode(
                        writer.getFullName()
                ),
                TextFileSupport.encode(
                        writer.getEmail()
                ),
                TextFileSupport.encode(
                        writer.getPassword()
                ),
                TextFileSupport.encode(
                        writer.getPenName()
                ),
                TextFileSupport.encode(
                        writer.getBio()
                ),
                TextFileSupport.encode(
                        writer.getWebsiteOrSocial()
                ),
                writer.getWallet()
                        .getBalance()
                        .toPlainString()
        );
    }

    private IllegalStateException invalidLine(
            String line
    ) {
        return new IllegalStateException(
                "Invalid user record in file: "
                        + line
        );
    }

    private IllegalStateException invalidLine(
            String line,
            RuntimeException cause
    ) {
        return new IllegalStateException(
                "Invalid user record in file: "
                        + line,
                cause
        );
    }
}