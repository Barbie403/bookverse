


module it.bookverse {

        requires javafx.controls;
        requires javafx.fxml;

        exports it.bookverse;
        exports it.bookverse.controller;
        exports it.bookverse.entity;
        exports it.bookverse.exception;
        exports it.bookverse.navigation;
        exports it.bookverse.payment;
        exports it.bookverse.persistence;
        exports it.bookverse.session;
        exports it.bookverse.boundary;

        opens it.bookverse.boundary to javafx.fxml;
        }