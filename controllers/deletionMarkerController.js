const db = require("../config/database");
const crypto = require("crypto");


const createDeletionMarker = async (req, res) => {
    try {
        const {
            entity_type,
            entity_id,
            deletion_version
        } = req.body;

        if (
            !entity_type ||
            !entity_id ||
            deletion_version === undefined
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Entity type, entity ID and deletion version are required"
            });
        }

        // Check whether this deletion marker already exists
        const [existing] = await db.query(
            `SELECT
                deletion_marker_id,
                entity_type,
                entity_id,
                deletion_version,
                deleted_at,
                marker
             FROM deletion_markers
             WHERE entity_type = ?
               AND entity_id = ?
               AND deletion_version = ?`,
            [
                entity_type,
                entity_id,
                deletion_version
            ]
        );

        if (existing.length > 0) {
            return res.status(200).json({
                success: true,
                message: "Deletion marker already exists",
                deletion_marker: existing[0]
            });
        }

        // Generate a unique SHA-256 marker
        const marker = crypto
            .createHash("sha256")
            .update(
                `${entity_type}:${entity_id}:${deletion_version}:${Date.now()}`
            )
            .digest("hex");

        const [result] = await db.query(
            `INSERT INTO deletion_markers
             (
                entity_type,
                entity_id,
                deletion_version,
                marker
             )
             VALUES (?, ?, ?, ?)`,
            [
                entity_type,
                entity_id,
                deletion_version,
                marker
            ]
        );

        const [rows] = await db.query(
            `SELECT
                deletion_marker_id,
                entity_type,
                entity_id,
                deletion_version,
                deleted_at,
                marker
             FROM deletion_markers
             WHERE deletion_marker_id = ?`,
            [result.insertId]
        );

        res.status(201).json({
            success: true,
            message: "Deletion marker created successfully",
            deletion_marker: rows[0]
        });

    } catch (error) {
        console.error(
            "Create deletion marker error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create deletion marker"
        });
    }
};


const getDeletionMarkers = async (req, res) => {
    try {
        const [markers] = await db.query(
            `SELECT
                deletion_marker_id,
                entity_type,
                entity_id,
                deletion_version,
                deleted_at,
                marker
             FROM deletion_markers
             ORDER BY deleted_at DESC`
        );

        res.status(200).json({
            success: true,
            deletion_markers: markers
        });

    } catch (error) {
        console.error(
            "Get deletion markers error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve deletion markers"
        });
    }
};


const getEntityDeletionMarkers = async (req, res) => {
    try {
        const {
            entity_type,
            entity_id
        } = req.params;

        const [markers] = await db.query(
            `SELECT
                deletion_marker_id,
                entity_type,
                entity_id,
                deletion_version,
                deleted_at,
                marker
             FROM deletion_markers
             WHERE entity_type = ?
               AND entity_id = ?
             ORDER BY deletion_version DESC`,
            [
                entity_type,
                entity_id
            ]
        );

        res.status(200).json({
            success: true,
            deletion_markers: markers
        });

    } catch (error) {
        console.error(
            "Get entity deletion markers error:",
            error
        );

        res.status(500).json({
            success: false,
            message:
                "Could not retrieve entity deletion markers"
        });
    }
};


module.exports = {
    createDeletionMarker,
    getDeletionMarkers,
    getEntityDeletionMarkers
};