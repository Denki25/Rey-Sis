package org.example.ui.views.teacher;

import org.example.service.TeacherGradeService;
import org.example.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public final class TeacherDashboardFrame {
    private final JFrame window = new JFrame("REY SIS | Teacher Grades");
    private final TeacherGradeService gradeService = new TeacherGradeService();
    private JComboBox<String> students;
    private JComboBox<String> courses;
    private JTextField grade;
    private JTextField semester;
    private JTextField academicYear;
    private JTextField remarks;

    public TeacherDashboardFrame() {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(760, 470);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel title = new JLabel("Grade Encoding");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        content.add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        students = new JComboBox<>();
        courses = new JComboBox<>();
        grade = new JTextField();
        semester = new JTextField();
        academicYear = new JTextField();
        remarks = new JTextField();
        form.add(new JLabel("Student")); form.add(students);
        form.add(new JLabel("Course")); form.add(courses);
        form.add(new JLabel("Grade (1.00 - 5.00)")); form.add(grade);
        form.add(new JLabel("Semester")); form.add(semester);
        form.add(new JLabel("Academic year")); form.add(academicYear);
        form.add(new JLabel("Remarks")); form.add(remarks);
        content.add(form, BorderLayout.CENTER);
        JButton save = new JButton("Save Grade");
        save.addActionListener(event -> saveGrade());
        JButton signOut = new JButton("Sign Out");
        signOut.addActionListener(event -> { window.dispose(); LoginFrame login = new LoginFrame(); login.showWindow(); });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(signOut); actions.add(save);
        content.add(actions, BorderLayout.SOUTH);
        loadOptions();
        return content;
    }

    private void loadOptions() {
        try {
            for (String value : gradeService.students()) students.addItem(value);
            for (String value : gradeService.courses()) courses.addItem(value);
        } catch (SQLException | SecurityException exception) {
            showError("Unable to load teacher data.");
        }
    }

    private void saveGrade() {
        try {
            String studentValue = String.valueOf(students.getSelectedItem());
            String courseValue = String.valueOf(courses.getSelectedItem());
            String studentId = studentValue.split(" \\|", 2)[0];
            String courseCode = courseValue.split(" \\|", 2)[0];
            double value = Double.parseDouble(grade.getText().trim());
            gradeService.saveGrade(studentId, courseCode, semester.getText().trim(), academicYear.getText().trim(), value, remarks.getText().trim());
            JOptionPane.showMessageDialog(window, "Grade saved successfully.");
        } catch (NumberFormatException exception) {
            showError("Enter a numeric grade between 1.00 and 5.00.");
        } catch (SQLException | SecurityException exception) {
            showError("Unable to save the grade.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(window, message, "REY SIS", JOptionPane.ERROR_MESSAGE);
    }
}
