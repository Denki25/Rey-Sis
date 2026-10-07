package org.example.auth;

import org.example.data.UserRepository;
import org.example.model.User;

import java.sql.SQLException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this(new UserRepository());
    }

    public AuthService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public AuthenticationResult authenticate(String username, char[] password) throws SQLException {
        String validationMessage = LoginValidator.validate(username, password);
        if (validationMessage != null) {
            return AuthenticationResult.invalid(validationMessage);
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null || !verifyPassword(password, user.getPasswordHash())) {
            return AuthenticationResult.invalid("Invalid username or password.");
        }
        if (!user.isActive()) {
            return AuthenticationResult.inactive();
        }
        return AuthenticationResult.success(user);
    }

    private boolean verifyPassword(char[] password, String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        if (!passwordHash.startsWith("pbkdf2$")) {
            return MessageDigest.isEqual(
                    passwordHash.getBytes(StandardCharsets.UTF_8),
                    new String(password).getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = passwordHash.split("\\$", -1);
            if (parts.length != 4) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            PBEKeySpec specification = new PBEKeySpec(password, salt, iterations, expected.length * 8);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(specification).getEncoded();
            return MessageDigest.isEqual(expected, actual);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            return false;
        }
    }

    public record AuthenticationResult(Status status, User user, String message) {
        public enum Status { SUCCESS, INVALID, INACTIVE }

        static AuthenticationResult success(User user) {
            return new AuthenticationResult(Status.SUCCESS, user, null);
        }

        static AuthenticationResult invalid(String message) {
            return new AuthenticationResult(Status.INVALID, null, message);
        }

        static AuthenticationResult inactive() {
            return new AuthenticationResult(Status.INACTIVE, null, "Your account is inactive.");
        }

        public boolean isSuccessful() {
            return status == Status.SUCCESS;
        }
    }
}
