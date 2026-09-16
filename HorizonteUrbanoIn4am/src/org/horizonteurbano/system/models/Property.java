package org.horizonteurbano.system.models;

import java.time.LocalDateTime;

public class Property {

    private int idProperty;
    private String internalCode;
    private PropertyType type;
    private String address;
    private State state;
    private double area;
    private double price;
    private boolean active;
    private LocalDateTime dateRegister;
    private LocalDateTime updateDate;
    private String imageUrl;
    private String idUser;

    public Property() {
    }

    public Property(int idProperty, String internalCode, PropertyType type, String address,
            State state, double area, double price, boolean active,
            LocalDateTime dateRegister, LocalDateTime updateDate,
            String imageUrl, String idUser) {
        this.idProperty = idProperty;
        this.internalCode = internalCode;
        this.type = type;
        this.address = address;
        this.state = state;
        this.area = area;
        this.price = price;
        this.active = active;
        this.dateRegister = dateRegister;
        this.updateDate = updateDate;
        this.imageUrl = imageUrl;
        this.idUser = idUser;
    }

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
