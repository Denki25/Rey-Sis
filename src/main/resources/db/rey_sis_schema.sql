CREATE DATABASE IF NOT EXISTS `rey_sis`;
USE `rey_sis`;

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(50) NOT NULL,
    user_id INT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    program VARCHAR(100) NOT NULL,
    year_level VARCHAR(50) NOT NULL,
    section VARCHAR(50) NULL,
    email VARCHAR(150) NULL,
    contact_number VARCHAR(50) NULL,
    date_of_birth DATE NULL,
    address TEXT NULL,
    academic_status VARCHAR(50) NULL,
    curriculum_year VARCHAR(50) NULL,
    adviser VARCHAR(150) NULL,
    avatar_path VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id),
    UNIQUE (user_id),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT NOT NULL AUTO_INCREMENT,
    course_code VARCHAR(50) NOT NULL UNIQUE,
    course_name VARCHAR(200) NOT NULL,
    units INT NOT NULL DEFAULT 0,
    department VARCHAR(100) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id)
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id INT NOT NULL AUTO_INCREMENT,
    course_id INT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(100) NULL,
    instructor VARCHAR(150) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (schedule_id),
    CONSTRAINT fk_schedules_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (enrollment_id),
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE IF NOT EXISTS grades (
    grade_id INT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    course_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    grade DECIMAL(4,2) NULL,
    remarks TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (grade_id),
    CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_grades_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE IF NOT EXISTS announcements (
    announcement_id INT NOT NULL AUTO_INCREMENT,
    content TEXT NOT NULL,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (announcement_id)
);

CREATE TABLE IF NOT EXISTS tasks (
    task_id INT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    due_date DATE NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (task_id),
    CONSTRAINT fk_tasks_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_type VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
    PRIMARY KEY (payment_id),
    CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

INSERT INTO users (username, password_hash, role, is_active)
VALUES
    ('student', 'pbkdf2$120000$cmV5LXNpcy1kZW1vLXNhbHQ=$VQBvKIEBx9gjlzXyi4bt7i732lJNPWonc8pbpeb8yVw=', 'STUDENT', TRUE),
    ('cashier', 'pbkdf2$120000$cmV5LXNpcy1kZW1vLXNhbHQ=$VQBvKIEBx9gjlzXyi4bt7i732lJNPWonc8pbpeb8yVw=', 'CASHIER', TRUE),
    ('admin', 'pbkdf2$120000$cmV5LXNpcy1kZW1vLXNhbHQ=$VQBvKIEBx9gjlzXyi4bt7i732lJNPWonc8pbpeb8yVw=', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    role = VALUES(role),
    is_active = TRUE;

SET @student_user_id = (SELECT user_id FROM users WHERE username = 'student');

INSERT INTO students (student_id, user_id, first_name, last_name, program, year_level, section, email, academic_status)
VALUES (CONCAT('DEMO-', @student_user_id), @student_user_id, 'Demo', 'Student',
        'BS Information Technology', '1st Year', 'A', 'student@rey-sis.local', 'Active')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);
