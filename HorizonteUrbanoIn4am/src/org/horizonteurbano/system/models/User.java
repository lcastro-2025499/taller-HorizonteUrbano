package org.horizonteurbano.system.models;

import java.time.LocalDateTime;

public class User {

    private String idUser;
    private String name;
    private String lastName;
    private String userName;
    private String email;
    private Role rol;
    private String phone;
    private String password;
    private boolean active;
    private LocalDateTime dateRegister;

    public User() {
    }

    public User(String idUser, String name, String lastName, String userName, String email,
            Role rol, String phone, String password, boolean active, LocalDateTime dateRegister) {
        this.idUser = idUser;
        this.name = name;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.rol = rol;
        this.phone = phone;
        this.password = password;
        this.active = active;
        this.dateRegister = dateRegister;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(LocalDateTime dateRegister) {
        this.dateRegister = dateRegister;
    }
}
