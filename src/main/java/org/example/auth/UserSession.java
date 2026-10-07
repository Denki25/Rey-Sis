package org.example.auth;

import org.example.model.User;

public final class UserSession {
    private static User currentUser;

    private UserSession() {
    }

    public static void start(User user) {
        currentUser = new User(user.getUserId(), user.getUsername(), null, user.getRole(), user.isActive());
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
