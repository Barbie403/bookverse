//helper

package it.bookverse.persistence;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

final class TextFileSupport {

    private static final String NULL_VALUE = "~";

    private TextFileSupport() {
    }

    static void ensureFileExists(
            Path filePath
    ) {
        try {
            Path parentDirectory =
                    filePath.getParent();

            if (parentDirectory != null) {
                Files.createDirectories(
                        parentDirectory
                );
            }

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot create persistence file: "
                            + filePath,
                    exception
            );
        }
    }

    static List<String> readAllLines(
            Path filePath
    ) {
        ensureFileExists(filePath);

        try {
            return Files.readAllLines(
                    filePath,
                    StandardCharsets.UTF_8
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot read persistence file: "
                            + filePath,
                    exception
            );
        }
    }

    static void writeAllLines(
            Path filePath,
            List<String> lines
    ) {
        ensureFileExists(filePath);

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot write persistence file: "
                            + filePath,
                    exception
            );
        }
    }

    static String encode(
            String value
    ) {
        if (value == null) {
            return NULL_VALUE;
        }

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    static String decode(
            String value
    ) {
        if (NULL_VALUE.equals(value)) {
            return null;
        }

        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }
}