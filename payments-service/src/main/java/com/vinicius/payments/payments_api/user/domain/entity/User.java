package com.vinicius.payments.payments_api.user.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {



    private UUID id;
    private String name;
    private String email;
    private String login;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, String name, String email, String login, String password, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String name, String email, String login, String password) {
        LocalDateTime now = LocalDateTime.now();
        return new User(null, name, email, login, password, now, now);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String newName, String newEmail, String newLogin) {
        this.name = newName;
        this.email = newEmail;
        this.login = newLogin;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
