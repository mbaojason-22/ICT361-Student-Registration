const db = require("../config/database");


// Get student profile
const getStudentProfile = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [students] = await db.query(
            `SELECT
                s.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                s.programme_id,
                p.programme_code,
                p.programme_name,
                s.status
             FROM students s
             JOIN programmes p
                ON s.programme_id = p.programme_id
             WHERE s.student_id = ?`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Student not found"
            });
        }

        res.status(200).json({
            success: true,
            student: students[0]
        });

    } catch (error) {
        console.error("Get student profile error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve student profile"
        });
    }
};


// Get student's registered courses
const getStudentCourses = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [courses] = await db.query(
            `SELECT
                ce.enrolment_id,
                ce.student_id,
                ce.course_id,
                c.course_code,
                c.course_name,
                c.description,
                c.year_level,
                ce.status,
                ce.approval_status,
                ce.enrolled_at
             FROM course_enrolments ce
             JOIN courses c
                ON ce.course_id = c.course_id
             WHERE ce.student_id = ?
             ORDER BY c.year_level, c.course_code`,
            [student_id]
        );

        res.status(200).json({
            success: true,
            courses
        });

    } catch (error) {
        console.error("Get student courses error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve student courses"
        });
    }
};


// Get courses available for student's programme and selected year
const getCoursesByYear = async (req, res) => {
    try {
        const { student_id } = req.params;
        const { year_level } = req.query;

        if (!year_level) {
            return res.status(400).json({
                success: false,
                message: "Year level is required"
            });
        }

        const year = Number(year_level);

        if (!Number.isInteger(year) || year < 1 || year > 4) {
            return res.status(400).json({
                success: false,
                message: "Year level must be between 1 and 4"
            });
        }

        // Get student's programme
        const [students] = await db.query(
            `SELECT
                s.programme_id,
                p.programme_code,
                p.programme_name
             FROM students s
             JOIN programmes p
                ON s.programme_id = p.programme_id
             WHERE s.student_id = ?
               AND s.status = 'ACTIVE'`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Active student not found"
            });
        }

        const student = students[0];

        // Get courses shared with student's programme
        // and belonging to selected year
        const [courses] = await db.query(
            `SELECT
                c.course_id,
                c.course_code,
                c.course_name,
                c.description,
                c.year_level,
                c.active
             FROM courses c
             JOIN course_programmes cp
                ON c.course_id = cp.course_id
             WHERE cp.programme_id = ?
               AND c.year_level = ?
               AND c.active = 1
             ORDER BY c.course_code`,
            [
                student.programme_id,
                year
            ]
        );

        res.status(200).json({
            success: true,
            programme: {
                programme_id: student.programme_id,
                programme_code: student.programme_code,
                programme_name: student.programme_name
            },
            year_level: year,
            courses
        });

    } catch (error) {
        console.error("Get courses by year error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve courses for selected year"
        });
    }
};


// Register selected courses
const registerCourses = async (req, res) => {
    try {
        const { student_id } = req.params;
        const { course_ids, year_level } = req.body;

        if (
            !year_level ||
            !Number.isInteger(Number(year_level)) ||
            Number(year_level) < 1 ||
            Number(year_level) > 4
        ) {
            return res.status(400).json({
                success: false,
                message: "Please select a valid year level"
            });
        }

        if (
            !course_ids ||
            !Array.isArray(course_ids) ||
            course_ids.length === 0
        ) {
            return res.status(400).json({
                success: false,
                message: "Please select at least one course"
            });
        }

        const year = Number(year_level);

        // Get student's programme
        const [students] = await db.query(
            `SELECT programme_id
             FROM students
             WHERE student_id = ?
               AND status = 'ACTIVE'`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Active student not found"
            });
        }

        const programme_id = students[0].programme_id;

        // Remove duplicate course IDs from request
        const uniqueCourseIds = [
            ...new Set(course_ids.map(Number))
        ];

        // Make sure all selected courses belong to
        // the student's programme and selected year
        const placeholders = uniqueCourseIds
            .map(() => "?")
            .join(",");

        const [validCourses] = await db.query(
            `SELECT
                c.course_id
             FROM courses c
             JOIN course_programmes cp
                ON c.course_id = cp.course_id
             WHERE c.course_id IN (${placeholders})
               AND cp.programme_id = ?
               AND c.year_level = ?
               AND c.active = 1`,
            [
                ...uniqueCourseIds,
                programme_id,
                year
            ]
        );

        if (validCourses.length !== uniqueCourseIds.length) {
            return res.status(400).json({
                success: false,
                message:
                    "One or more selected courses do not belong to your programme or selected year"
            });
        }

        // Register each selected course
        for (const course_id of uniqueCourseIds) {

            const [existing] = await db.query(
                `SELECT
                    enrolment_id,
                    approval_status
                 FROM course_enrolments
                 WHERE student_id = ?
                   AND course_id = ?`,
                [
                    student_id,
                    course_id
                ]
            );

            if (existing.length > 0) {

                const existingEnrollment = existing[0];

                // Allow a previously rejected course
                // to be registered again
                if (
                    existingEnrollment.approval_status === "REJECTED"
                ) {
                    await db.query(
                        `UPDATE course_enrolments
                         SET
                            approval_status = 'PENDING',
                            status = 'ENROLLED',
                            enrolled_at = NOW()
                         WHERE enrolment_id = ?`,
                        [
                            existingEnrollment.enrolment_id
                        ]
                    );
                }

                continue;
            }

            await db.query(
                `INSERT INTO course_enrolments
                 (
                    student_id,
                    course_id,
                    status,
                    approval_status
                 )
                 VALUES (?, ?, 'ENROLLED', 'PENDING')`,
                [
                    student_id,
                    course_id
                ]
            );
        }

        res.status(201).json({
            success: true,
            message:
                "Courses registered successfully and are waiting for lecturer approval",
            year_level: year,
            course_count: uniqueCourseIds.length
        });

    } catch (error) {
        console.error("Register courses error:", error);

        res.status(500).json({
            success: false,
            message: "Could not register courses"
        });
    }
};


module.exports = {
    getStudentProfile,
    getStudentCourses,
    getCoursesByYear,
    registerCourses
};