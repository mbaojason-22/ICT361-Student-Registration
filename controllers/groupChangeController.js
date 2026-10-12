const db = require("../config/database");


// Student creates a group change request
const createGroupChangeRequest = async (req, res) => {
    try {
        const { student_id } = req.params;
        const {
            current_group_id,
            requested_group_id,
            reason
        } = req.body;

        if (!requested_group_id) {
            return res.status(400).json({
                success: false,
                message: "Requested group ID is required"
            });
        }

        if (
            current_group_id &&
            Number(current_group_id) === Number(requested_group_id)
        ) {
            return res.status(400).json({
                success: false,
                message: "Current group and requested group cannot be the same"
            });
        }

        // Check student exists
        const [students] = await db.query(
            `SELECT student_id
             FROM students
             WHERE student_id = ?`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Student not found"
            });
        }

        // Check requested group exists and is active
        const [groups] = await db.query(
            `SELECT
                group_id,
                max_members,
                current_members,
                active
             FROM lab_groups
             WHERE group_id = ?`,
            [requested_group_id]
        );

        if (groups.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Requested group not found"
            });
        }

        if (groups[0].active !== 1) {
            return res.status(400).json({
                success: false,
                message: "Requested group is not active"
            });
        }

        if (groups[0].current_members >= groups[0].max_members) {
            return res.status(400).json({
                success: false,
                message: "Requested group is already full"
            });
        }

        // Check for another pending request
        const [pendingRequest] = await db.query(
            `SELECT request_id
             FROM group_change_requests
             WHERE student_id = ?
               AND status = 'PENDING'`,
            [student_id]
        );

        if (pendingRequest.length > 0) {
            return res.status(409).json({
                success: false,
                message: "You already have a pending group change request"
            });
        }

        const [result] = await db.query(
            `INSERT INTO group_change_requests
             (
                student_id,
                current_group_id,
                requested_group_id,
                reason
             )
             VALUES (?, ?, ?, ?)`,
            [
                student_id,
                current_group_id || null,
                requested_group_id,
                reason || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Group change request submitted successfully",
            request_id: result.insertId
        });

    } catch (error) {
        console.error("Create group change request error:", error);

        res.status(500).json({
            success: false,
            message: "Could not submit group change request"
        });
    }
};


// Student views their requests
const getStudentGroupChangeRequests = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [requests] = await db.query(
            `SELECT
                gcr.request_id,
                gcr.student_id,
                gcr.current_group_id,
                current_group.group_code AS current_group_code,
                current_group.group_name AS current_group_name,
                gcr.requested_group_id,
                requested_group.group_code AS requested_group_code,
                requested_group.group_name AS requested_group_name,
                gcr.reason,
                gcr.status,
                gcr.review_comment,
                gcr.requested_at,
                gcr.reviewed_at
             FROM group_change_requests gcr
             LEFT JOIN lab_groups current_group
                ON gcr.current_group_id = current_group.group_id
             JOIN lab_groups requested_group
                ON gcr.requested_group_id = requested_group.group_id
             WHERE gcr.student_id = ?
             ORDER BY gcr.requested_at DESC`,
            [student_id]
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error("Get student group change requests error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve group change requests"
        });
    }
};


// Lecturer views pending requests
const getPendingGroupChangeRequests = async (req, res) => {
    try {
        const [requests] = await db.query(
            `SELECT
                gcr.request_id,
                gcr.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                gcr.current_group_id,
                current_group.group_code AS current_group_code,
                current_group.group_name AS current_group_name,
                gcr.requested_group_id,
                requested_group.group_code AS requested_group_code,
                requested_group.group_name AS requested_group_name,
                gcr.reason,
                gcr.status,
                gcr.requested_at
             FROM group_change_requests gcr
             JOIN students s
                ON gcr.student_id = s.student_id
             LEFT JOIN lab_groups current_group
                ON gcr.current_group_id = current_group.group_id
             JOIN lab_groups requested_group
                ON gcr.requested_group_id = requested_group.group_id
             WHERE gcr.status = 'PENDING'
             ORDER BY gcr.requested_at ASC`,
        );

        res.status(200).json({
            success: true,
            requests
        });

    } catch (error) {
        console.error("Get pending group change requests error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve pending group change requests"
        });
    }
};


// Lecturer approves a request
const approveGroupChangeRequest = async (req, res) => {
    const connection = await db.getConnection();

    try {
        const {
            lecturer_id,
            request_id
        } = req.params;

        const { review_comment } = req.body || {};

        await connection.beginTransaction();

        const [requests] = await connection.query(
            `SELECT
                request_id,
                student_id,
                current_group_id,
                requested_group_id
             FROM group_change_requests
             WHERE request_id = ?
               AND status = 'PENDING'
             FOR UPDATE`,
            [request_id]
        );

        if (requests.length === 0) {
            await connection.rollback();

            return res.status(404).json({
                success: false,
                message: "Pending group change request not found"
            });
        }

        const request = requests[0];

        // Check requested group
        const [groups] = await connection.query(
            `SELECT
                group_id,
                max_members,
                current_members,
                active
             FROM lab_groups
             WHERE group_id = ?
             FOR UPDATE`,
            [request.requested_group_id]
        );

        if (groups.length === 0 || groups[0].active !== 1) {
            await connection.rollback();

            return res.status(400).json({
                success: false,
                message: "Requested group is not available"
            });
        }

        if (groups[0].current_members >= groups[0].max_members) {
            await connection.rollback();

            return res.status(400).json({
                success: false,
                message: "Requested group is already full"
            });
        }

        // End current membership, if one exists
        if (request.current_group_id) {
            await connection.query(
                `UPDATE group_memberships
                 SET status = 'ENDED',
                     ended_at = NOW()
                 WHERE student_id = ?
                   AND group_id = ?
                   AND status = 'ACTIVE'`,
                [
                    request.student_id,
                    request.current_group_id
                ]
            );

            await connection.query(
                `UPDATE lab_groups
                 SET current_members =
                    CASE
                        WHEN current_members > 0 THEN current_members - 1
                        ELSE 0
                    END
                 WHERE group_id = ?`,
                [request.current_group_id]
            );
        
        }

        // Add student to requested group
        await connection.query(
            `INSERT INTO group_memberships
             (
                student_id,
                group_id,
                status
             )
             VALUES (?, ?, 'ACTIVE')`,
            [
                request.student_id,
                request.requested_group_id
            ]
        );

        await connection.query(
            `UPDATE lab_groups
             SET current_members = current_members + 1
             WHERE group_id = ?`,
            [request.requested_group_id]
        );

        // Approve request
        await connection.query(
            `UPDATE group_change_requests
             SET status = 'APPROVED',
                 reviewed_by_lecturer_id = ?,
                 review_comment = ?,
                 reviewed_at = NOW()
             WHERE request_id = ?`,
            [
                lecturer_id,
                review_comment || null,
                request_id
            ]
        );

        await connection.commit();

        res.status(200).json({
            success: true,
            message: "Group change request approved successfully"
        });

    } catch (error) {
        await connection.rollback();

        console.error("Approve group change request error:", error);

        res.status(500).json({
            success: false,
            message: "Could not approve group change request"
        });

    } finally {
        connection.release();
    }
};


// Lecturer rejects a request
const rejectGroupChangeRequest = async (req, res) => {
    try {
        const {
            lecturer_id,
            request_id
        } = req.params;

        const { review_comment } = req.body;

        const [requests] = await db.query(
            `SELECT request_id
             FROM group_change_requests
             WHERE request_id = ?
               AND status = 'PENDING'`,
            [request_id]
        );

        if (requests.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending group change request not found"
            });
        }

        await db.query(
            `UPDATE group_change_requests
             SET status = 'REJECTED',
                 reviewed_by_lecturer_id = ?,
                 review_comment = ?,
                 reviewed_at = NOW()
             WHERE request_id = ?`,
            [
                lecturer_id,
                review_comment || null,
                request_id
            ]
        );

        res.status(200).json({
            success: true,
            message: "Group change request rejected successfully"
        });

    } catch (error) {
        console.error("Reject group change request error:", error);

        res.status(500).json({
            success: false,
            message: "Could not reject group change request"
        });
    }
};


// Student cancels a pending request
const cancelGroupChangeRequest = async (req, res) => {
    try {
        const {
            student_id,
            request_id
        } = req.params;

        const [requests] = await db.query(
            `SELECT request_id
             FROM group_change_requests
             WHERE request_id = ?
               AND student_id = ?
               AND status = 'PENDING'`,
            [request_id, student_id]
        );

        if (requests.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending group change request not found"
            });
        }

        await db.query(
            `UPDATE group_change_requests
             SET status = 'CANCELLED'
             WHERE request_id = ?`,
            [request_id]
        );

        res.status(200).json({
            success: true,
            message: "Group change request cancelled successfully"
        });

    } catch (error) {
        console.error("Cancel group change request error:", error);

        res.status(500).json({
            success: false,
            message: "Could not cancel group change request"
        });
    }
};


module.exports = {
    createGroupChangeRequest,
    getStudentGroupChangeRequests,
    getPendingGroupChangeRequests,
    approveGroupChangeRequest,
    rejectGroupChangeRequest,
    cancelGroupChangeRequest
};