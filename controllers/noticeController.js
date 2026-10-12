const db = require("../config/database");


// Create a lecturer notice
const createNotice = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const {
            course_id,
            group_id,
            title,
            message,
            priority,
            expires_at
        } = req.body;

        if (!title || !message) {
            return res.status(400).json({
                success: false,
                message: "Title and message are required"
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

        // If a course is supplied, make sure the lecturer teaches it
        if (course_id) {
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
        }

        // If a group is supplied, make sure it exists
        if (group_id) {
            const [groups] = await db.query(
                `SELECT group_id
                 FROM lab_groups
                 WHERE group_id = ?
                   AND active = 1`,
                [group_id]
            );

            if (groups.length === 0) {
                return res.status(404).json({
                    success: false,
                    message: "Group not found"
                });
            }
        }

        const validPriorities = ["NORMAL", "IMPORTANT", "URGENT"];
        const noticePriority = priority || "NORMAL";

        if (!validPriorities.includes(noticePriority)) {
            return res.status(400).json({
                success: false,
                message: "Invalid notice priority"
            });
        }

        const [result] = await db.query(
            `INSERT INTO lecturer_notices
             (
                lecturer_id,
                course_id,
                group_id,
                title,
                message,
                priority,
                expires_at
             )
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [
                lecturer_id,
                course_id || null,
                group_id || null,
                title,
                message,
                noticePriority,
                expires_at || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Notice created successfully",
            notice_id: result.insertId
        });

    } catch (error) {
        console.error("Create notice error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create notice"
        });
    }
};


// Get active notices
const getNotices = async (req, res) => {
    try {
        const [notices] = await db.query(
            `SELECT
                ln.notice_id,
                ln.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                ln.course_id,
                c.course_code,
                c.course_name,
                ln.group_id,
                lg.group_code,
                lg.group_name,
                ln.title,
                ln.message,
                ln.priority,
                ln.published_at,
                ln.expires_at
             FROM lecturer_notices ln
             JOIN lecturers l
                ON ln.lecturer_id = l.lecturer_id
             LEFT JOIN courses c
                ON ln.course_id = c.course_id
             LEFT JOIN lab_groups lg
                ON ln.group_id = lg.group_id
             WHERE ln.active = 1
               AND (
                    ln.expires_at IS NULL
                    OR ln.expires_at > NOW()
               )
             ORDER BY
                CASE ln.priority
                    WHEN 'URGENT' THEN 1
                    WHEN 'IMPORTANT' THEN 2
                    ELSE 3
                END,
                ln.published_at DESC`
        );

        res.status(200).json({
            success: true,
            notices
        });

    } catch (error) {
        console.error("Get notices error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve notices"
        });
    }
};


// Get notices for a specific course
const getCourseNotices = async (req, res) => {
    try {
        const { course_id } = req.params;

        const [notices] = await db.query(
            `SELECT
                ln.notice_id,
                ln.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                ln.course_id,
                c.course_code,
                c.course_name,
                ln.group_id,
                lg.group_code,
                lg.group_name,
                ln.title,
                ln.message,
                ln.priority,
                ln.published_at,
                ln.expires_at
             FROM lecturer_notices ln
             JOIN lecturers l
                ON ln.lecturer_id = l.lecturer_id
             LEFT JOIN courses c
                ON ln.course_id = c.course_id
             LEFT JOIN lab_groups lg
                ON ln.group_id = lg.group_id
             WHERE ln.course_id = ?
               AND ln.active = 1
               AND (
                    ln.expires_at IS NULL
                    OR ln.expires_at > NOW()
               )
             ORDER BY
                CASE ln.priority
                    WHEN 'URGENT' THEN 1
                    WHEN 'IMPORTANT' THEN 2
                    ELSE 3
                END,
                ln.published_at DESC`,
            [course_id]
        );

        res.status(200).json({
            success: true,
            notices
        });

    } catch (error) {
        console.error("Get course notices error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve course notices"
        });
    }
};


// Get notices for a specific group
const getGroupNotices = async (req, res) => {
    try {
        const { group_id } = req.params;

        const [notices] = await db.query(
            `SELECT
                ln.notice_id,
                ln.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                ln.course_id,
                c.course_code,
                c.course_name,
                ln.group_id,
                lg.group_code,
                lg.group_name,
                ln.title,
                ln.message,
                ln.priority,
                ln.published_at,
                ln.expires_at
             FROM lecturer_notices ln
             JOIN lecturers l
                ON ln.lecturer_id = l.lecturer_id
             LEFT JOIN courses c
                ON ln.course_id = c.course_id
             LEFT JOIN lab_groups lg
                ON ln.group_id = lg.group_id
             WHERE ln.group_id = ?
               AND ln.active = 1
               AND (
                    ln.expires_at IS NULL
                    OR ln.expires_at > NOW()
               )
             ORDER BY
                CASE ln.priority
                    WHEN 'URGENT' THEN 1
                    WHEN 'IMPORTANT' THEN 2
                    ELSE 3
                END,
                ln.published_at DESC`,
            [group_id]
        );

        res.status(200).json({
            success: true,
            notices
        });

    } catch (error) {
        console.error("Get group notices error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve group notices"
        });
    }
};


// Deactivate a notice
const deactivateNotice = async (req, res) => {
    try {
        const {
            lecturer_id,
            notice_id
        } = req.params;

        const [notices] = await db.query(
            `SELECT notice_id
             FROM lecturer_notices
             WHERE notice_id = ?
               AND lecturer_id = ?
               AND active = 1`,
            [notice_id, lecturer_id]
        );

        if (notices.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Active notice not found"
            });
        }

        await db.query(
            `UPDATE lecturer_notices
             SET active = 0
             WHERE notice_id = ?`,
            [notice_id]
        );

        res.status(200).json({
            success: true,
            message: "Notice deactivated successfully"
        });

    } catch (error) {
        console.error("Deactivate notice error:", error);

        res.status(500).json({
            success: false,
            message: "Could not deactivate notice"
        });
    }
};


module.exports = {
    createNotice,
    getNotices,
    getCourseNotices,
    getGroupNotices,
    deactivateNotice
};