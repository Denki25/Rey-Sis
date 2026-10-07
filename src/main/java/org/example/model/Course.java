package org.example.model;

public final class Course {
    private final String code;
    private final String title;
    private final int units;

    public Course(String code, String title) {
        this(code, title, 0);
    }

    public Course(String code, String title, int units) {
        this.code = code;
        this.title = title;
        this.units = units;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getUnits() { return units; }
}
