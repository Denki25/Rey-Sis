package org.example.model;

public class Cashier extends Person {
    public Cashier(String name, String id) {
        super(id, name);
    }

    public String getId() {
        return getIdentifier();
    }

    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getRoleLabel() {
        return "Cashier";
    }
}