const db = require("../config/database");

const createAcademicLink = async (req, res) => {
    try {
        const {
            title,
            description,
            url,
            category,
            sort_order
        } = req.body;

        if (!title || !url) {
            return res.status(400).json({
                success: false,
                message: "Title and URL are required"
            });
        }

        const [result] = await db.query(
            `INSERT INTO academic_links
             (
                title,
                description,
                url,
                category,
                sort_order
             )
             VALUES (?, ?, ?, ?, ?)`,
            [
                title,
                description || null,
                url,
                category || null,
                sort_order || 0
            ]
        );

        res.status(201).json({
            success: true,
            message: "Academic link created successfully",
            link_id: result.insertId
        });

    } catch (error) {
        console.error("Create academic link error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create academic link"
        });
    }
};


const getAcademicLinks = async (req, res) => {
    try {
        const [links] = await db.query(
            `SELECT
                link_id,
                title,
                description,
                url,
                category,
                active,
                sort_order
             FROM academic_links
             WHERE active = 1
             ORDER BY sort_order, title`
        );

        res.status(200).json({
            success: true,
            links
        });

    } catch (error) {
        console.error("Get academic links error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve academic links"
        });
    }
};


const getAcademicLinksByCategory = async (req, res) => {
    try {
        const { category } = req.params;

        const [links] = await db.query(
            `SELECT
                link_id,
                title,
                description,
                url,
                category,
                active,
                sort_order
             FROM academic_links
             WHERE category = ?
               AND active = 1
             ORDER BY sort_order, title`,
            [category]
        );

        res.status(200).json({
            success: true,
            links
        });

    } catch (error) {
        console.error(
            "Get academic links by category error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve academic links"
        });
    }
};


const deactivateAcademicLink = async (req, res) => {
    try {
        const { link_id } = req.params;

        const [result] = await db.query(
            `UPDATE academic_links
             SET active = 0
             WHERE link_id = ?
               AND active = 1`,
            [link_id]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Academic link not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Academic link deactivated successfully"
        });

    } catch (error) {
        console.error(
            "Deactivate academic link error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not deactivate academic link"
        });
    }
};


module.exports = {
    createAcademicLink,
    getAcademicLinks,
    getAcademicLinksByCategory,
    deactivateAcademicLink
};