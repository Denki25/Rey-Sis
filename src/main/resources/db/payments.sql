CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_type VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
    CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);
