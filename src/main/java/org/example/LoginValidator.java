package org.example;

public final class LoginValidator {
    private LoginValidator() {
    }

    public static String validate(String username, char[] password) {
        if (username == null || username.isBlank()) {
            return "Please enter your username.";
        }
        if (password == null || password.length == 0) {
            return "Please enter your password.";
        }
        return null;
    }
}
