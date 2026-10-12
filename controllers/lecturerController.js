const db = require("../config/database");


// Get pending course registrations
const getPendingRegistrations = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const [registrations] = await db.query(
            `SELECT
                ce.enrolment_id,
                ce.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                ce.course_id,
                c.course_code,
                c.course_name,
                ce.status,
                ce.approval_status,
                ce.enrolled_at
             FROM course_enrolments ce
             JOIN students s
                ON ce.student_id = s.student_id
             JOIN courses c
                ON ce.course_id = c.course_id
             JOIN lecturer_courses lc
                ON ce.course_id = lc.course_id
             WHERE lc.lecturer_id = ?
               AND ce.approval_status = 'PENDING'
             ORDER BY ce.enrolled_at ASC`,
            [lecturer_id]
        );

        res.status(200).json({
            success: true,
            registrations
        });

    } catch (error) {
        console.error(
            "Get pending registrations error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve pending registrations"
        });
    }
};


// Approve course registration
const approveRegistration = async (req, res) => {
    try {
        const {
            lecturer_id,
            enrollment_id
        } = req.params;

        const [registrations] = await db.query(
            `SELECT
                ce.enrolment_id
             FROM course_enrolments ce
             JOIN lecturer_courses lc
                ON ce.course_id = lc.course_id
             WHERE ce.enrolment_id = ?
               AND lc.lecturer_id = ?
               AND ce.approval_status = 'PENDING'`,
            [
                enrollment_id,
                lecturer_id
            ]
        );

        if (registrations.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending course registration not found"
            });
        }

        await db.query(
            `UPDATE course_enrolments
             SET approval_status = 'APPROVED'
             WHERE enrolment_id = ?`,
            [enrollment_id]
        );

        res.status(200).json({
            success: true,
            message: "Course registration approved successfully"
        });

    } catch (error) {
        console.error(
            "Approve registration error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not approve course registration"
        });
    }
};


// Reject course registration
const rejectRegistration = async (req, res) => {
    try {
        const {
            lecturer_id,
            enrollment_id
        } = req.params;

        const [registrations] = await db.query(
            `SELECT
                ce.enrolment_id
             FROM course_enrolments ce
             JOIN lecturer_courses lc
                ON ce.course_id = lc.course_id
             WHERE ce.enrolment_id = ?
               AND lc.lecturer_id = ?
               AND ce.approval_status = 'PENDING'`,
            [
                enrollment_id,
                lecturer_id
            ]
        );

        if (registrations.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending course registration not found"
            });
        }

        await db.query(
            `UPDATE course_enrolments
             SET approval_status = 'REJECTED'
             WHERE enrolment_id = ?`,
            [enrollment_id]
        );

        res.status(200).json({
            success: true,
            message: "Course registration rejected successfully"
        });

    } catch (error) {
        console.error(
            "Reject registration error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not reject course registration"
        });
    }
};


// Get all students
const getAllStudents = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        // Check lecturer
        const [lecturers] = await db.query(
            `SELECT lecturer_id
             FROM lecturers
             WHERE lecturer_id = ?
               AND status = 'ACTIVE'`,
            [lecturer_id]
        );

        if (lecturers.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Lecturer not found"
            });
        }

        const [students] = await db.query(
            `SELECT
                s.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                s.programme_id,
                p.code,
                p.name,
                s.status,
                CASE
                    WHEN a.account_id IS NULL THEN 0
                    ELSE 1
                END AS has_account
             FROM students s
             LEFT JOIN programmes p
                ON s.programme_id = p.programme_id
             LEFT JOIN accounts a
                ON a.student_id = s.student_id
               AND a.role = 'STUDENT'
             ORDER BY s.student_number`
        );

        res.status(200).json({
            success: true,
            students
        });

    } catch (error) {
        console.error(
            "Get all students error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve students"
        });
    }
};


// Add a student without creating an account
const addStudent = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const {
            student_number,
            first_name,
            last_name,
            programme_id
        } = req.body;

        if (
            !student_number ||
            !first_name ||
            !last_name ||
            !programme_id
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Student number, first name, last name and programme ID are required"
            });
        }

        // Check lecturer
        const [lecturers] = await db.query(
            `SELECT lecturer_id
             FROM lecturers
             WHERE lecturer_id = ?
               AND status = 'ACTIVE'`,
            [lecturer_id]
        );

        if (lecturers.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Lecturer not found"
            });
        }

        // Check programme
        const [programmes] = await db.query(
            `SELECT programme_id
             FROM programmes
             WHERE programme_id = ?
               AND active = 1`,
            [programme_id]
        );

        if (programmes.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Programme not found"
            });
        }

        // Check duplicate student number
        const [existingStudent] = await db.query(
            `SELECT student_id
             FROM students
             WHERE student_number = ?`,
            [student_number]
        );

        if (existingStudent.length > 0) {
            return res.status(409).json({
                success: false,
                message: "Student number already exists"
            });
        }

        // Add student without creating an account
        const [result] = await db.query(
            `INSERT INTO students
             (
                student_number,
                first_name,
                last_name,
                programme_id,
                status
             )
             VALUES (?, ?, ?, ?, 'ACTIVE')`,
            [
                student_number,
                first_name,
                last_name,
                programme_id
            ]
        );

        res.status(201).json({
            success: true,
            message: "Student added successfully",
            student_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Add student error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not add student"
        });
    }
};


module.exports = {
    getPendingRegistrations,
    approveRegistration,
    rejectRegistration,
    getAllStudents,
    addStudent
};