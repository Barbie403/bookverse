package it.bookverse.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Writer extends User {

    private String penName;
    private String bio;
    private String websiteOrSocial;

    private final Wallet wallet;

    /*
     * Groups the data required to restore
     * an existing writer from persistence.
     */
    public record WriterData(
            String id,
            String fullName,
            String email,
            String password,
            String penName,
            String bio,
            String websiteOrSocial,
            BigDecimal walletBalance
    ) {
    }

    /*
     * Used when a new writer registers.
     * Every new writer starts with an empty wallet.
     */
    public Writer(
            String id,
            String fullName,
            String email,
            String password,
            String penName,
            String bio,
            String websiteOrSocial
    ) {
        this(
                new WriterData(
                        id,
                        fullName,
                        email,
                        password,
                        penName,
                        bio,
                        websiteOrSocial,
                        BigDecimal.ZERO
                )
        );
    }

    /*
     * Used when an existing writer is restored
     * from persistent storage.
     */
    public Writer(
            WriterData data
    ) {
        super(
                Objects.requireNonNull(
                        data,
                        "Writer data cannot be null."
                ).id(),
                data.fullName(),
                data.email(),
                data.password()
        );

        this.penName = Objects.requireNonNull(
                data.penName(),
                "Pen name cannot be null."
        );

        this.bio = Objects.requireNonNull(
                data.bio(),
                "Biography cannot be null."
        );

        this.websiteOrSocial =
                data.websiteOrSocial();

        this.wallet = new Wallet(
                Objects.requireNonNull(
                        data.walletBalance(),
                        "Wallet balance cannot be null."
                )
        );
    }

    @Override
    public Role getRole() {
        return Role.WRITER;
    }

    public String getPenName() {
        return penName;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsiteOrSocial() {
        return websiteOrSocial;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setPenName(
            String penName
    ) {
        this.penName = Objects.requireNonNull(
                penName,
                "Pen name cannot be null."
        );
    }

    public void setBio(
            String bio
    ) {
        this.bio = Objects.requireNonNull(
                bio,
                "Biography cannot be null."
        );
    }

    public void setWebsiteOrSocial(
            String websiteOrSocial
    ) {
        this.websiteOrSocial =
                websiteOrSocial;
    }
}