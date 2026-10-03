package org.horizonteurbano.system.service;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public boolean register(User user, String rawPassword) {
        if (user == null || isBlank(rawPassword)) return false;
        user.setPassword(hashPassword(rawPassword));
        return userRepository.saveUser(user);
    }

    public boolean updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public User login(String identifier, String rawPassword) {
        if (isBlank(identifier) || isBlank(rawPassword)) return null;
        User stored = userRepository.getUserByIdentifier(identifier);
        if (stored == null || !checkPassword(rawPassword, stored.getPassword())) return null;
        return stored;
    }

    public boolean changePassword(String email, String newRawPassword) {
        if (isBlank(email) || isBlank(newRawPassword)) return false;
        User user = userRepository.getUserByEmail(email);
        if (user == null) return false;
        return userRepository.updatePassword(user.getIdUser(), hashPassword(newRawPassword));
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

    private static final java.util.regex.Pattern EMAIL_PATTERN = java.util.regex.Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public boolean isValidEmail(String email) {
        return email != null && email.trim().length() <= 40 && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}