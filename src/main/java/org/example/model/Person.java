package org.example.model;

public abstract class Person {
    private final String identifier;
    private String name;

    protected Person(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public abstract String getRoleLabel();
}
