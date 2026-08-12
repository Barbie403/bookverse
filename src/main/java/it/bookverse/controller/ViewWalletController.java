package it.bookverse.controller;

import it.bookverse.entity.Reader;
import it.bookverse.entity.Role;
import it.bookverse.entity.User;
import it.bookverse.entity.Wallet;
import it.bookverse.entity.Writer;
import it.bookverse.session.UserSession;

import java.math.BigDecimal;

public class ViewWalletController {

    private final UserSession userSession;

    public ViewWalletController(
            UserSession userSession
    ) {
        this.userSession = userSession;
    }

    public WalletDetails viewWallet() {
        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        Wallet wallet = getWallet(currentUser);

        return new WalletDetails(
                currentUser.getFullName(),
                currentUser.getRole(),
                wallet.getBalance()
        );
    }

    private Wallet getWallet(User user) {
        if (user instanceof Reader reader) {
            return reader.getWallet();
        }

        if (user instanceof Writer writer) {
            return writer.getWallet();
        }

        throw new IllegalStateException(
                "The current user does not have a wallet."
        );
    }

    public record WalletDetails(
            String fullName,
            Role role,
            BigDecimal balance
    ) {
    }
}