package org.horizonteurbano.system.models;

public class State {

    private int idState;
    private String nameState;

    public State() {
    }

    @Override
    public String toString() {
        return nameState;
    }

    public int getIdState() {
        return idState;
    }

    public void setIdState(int idState) {
        this.idState = idState;
    }

    public String getNameState() {
        return nameState;
    }

    public void setNameState(String nameState) {
        this.nameState = nameState;
    }
}
