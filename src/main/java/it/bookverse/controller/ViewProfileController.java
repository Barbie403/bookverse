package it.bookverse.controller;

import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.session.UserSession;

import java.time.LocalDate;

public class ViewProfileController {

    private final UserSession userSession;

    public ViewProfileController(
            UserSession userSession
    ) {
        this.userSession = userSession;
    }

    public ProfileDetails viewProfile() {
        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        if (currentUser instanceof Reader reader) {
            return createReaderProfile(reader);
        }

        if (currentUser instanceof Writer writer) {
            return createWriterProfile(writer);
        }

        throw new IllegalStateException(
                "The current user has an unsupported role."
        );
    }

    private ReaderProfileDetails createReaderProfile(
            Reader reader
    ) {
        return new ReaderProfileDetails(
                reader.getFullName(),
                reader.getEmail(),
                reader.getBirthDate(),
                reader.isMinor()
                        ? "Minor"
                        : "Adult"
        );
    }

    private WriterProfileDetails createWriterProfile(
            Writer writer
    ) {
        return new WriterProfileDetails(
                writer.getFullName(),
                writer.getEmail(),
                writer.getPenName(),
                writer.getBio(),
                formatWebsite(writer.getWebsiteOrSocial())
        );
    }

    private String formatWebsite(
            String websiteOrSocial
    ) {
        if (websiteOrSocial == null
                || websiteOrSocial.isBlank()) {

            return "Not provided";
        }

        return websiteOrSocial;
    }

    /*
     * Common output type of the View Profile use case.
     */
    public sealed interface ProfileDetails
            permits ReaderProfileDetails,
            WriterProfileDetails {
    }

    public record ReaderProfileDetails(
            String fullName,
            String email,
            LocalDate birthDate,
            String accountType
    ) implements ProfileDetails {
    }

    public record WriterProfileDetails(
            String fullName,
            String email,
            String penName,
            String biography,
            String websiteOrSocial
    ) implements ProfileDetails {
    }
}