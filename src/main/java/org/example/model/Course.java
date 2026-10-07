package org.example.model;

public final class Course {
    private final String code;
    private final String title;
    private final int units;
    private final String department;

    public Course(String code, String title) {
        this(code, title, 0, null);
    }

    public Course(String code, String title, int units) {
        this(code, title, units, null);
    }

    public Course(String code, String title, int units, String department) {
        this.code = code;
        this.title = title;
        this.units = units;
        this.department = department;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getUnits() { return units; }
    public String getDepartment() { return department; }
}
