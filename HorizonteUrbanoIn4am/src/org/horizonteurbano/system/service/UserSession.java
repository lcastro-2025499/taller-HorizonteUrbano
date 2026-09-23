package org.horizonteurbano.system.service;

import org.horizonteurbano.system.models.User;

public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public int getCurrentRoleId() {
        if (currentUser == null || currentUser.getRol() == null) {
            return -1;
        }
        return currentUser.getRol().getIdRole();
    }

    public String getCurrentUserName() {
        return currentUser == null ? "" : currentUser.getName();
    }

    public void logout() {
        this.currentUser = null;
    }
}
