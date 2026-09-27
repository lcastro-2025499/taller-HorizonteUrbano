package org.horizonteurbano.system.service;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // Hashes the raw password and saves the user
    public boolean register(User user, String rawPassword) {
        if (user == null || isBlank(rawPassword)) {
            return false;
        }
        user.setPassword(hashPassword(rawPassword));
        return userRepository.saveUser(user);
    }

    // Returns the authenticated user or null if credentials are invalid
    public User login(String identifier, String rawPassword) {
        if (isBlank(identifier) || isBlank(rawPassword)) {
            return null;
        }
        User stored = userRepository.getUserByIdentifier(identifier);
        if (stored == null) {
            return null;
        }
        if (!checkPassword(rawPassword, stored.getPassword())) {
            return null;
        }
        return stored;
    }

    // Changes password by email (used in the "forgot password" flow)
    public boolean changePassword(String email, String newRawPassword) {
        if (isBlank(email) || isBlank(newRawPassword)) {
            return false;
        }
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            return false;
        }
        String hashed = hashPassword(newRawPassword);
        return userRepository.updatePassword(user.getIdUser(), hashed);
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
