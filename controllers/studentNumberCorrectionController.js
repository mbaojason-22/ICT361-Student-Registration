const db = require("../config/database");

const createCorrectionRequest = async (req, res) => {
    try {
        const { student_id } = req.params;
        const {
            requested_student_number,
            reason
        } = req.body;

        if (!requested_student_number) {
            return res.status(400).json({
                success: false,
                message: "Requested student number is required"
            });
        }

        if (!/^\d{9}$/.test(requested_student_number)) {
            return res.status(400).json({
                success: false,
                message: "Student number must contain exactly 9 digits"
            });
        }

        const [students] = await db.query(
            `SELECT
                student_id,
                student_number
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

        const currentStudentNumber =
            students[0].student_number;

        if (
            currentStudentNumber ===
            requested_student_number
        ) {
            return res.status(400).json({
                success: false,
                message: "Requested student number is the same as the current number"
            });
        }

        const [existingNumber] = await db.query(
            `SELECT student_id
             FROM students
             WHERE student_number = ?`,
            [requested_student_number]
        );

        if (existingNumber.length > 0) {
            return res.status(409).json({
                success: false,
                message: "Requested student number is already in use"
            });
        }

        const [pendingRequest] = await db.query(
            `SELECT correction_request_id
             FROM student_number_correction_requests
             WHERE student_id = ?
               AND status = 'PENDING'`,
            [student_id]
        );

        if (pendingRequest.length > 0) {
            return res.status(409).json({
                success: false,
                message: "You already have a pending correction request"
            });
        }

        const [result] = await db.query(
            `INSERT INTO student_number_correction_requests
             (
                student_id,
                current_student_number,
                requested_student_number,
                reason
             )
             VALUES (?, ?, ?, ?)`,
            [
                student_id,
                currentStudentNumber,
                requested_student_number,
                reason || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Student number correction request created successfully",
            correction_request_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Create correction request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create correction request"
        });
    }
};


const getStudentCorrectionRequests = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [requests] = await db.query(
            `SELECT
                correction_request_id,
                student_id,
                current_student_number,
                requested_student_number,
                reason,
                status,
                reviewed_by_lecturer_id,
                review_comment,
                requested_at,
                reviewed_at
             FROM student_number_correction_requests
             WHERE student_id = ?
             ORDER BY requested_at DESC`,
            [student_id]
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error(
            "Get student correction requests error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve correction requests"
        });
    }
};


const getPendingCorrectionRequests = async (req, res) => {
    try {
        const [requests] = await db.query(
            `SELECT
                sncr.correction_request_id,
                sncr.student_id,
                s.first_name,
                s.last_name,
                sncr.current_student_number,
                sncr.requested_student_number,
                sncr.reason,
                sncr.status,
                sncr.requested_at
             FROM student_number_correction_requests sncr
             JOIN students s
                ON sncr.student_id = s.student_id
             WHERE sncr.status = 'PENDING'
             ORDER BY sncr.requested_at ASC`
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error(
            "Get pending correction requests error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve pending correction requests"
        });
    }
};


const approveCorrectionRequest = async (req, res) => {
    const connection = await db.getConnection();

    try {
        const {
            correction_request_id,
            lecturer_id
        } = req.params;

        const { review_comment } = req.body || {};

        await connection.beginTransaction();

        const [requests] = await connection.query(
            `SELECT
                correction_request_id,
                student_id,
                current_student_number,
                requested_student_number,
                status
             FROM student_number_correction_requests
             WHERE correction_request_id = ?
             FOR UPDATE`,
            [correction_request_id]
        );

        if (requests.length === 0) {
            await connection.rollback();

            return res.status(404).json({
                success: false,
                message: "Correction request not found"
            });
        }

        const request = requests[0];

        if (request.status !== "PENDING") {
            await connection.rollback();

            return res.status(400).json({
                success: false,
                message: "Correction request has already been reviewed"
            });
        }

        // Make sure the requested number is still available
        const [existingNumber] = await connection.query(
            `SELECT student_id
             FROM students
             WHERE student_number = ?
               AND student_id != ?`,
            [
                request.requested_student_number,
                request.student_id
            ]
        );

        if (existingNumber.length > 0) {
            await connection.rollback();

            return res.status(409).json({
                success: false,
                message: "Requested student number is already in use"
            });
        }

        await connection.query(
            `UPDATE students
             SET student_number = ?
             WHERE student_id = ?`,
            [
                request.requested_student_number,
                request.student_id
            ]
        );

        await connection.query(
            `UPDATE student_number_correction_requests
             SET
                status = 'APPROVED',
                reviewed_by_lecturer_id = ?,
                review_comment = ?,
                reviewed_at = NOW()
             WHERE correction_request_id = ?`,
            [
                lecturer_id,
                review_comment || null,
                correction_request_id
            ]
        );

        await connection.commit();

        res.status(200).json({
            success: true,
            message: "Student number correction approved successfully"
        });

    } catch (error) {
        await connection.rollback();

        console.error(
            "Approve correction request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not approve correction request"
        });

    } finally {
        connection.release();
    }
};


const rejectCorrectionRequest = async (req, res) => {
    try {
        const {
            correction_request_id,
            lecturer_id
        } = req.params;

        const { review_comment } = req.body || {};

        const [result] = await db.query(
            `UPDATE student_number_correction_requests
             SET
                status = 'REJECTED',
                reviewed_by_lecturer_id = ?,
                review_comment = ?,
                reviewed_at = NOW()
             WHERE correction_request_id = ?
               AND status = 'PENDING'`,
            [
                lecturer_id,
                review_comment || null,
                correction_request_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending correction request not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Student number correction request rejected successfully"
        });

    } catch (error) {
        console.error(
            "Reject correction request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not reject correction request"
        });
    }
};


const cancelCorrectionRequest = async (req, res) => {
    try {
        const {
            correction_request_id,
            student_id
        } = req.params;

        const [result] = await db.query(
            `UPDATE student_number_correction_requests
             SET status = 'CANCELLED'
             WHERE correction_request_id = ?
               AND student_id = ?
               AND status = 'PENDING'`,
            [
                correction_request_id,
                student_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending correction request not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Student number correction request cancelled successfully"
        });

    } catch (error) {
        console.error(
            "Cancel correction request error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not cancel correction request"
        });
    }
};


module.exports = {
    createCorrectionRequest,
    getStudentCorrectionRequests,
    getPendingCorrectionRequests,
    approveCorrectionRequest,
    rejectCorrectionRequest,
    cancelCorrectionRequest
};