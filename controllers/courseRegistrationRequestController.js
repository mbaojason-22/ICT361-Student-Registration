const db = require("../config/database");

const createRegistrationRequest = async (req, res) => {
    try {
        const { student_id } = req.params;
        const { course_id, student_comment } = req.body;

        if (!course_id) {
            return res.status(400).json({
                success: false,
                message: "Course ID is required"
            });
        }

        // Check student
        const [students] = await db.query(
            `SELECT student_id
             FROM students
             WHERE student_id = ?
               AND status = 'ACTIVE'`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Student not found"
            });
        }

        // Check course
        const [courses] = await db.query(
            `SELECT course_id
             FROM courses
             WHERE course_id = ?
               AND active = 1`,
            [course_id]
        );

        if (courses.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Course not found"
            });
        }

        // Check for an existing pending request
        const [existingRequests] = await db.query(
            `SELECT registration_request_id
             FROM course_registration_requests
             WHERE student_id = ?
               AND course_id = ?
               AND status = 'PENDING'`,
            [student_id, course_id]
        );

        if (existingRequests.length > 0) {
            return res.status(409).json({
                success: false,
                message: "A pending registration request already exists"
            });
        }

        const [result] = await db.query(
            `INSERT INTO course_registration_requests
             (
                student_id,
                course_id,
                student_comment
             )
             VALUES (?, ?, ?)`,
            [
                student_id,
                course_id,
                student_comment || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Course registration request created successfully",
            registration_request_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Create registration request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create registration request"
        });
    }
};


const getStudentRegistrationRequests = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [requests] = await db.query(
            `SELECT
                crr.registration_request_id,
                crr.student_id,
                crr.course_id,
                c.course_code,
                c.course_name,
                crr.status,
                crr.student_comment,
                crr.lecturer_comment,
                crr.requested_at,
                crr.reviewed_at,
                crr.reviewed_by_lecturer_id
             FROM course_registration_requests crr
             JOIN courses c
                ON crr.course_id = c.course_id
             WHERE crr.student_id = ?
             ORDER BY crr.requested_at DESC`,
            [student_id]
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error(
            "Get student registration requests error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve registration requests"
        });
    }
};


const getPendingRegistrationRequests = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        // Only courses assigned to this lecturer
        const [requests] = await db.query(
            `SELECT
                crr.registration_request_id,
                crr.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                crr.course_id,
                c.course_code,
                c.course_name,
                crr.status,
                crr.student_comment,
                crr.requested_at
             FROM course_registration_requests crr
             JOIN students s
                ON crr.student_id = s.student_id
             JOIN courses c
                ON crr.course_id = c.course_id
             JOIN lecturer_courses lc
                ON crr.course_id = lc.course_id
             WHERE lc.lecturer_id = ?
               AND crr.status = 'PENDING'
             ORDER BY crr.requested_at ASC`,
            [lecturer_id]
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error(
            "Get pending registration requests error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve pending registration requests"
        });
    }
};


const approveRegistrationRequest = async (req, res) => {
    const connection = await db.getConnection();

    try {
        const {
            registration_request_id,
            lecturer_id
        } = req.params;

        const { lecturer_comment } = req.body || {};

        await connection.beginTransaction();

        // Make sure lecturer is assigned to the requested course
        const [assignment] = await connection.query(
            `SELECT lc.lecturer_course_id
             FROM lecturer_courses lc
             WHERE lc.lecturer_id = ?
               AND lc.course_id = (
                   SELECT course_id
                   FROM course_registration_requests
                   WHERE registration_request_id = ?
               )`,
            [
                lecturer_id,
                registration_request_id
            ]
        );

        if (assignment.length === 0) {
            await connection.rollback();

            return res.status(403).json({
                success: false,
                message: "Lecturer is not assigned to this course"
            });
        }

        // Get the pending request
        const [requests] = await connection.query(
            `SELECT
                registration_request_id,
                student_id,
                course_id,
                status
             FROM course_registration_requests
             WHERE registration_request_id = ?
             FOR UPDATE`,
            [registration_request_id]
        );

        if (requests.length === 0) {
            await connection.rollback();

            return res.status(404).json({
                success: false,
                message: "Registration request not found"
            });
        }

        const request = requests[0];

        if (request.status !== "PENDING") {
            await connection.rollback();

            return res.status(400).json({
                success: false,
                message: "Registration request has already been reviewed"
            });
        }

        // Approve the request
        await connection.query(
            `UPDATE course_registration_requests
             SET
                status = 'APPROVED',
                lecturer_comment = ?,
                reviewed_at = NOW(),
                reviewed_by_lecturer_id = ?
             WHERE registration_request_id = ?`,
            [
                lecturer_comment || null,
                lecturer_id,
                registration_request_id
            ]
        );

        // Add/update the student's course enrolment
        const [existingEnrolment] = await connection.query(
            `SELECT enrolment_id
             FROM course_enrolments
             WHERE student_id = ?
               AND course_id = ?`,
            [
                request.student_id,
                request.course_id
            ]
        );

        if (existingEnrolment.length === 0) {
            await connection.query(
                `INSERT INTO course_enrolments
                 (
                    student_id,
                    course_id,
                    status,
                    approval_status
                 )
                 VALUES (?, ?, 'ENROLLED', 'APPROVED')`,
                [
                    request.student_id,
                    request.course_id
                ]
            );
        } else {
            await connection.query(
                `UPDATE course_enrolments
                 SET
                    status = 'ENROLLED',
                    approval_status = 'APPROVED',
                    enrolled_at = NOW()
                 WHERE enrolment_id = ?`,
                [existingEnrolment[0].enrolment_id]
            );
        }

        await connection.commit();

        res.status(200).json({
            success: true,
            message: "Course registration request approved successfully"
        });

    } catch (error) {
        await connection.rollback();

        console.error(
            "Approve registration request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not approve registration request"
        });

    } finally {
        connection.release();
    }
};


const rejectRegistrationRequest = async (req, res) => {
    try {
        const {
            registration_request_id,
            lecturer_id
        } = req.params;

        const { lecturer_comment } = req.body || {};

        const [assignment] = await db.query(
            `SELECT lc.lecturer_course_id
             FROM lecturer_courses lc
             JOIN course_registration_requests crr
                ON lc.course_id = crr.course_id
             WHERE lc.lecturer_id = ?
               AND crr.registration_request_id = ?`,
            [
                lecturer_id,
                registration_request_id
            ]
        );

        if (assignment.length === 0) {
            return res.status(403).json({
                success: false,
                message: "Lecturer is not assigned to this course"
            });
        }

        const [result] = await db.query(
            `UPDATE course_registration_requests
             SET
                status = 'REJECTED',
                lecturer_comment = ?,
                reviewed_at = NOW(),
                reviewed_by_lecturer_id = ?
             WHERE registration_request_id = ?
               AND status = 'PENDING'`,
            [
                lecturer_comment || null,
                lecturer_id,
                registration_request_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending registration request not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Course registration request rejected successfully"
        });

    } catch (error) {
        console.error(
            "Reject registration request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not reject registration request"
        });
    }
};


const cancelRegistrationRequest = async (req, res) => {
    try {
        const {
            registration_request_id,
            student_id
        } = req.params;

        const [result] = await db.query(
            `UPDATE course_registration_requests
             SET status = 'CANCELLED'
             WHERE registration_request_id = ?
               AND student_id = ?
               AND status = 'PENDING'`,
            [
                registration_request_id,
                student_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending registration request not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Course registration request cancelled successfully"
        });

    } catch (error) {
        console.error(
            "Cancel registration request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not cancel registration request"
        });
    }
};


module.exports = {
    createRegistrationRequest,
    getStudentRegistrationRequests,
    getPendingRegistrationRequests,
    approveRegistrationRequest,
    rejectRegistrationRequest,
    cancelRegistrationRequest
};