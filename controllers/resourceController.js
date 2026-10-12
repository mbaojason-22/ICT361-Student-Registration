const db = require("../config/database");


// Create a resource
const createResource = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const {
            category_id,
            course_id,
            title,
            description,
            resource_type,
            file_url,
            external_url
        } = req.body;

        if (!category_id || !title || !resource_type) {
            return res.status(400).json({
                success: false,
                message: "Category ID, title and resource type are required"
            });
        }

        const validTypes = [
            "PDF",
            "DOCUMENT",
            "VIDEO",
            "LINK",
            "OTHER"
        ];

        if (!validTypes.includes(resource_type)) {
            return res.status(400).json({
                success: false,
                message: "Invalid resource type"
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

        // Check category
        const [categories] = await db.query(
            `SELECT category_id
             FROM resource_categories
             WHERE category_id = ?
               AND active = 1`,
            [category_id]
        );

        if (categories.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Resource category not found"
            });
        }

        // If a course is supplied, make sure it exists
        if (course_id) {
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
        }

        const [result] = await db.query(
            `INSERT INTO resources
             (
                category_id,
                course_id,
                title,
                description,
                resource_type,
                file_url,
                external_url,
                uploaded_by_lecturer_id
             )
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
            [
                category_id,
                course_id || null,
                title,
                description || null,
                resource_type,
                file_url || null,
                external_url || null,
                lecturer_id
            ]
        );

        res.status(201).json({
            success: true,
            message: "Resource created successfully",
            resource_id: result.insertId
        });

    } catch (error) {
        console.error("Create resource error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create resource"
        });
    }
};


// Get all active resources
const getResources = async (req, res) => {
    try {
        const [resources] = await db.query(
            `SELECT
                r.resource_id,
                r.category_id,
                rc.category_name,
                r.course_id,
                c.course_code,
                c.course_name,
                r.title,
                r.description,
                r.resource_type,
                r.file_url,
                r.external_url,
                r.uploaded_by_lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                r.created_at,
                r.updated_at
             FROM resources r
             JOIN resource_categories rc
                ON r.category_id = rc.category_id
             LEFT JOIN courses c
                ON r.course_id = c.course_id
             LEFT JOIN lecturers l
                ON r.uploaded_by_lecturer_id = l.lecturer_id
             WHERE r.active = 1
               AND rc.active = 1
             ORDER BY rc.sort_order, r.created_at DESC`
        );

        res.status(200).json({
            success: true,
            resources
        });

    } catch (error) {
        console.error("Get resources error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve resources"
        });
    }
};


// Get resources for a specific course
const getCourseResources = async (req, res) => {
    try {
        const { course_id } = req.params;

        const [resources] = await db.query(
            `SELECT
                r.resource_id,
                r.category_id,
                rc.category_name,
                r.course_id,
                c.course_code,
                c.course_name,
                r.title,
                r.description,
                r.resource_type,
                r.file_url,
                r.external_url,
                r.uploaded_by_lecturer_id,
                l.first_name AS lecturer_first_name,
                l.last_name AS lecturer_last_name,
                r.created_at,
                r.updated_at
             FROM resources r
             JOIN resource_categories rc
                ON r.category_id = rc.category_id
             JOIN courses c
                ON r.course_id = c.course_id
             LEFT JOIN lecturers l
                ON r.uploaded_by_lecturer_id = l.lecturer_id
             WHERE r.course_id = ?
               AND r.active = 1
               AND rc.active = 1
             ORDER BY rc.sort_order, r.created_at DESC`,
            [course_id]
        );

        res.status(200).json({
            success: true,
            resources
        });

    } catch (error) {
        console.error(
            "Get course resources error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve course resources"
        });
    }
};


// Get resources uploaded by a lecturer
const getLecturerResources = async (req, res) => {
    try {
        const { lecturer_id } = req.params;

        const [resources] = await db.query(
            `SELECT
                r.resource_id,
                r.category_id,
                rc.category_name,
                r.course_id,
                c.course_code,
                c.course_name,
                r.title,
                r.description,
                r.resource_type,
                r.file_url,
                r.external_url,
                r.active,
                r.created_at,
                r.updated_at
             FROM resources r
             JOIN resource_categories rc
                ON r.category_id = rc.category_id
             LEFT JOIN courses c
                ON r.course_id = c.course_id
             WHERE r.uploaded_by_lecturer_id = ?
             ORDER BY r.created_at DESC`,
            [lecturer_id]
        );

        res.status(200).json({
            success: true,
            resources
        });

    } catch (error) {
        console.error(
            "Get lecturer resources error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve lecturer resources"
        });
    }
};


// Deactivate a resource
const deactivateResource = async (req, res) => {
    try {
        const {
            resource_id,
            lecturer_id
        } = req.params;

        const [resources] = await db.query(
            `SELECT resource_id
             FROM resources
             WHERE resource_id = ?
               AND uploaded_by_lecturer_id = ?`,
            [
                resource_id,
                lecturer_id
            ]
        );

        if (resources.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Resource not found"
            });
        }

        await db.query(
            `UPDATE resources
             SET active = 0
             WHERE resource_id = ?`,
            [resource_id]
        );

        res.status(200).json({
            success: true,
            message: "Resource deactivated successfully"
        });

    } catch (error) {
        console.error(
            "Deactivate resource error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not deactivate resource"
        });
    }
};


module.exports = {
    createResource,
    getResources,
    getCourseResources,
    getLecturerResources,
    deactivateResource
};