package org.example.model;

public final class User extends Person {
    private final int userId;
    private final String passwordHash;
    private final String role;
    private final boolean active;

    public User(int userId, String username, String passwordHash, String role, boolean active) {
        super(Integer.toString(userId), username);
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return getName(); }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }

    @Override
    public String getRoleLabel() {
        return role == null ? "Unknown" : role;
    }
}
