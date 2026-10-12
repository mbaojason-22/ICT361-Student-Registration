const db = require("../config/database");


// Create an assignment
const createAssignment = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const {
            course_id,
            title,
            description,
            due_at,
            max_marks,
            status
        } = req.body;

        if (!course_id || !title || !due_at) {
            return res.status(400).json({
                success: false,
                message: "Course ID, title and due date are required"
            });
        }

        // Check lecturer exists
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

        // Make sure lecturer teaches this course
        const [courseAssignment] = await db.query(
            `SELECT lecturer_course_id
             FROM lecturer_courses
             WHERE lecturer_id = ?
               AND course_id = ?`,
            [lecturer_id, course_id]
        );

        if (courseAssignment.length === 0) {
            return res.status(403).json({
                success: false,
                message: "Lecturer is not assigned to this course"
            });
        }

        const validStatuses = [
            "DRAFT",
            "PUBLISHED",
            "CLOSED"
        ];

        const assignmentStatus = status || "PUBLISHED";

        if (!validStatuses.includes(assignmentStatus)) {
            return res.status(400).json({
                success: false,
                message: "Invalid assignment status"
            });
        }

        const [result] = await db.query(
            `INSERT INTO assignments
             (
                course_id,
                lecturer_id,
                title,
                description,
                due_at,
                max_marks,
                status
             )
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [
                course_id,
                lecturer_id,
                title,
                description || null,
                due_at,
                max_marks || null,
                assignmentStatus
            ]
        );

        res.status(201).json({
            success: true,
            message: "Assignment created successfully",
            assignment_id: result.insertId
        });

    } catch (error) {
        console.error("Create assignment error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create assignment"
        });
    }
};


// Get assignments created by a lecturer
const getLecturerAssignments = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const [assignments] = await db.query(
            `SELECT
                a.assignment_id,
                a.course_id,
                c.course_code,
                c.course_name,
                a.lecturer_id,
                a.title,
                a.description,
                a.due_at,
                a.max_marks,
                a.status,
                a.created_at,
                a.updated_at
             FROM assignments a
             JOIN courses c
                ON a.course_id = c.course_id
             WHERE a.lecturer_id = ?
             ORDER BY a.due_at ASC`,
            [lecturer_id]
        );

        res.status(200).json({
            success: true,
            assignments
        });

    } catch (error) {
        console.error("Get lecturer assignments error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve assignments"
        });
    }
};


// Get published assignments for a course
const getCourseAssignments = async (req, res) => {
    try {
        const { course_id } = req.params;

        const [assignments] = await db.query(
            `SELECT
                a.assignment_id,
                a.course_id,
                c.course_code,
                c.course_name,
                a.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                a.title,
                a.description,
                a.due_at,
                a.max_marks,
                a.status,
                a.created_at,
                a.updated_at
             FROM assignments a
             JOIN courses c
                ON a.course_id = c.course_id
             LEFT JOIN lecturers l
                ON a.lecturer_id = l.lecturer_id
             WHERE a.course_id = ?
               AND a.status = 'PUBLISHED'
             ORDER BY a.due_at ASC`,
            [course_id]
        );

        res.status(200).json({
            success: true,
            assignments
        });

    } catch (error) {
        console.error("Get course assignments error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve course assignments"
        });
    }
};


// Update assignment status
const updateAssignmentStatus = async (req, res) => {
    try {
        const {
            lecturer_id,
            assignment_id
        } = req.params;

        const { status } = req.body;

        const validStatuses = [
            "DRAFT",
            "PUBLISHED",
            "CLOSED"
        ];

        if (!validStatuses.includes(status)) {
            return res.status(400).json({
                success: false,
                message: "Invalid assignment status"
            });
        }

        const [assignments] = await db.query(
            `SELECT assignment_id
             FROM assignments
             WHERE assignment_id = ?
               AND lecturer_id = ?`,
            [
                assignment_id,
                lecturer_id
            ]
        );

        if (assignments.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Assignment not found"
            });
        }

        await db.query(
            `UPDATE assignments
             SET status = ?
             WHERE assignment_id = ?`,
            [
                status,
                assignment_id
            ]
        );

        res.status(200).json({
            success: true,
            message: "Assignment status updated successfully"
        });

    } catch (error) {
        console.error(
            "Update assignment status error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not update assignment status"
        });
    }
};


module.exports = {
    createAssignment,
    getLecturerAssignments,
    getCourseAssignments,
    updateAssignmentStatus
};