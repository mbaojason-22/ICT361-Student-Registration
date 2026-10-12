const db = require("../config/database");


// Create or update a draft submission
const saveDraftSubmission = async (req, res) => {
    try {
        const { assignment_id, student_id } = req.params;

        const {
            file_url,
            file_name,
            text_submission
        } = req.body;

        // Check assignment exists and is published
        const [assignments] = await db.query(
            `SELECT assignment_id, due_at, status
             FROM assignments
             WHERE assignment_id = ?`,
            [assignment_id]
        );

        if (assignments.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Assignment not found"
            });
        }

        if (assignments[0].status !== "PUBLISHED") {
            return res.status(400).json({
                success: false,
                message: "This assignment is not available for submission"
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

        // Check existing submission
        const [existing] = await db.query(
            `SELECT submission_id, status
             FROM submissions
             WHERE assignment_id = ?
               AND student_id = ?`,
            [
                assignment_id,
                student_id
            ]
        );

        if (existing.length > 0) {

            if (existing[0].status !== "DRAFT") {
                return res.status(409).json({
                    success: false,
                    message: "This assignment has already been submitted"
                });
            }

            await db.query(
                `UPDATE submissions
                 SET file_url = ?,
                     file_name = ?,
                     text_submission = ?
                 WHERE submission_id = ?`,
                [
                    file_url || null,
                    file_name || null,
                    text_submission || null,
                    existing[0].submission_id
                ]
            );

            return res.status(200).json({
                success: true,
                message: "Draft submission updated successfully",
                submission_id: existing[0].submission_id
            });
        }

        const [result] = await db.query(
            `INSERT INTO submissions
             (
                assignment_id,
                student_id,
                file_url,
                file_name,
                text_submission,
                status
             )
             VALUES (?, ?, ?, ?, ?, 'DRAFT')`,
            [
                assignment_id,
                student_id,
                file_url || null,
                file_name || null,
                text_submission || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Draft submission saved successfully",
            submission_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Save draft submission error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not save draft submission"
        });
    }
};


// Submit an assignment
const submitAssignment = async (req, res) => {
    try {
        const {
            assignment_id,
            student_id
        } = req.params;

        // Check assignment
        const [assignments] = await db.query(
            `SELECT assignment_id, due_at, status
             FROM assignments
             WHERE assignment_id = ?`,
            [assignment_id]
        );

        if (assignments.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Assignment not found"
            });
        }

        const assignment = assignments[0];

        if (assignment.status !== "PUBLISHED") {
            return res.status(400).json({
                success: false,
                message: "This assignment is not open for submission"
            });
        }

        if (new Date(assignment.due_at) < new Date()) {
            return res.status(400).json({
                success: false,
                message: "The assignment deadline has passed"
            });
        }

        // Find student's draft
        const [submissions] = await db.query(
            `SELECT submission_id, status
             FROM submissions
             WHERE assignment_id = ?
               AND student_id = ?`,
            [
                assignment_id,
                student_id
            ]
        );

        if (submissions.length === 0) {
            return res.status(404).json({
                success: false,
                message: "No draft submission found"
            });
        }

        if (submissions[0].status !== "DRAFT") {
            return res.status(409).json({
                success: false,
                message: "Assignment has already been submitted"
            });
        }

        await db.query(
            `UPDATE submissions
             SET status = 'SUBMITTED',
                 submitted_at = NOW()
             WHERE submission_id = ?`,
            [submissions[0].submission_id]
        );

        res.status(200).json({
            success: true,
            message: "Assignment submitted successfully"
        });

    } catch (error) {
        console.error(
            "Submit assignment error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not submit assignment"
        });
    }
};


// Get student's submission
const getStudentSubmission = async (req, res) => {
    try {
        const {
            assignment_id,
            student_id
        } = req.params;

        const [submissions] = await db.query(
            `SELECT
                s.submission_id,
                s.assignment_id,
                a.title AS assignment_title,
                a.due_at,
                s.student_id,
                s.file_url,
                s.file_name,
                s.text_submission,
                s.status,
                s.submitted_at,
                s.marks,
                s.feedback,
                s.created_at,
                s.updated_at
             FROM submissions s
             JOIN assignments a
                ON s.assignment_id = a.assignment_id
             WHERE s.assignment_id = ?
               AND s.student_id = ?`,
            [
                assignment_id,
                student_id
            ]
        );

        if (submissions.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Submission not found"
            });
        }

        res.status(200).json({
            success: true,
            submission: submissions[0]
        });

    } catch (error) {
        console.error(
            "Get student submission error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve submission"
        });
    }
};


// Lecturer views submissions
const getAssignmentSubmissions = async (req, res) => {
    try {
        const {
            assignment_id,
            lecturer_id
        } = req.params;

        // Make sure lecturer owns the assignment
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

        const [submissions] = await db.query(
            `SELECT
                s.submission_id,
                s.assignment_id,
                s.student_id,
                st.student_number,
                st.first_name,
                st.last_name,
                s.file_url,
                s.file_name,
                s.text_submission,
                s.status,
                s.submitted_at,
                s.marks,
                s.feedback
             FROM submissions s
             JOIN students st
                ON s.student_id = st.student_id
             WHERE s.assignment_id = ?
             ORDER BY st.last_name, st.first_name`,
            [assignment_id]
        );

        res.status(200).json({
            success: true,
            submissions
        });

    } catch (error) {
        console.error(
            "Get assignment submissions error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve submissions"
        });
    }
};


// Grade a submission
const gradeSubmission = async (req, res) => {
    try {
        const {
            submission_id,
            lecturer_id
        } = req.params;

        const {
            marks,
            feedback
        } = req.body;

        // Check submission belongs to lecturer's assignment
        const [submissions] = await db.query(
            `SELECT
                s.submission_id,
                a.max_marks
             FROM submissions s
             JOIN assignments a
                ON s.assignment_id = a.assignment_id
             WHERE s.submission_id = ?
               AND a.lecturer_id = ?`,
            [
                submission_id,
                lecturer_id
            ]
        );

        if (submissions.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Submission not found"
            });
        }

        if (marks === undefined || marks === null) {
            return res.status(400).json({
                success: false,
                message: "Marks are required"
            });
        }

        if (
            submissions[0].max_marks !== null &&
            Number(marks) > Number(submissions[0].max_marks)
        ) {
            return res.status(400).json({
                success: false,
                message: "Marks cannot exceed maximum marks"
            });
        }

        if (Number(marks) < 0) {
            return res.status(400).json({
                success: false,
                message: "Marks cannot be negative"
            });
        }

        await db.query(
            `UPDATE submissions
             SET marks = ?,
                 feedback = ?,
                 status = 'GRADED'
             WHERE submission_id = ?`,
            [
                marks,
                feedback || null,
                submission_id
            ]
        );

        res.status(200).json({
            success: true,
            message: "Submission graded successfully"
        });

    } catch (error) {
        console.error(
            "Grade submission error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not grade submission"
        });
    }
};


module.exports = {
    saveDraftSubmission,
    submitAssignment,
    getStudentSubmission,
    getAssignmentSubmissions,
    gradeSubmission
};