package org.example.data;

import org.example.model.Announcement;
import org.example.model.Course;
import org.example.model.ScheduleItem;
import org.example.model.Student;
import org.example.model.Task;

import java.util.List;

public final class MockStudentRepository {
    private MockStudentRepository() {
    }

    public static Student getSampleStudent() {
        // Sample data for prototype/demo purposes only. No real university student records are used.
        List<ScheduleItem> schedule = List.of(
                new ScheduleItem(new Course("COMSCI 2110", "Object-Oriented Programming"), "8:00 AM\n9:30 AM", "Lab 1", "Dr. Christian Rey C. Seco"),
                new ScheduleItem(new Course("MATH 1013", "Discrete Mathematics"), "10:00 AM\n11:30 AM", "C-204", "Prof. Maria Santos"),
                new ScheduleItem(new Course("ITEL 3012", "Information Systems Analysis"), "1:00 PM\n2:30 PM", "C-305", "Prof. Daniel Cruz")
        );
        List<Announcement> announcements = List.of(
                new Announcement("Your grades for this semester are now available.", "Oct 5, 2025"),
                new Announcement("Enrollment for midyear term will start next month.", "Oct 4, 2025"),
                new Announcement("System maintenance this Saturday, 8:00 PM.", "Oct 3, 2025")
        );
        List<Task> tasks = List.of(
                new Task("Settle tuition fee", "Due Oct 10"),
                new Task("Evaluate professors", "Due Oct 15"),
                new Task("Update personal information", ""),
                new Task("Apply for scholarship (optional)", "")
        );
        return new Student("2025-0011", "Justine Rivera", "BS Information Technology", "3rd Year",
                "justine.rivera@reyu.edu", "0917 123 4567", 5, 1.75, 15, 18,
                "Eligible", schedule, announcements, tasks);
    }
}
