const db = require("../config/database");


// Create timetable entry
const createTimetableEntry = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const {
            course_id,
            lecturer_id: assigned_lecturer_id,
            day_of_week,
            start_time,
            end_time,
            venue,
            session_type
        } = req.body;

        if (
            !course_id ||
            day_of_week === undefined ||
            !start_time ||
            !end_time ||
            !venue
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Course ID, day, start time, end time and venue are required"
            });
        }

        // Check that the lecturer creating the timetable exists
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

        // Check day of week
        if (
            !Number.isInteger(Number(day_of_week)) ||
            Number(day_of_week) < 1 ||
            Number(day_of_week) > 7
        ) {
            return res.status(400).json({
                success: false,
                message: "Day of week must be between 1 and 7"
            });
        }

        // Check session type
        const validSessionTypes = [
            "LECTURE",
            "LAB",
            "TUTORIAL",
            "OTHER"
        ];

        const timetableSessionType =
            session_type || "LECTURE";

        if (!validSessionTypes.includes(timetableSessionType)) {
            return res.status(400).json({
                success: false,
                message: "Invalid session type"
            });
        }

        // If a lecturer is assigned to the timetable entry,
        // make sure that lecturer exists
        if (assigned_lecturer_id) {
            const [assignedLecturer] = await db.query(
                `SELECT lecturer_id
                 FROM lecturers
                 WHERE lecturer_id = ?
                   AND status = 'ACTIVE'`,
                [assigned_lecturer_id]
            );

            if (assignedLecturer.length === 0) {
                return res.status(404).json({
                    success: false,
                    message: "Assigned lecturer not found"
                });
            }
        }

        // Make sure the end time is after the start time
        if (end_time <= start_time) {
            return res.status(400).json({
                success: false,
                message: "End time must be after start time"
            });
        }

        const [result] = await db.query(
            `INSERT INTO timetable
             (
                course_id,
                lecturer_id,
                day_of_week,
                start_time,
                end_time,
                venue,
                session_type
             )
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [
                course_id,
                assigned_lecturer_id || null,
                day_of_week,
                start_time,
                end_time,
                venue,
                timetableSessionType
            ]
        );

        res.status(201).json({
            success: true,
            message: "Timetable entry created successfully",
            timetable_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Create timetable entry error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create timetable entry"
        });
    }
};


// Get all active timetable entries
const getTimetable = async (req, res) => {
    try {
        const [timetable] = await db.query(
            `SELECT
                t.timetable_id,
                t.course_id,
                c.course_code,
                c.course_name,
                t.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                t.day_of_week,
                t.start_time,
                t.end_time,
                t.venue,
                t.session_type
             FROM timetable t
             JOIN courses c
                ON t.course_id = c.course_id
             LEFT JOIN lecturers l
                ON t.lecturer_id = l.lecturer_id
             WHERE t.active = 1
             ORDER BY
                t.day_of_week,
                t.start_time`
        );

        res.status(200).json({
            success: true,
            timetable
        });

    } catch (error) {
        console.error(
            "Get timetable error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve timetable"
        });
    }
};


// Get timetable for a specific course
const getCourseTimetable = async (req, res) => {
    try {
        const { course_id } = req.params;

        const [timetable] = await db.query(
            `SELECT
                t.timetable_id,
                t.course_id,
                c.course_code,
                c.course_name,
                t.lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                t.day_of_week,
                t.start_time,
                t.end_time,
                t.venue,
                t.session_type
             FROM timetable t
             JOIN courses c
                ON t.course_id = c.course_id
             LEFT JOIN lecturers l
                ON t.lecturer_id = l.lecturer_id
             WHERE t.course_id = ?
               AND t.active = 1
             ORDER BY
                t.day_of_week,
                t.start_time`,
            [course_id]
        );

        res.status(200).json({
            success: true,
            timetable
        });

    } catch (error) {
        console.error(
            "Get course timetable error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve course timetable"
        });
    }
};


// Get timetable for a lecturer
const getLecturerTimetable = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const [timetable] = await db.query(
            `SELECT
                t.timetable_id,
                t.course_id,
                c.course_code,
                c.course_name,
                t.lecturer_id,
                t.day_of_week,
                t.start_time,
                t.end_time,
                t.venue,
                t.session_type
             FROM timetable t
             JOIN courses c
                ON t.course_id = c.course_id
             WHERE t.lecturer_id = ?
               AND t.active = 1
             ORDER BY
                t.day_of_week,
                t.start_time`,
            [lecturer_id]
        );

        res.status(200).json({
            success: true,
            timetable
        });

    } catch (error) {
        console.error(
            "Get lecturer timetable error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve lecturer timetable"
        });
    }
};


// Deactivate timetable entry
const deactivateTimetableEntry = async (req, res) => {
    try {
        const {
            timetable_id,
            lecturer_id
        } = req.params;

        const [entries] = await db.query(
            `SELECT timetable_id
             FROM timetable
             WHERE timetable_id = ?
               AND lecturer_id = ?`,
            [
                timetable_id,
                lecturer_id
            ]
        );

        if (entries.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Timetable entry not found"
            });
        }

        await db.query(
            `UPDATE timetable
             SET active = 0
             WHERE timetable_id = ?`,
            [timetable_id]
        );

        res.status(200).json({
            success: true,
            message: "Timetable entry deactivated successfully"
        });

    } catch (error) {
        console.error(
            "Deactivate timetable entry error:",
            error
        );

        res.status(500).json({
            success: false,
            message:
                "Could not deactivate timetable entry"
        });
    }
};


module.exports = {
    createTimetableEntry,
    getTimetable,
    getCourseTimetable,
    getLecturerTimetable,
    deactivateTimetableEntry
};