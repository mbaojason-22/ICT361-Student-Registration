const db = require("../config/database");

const createLocation = async (req, res) => {
    try {
        const {
            name,
            description,
            building_code,
            map_url,
            latitude,
            longitude
        } = req.body;

        if (!name) {
            return res.status(400).json({
                success: false,
                message: "Location name is required"
            });
        }

        const [result] = await db.query(
            `INSERT INTO campus_map_locations
             (
                name,
                description,
                building_code,
                map_url,
                latitude,
                longitude
             )
             VALUES (?, ?, ?, ?, ?, ?)`,
            [
                name,
                description || null,
                building_code || null,
                map_url || null,
                latitude || null,
                longitude || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Campus location created successfully",
            location_id: result.insertId
        });

    } catch (error) {
        console.error("Create campus location error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create campus location"
        });
    }
};


const getLocations = async (req, res) => {
    try {
        const [locations] = await db.query(
            `SELECT
                location_id,
                name,
                description,
                building_code,
                map_url,
                latitude,
                longitude,
                active
             FROM campus_map_locations
             WHERE active = 1
             ORDER BY name`
        );

        res.status(200).json({
            success: true,
            locations
        });

    } catch (error) {
        console.error("Get campus locations error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve campus locations"
        });
    }
};


const getLocation = async (req, res) => {
    try {
        const { location_id } = req.params;

        const [locations] = await db.query(
            `SELECT
                location_id,
                name,
                description,
                building_code,
                map_url,
                latitude,
                longitude,
                active
             FROM campus_map_locations
             WHERE location_id = ?
               AND active = 1`,
            [location_id]
        );

        if (locations.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Campus location not found"
            });
        }

        res.status(200).json({
            success: true,
            location: locations[0]
        });

    } catch (error) {
        console.error("Get campus location error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve campus location"
        });
    }
};


const deactivateLocation = async (req, res) => {
    try {
        const { location_id } = req.params;

        const [result] = await db.query(
            `UPDATE campus_map_locations
             SET active = 0
             WHERE location_id = ?
               AND active = 1`,
            [location_id]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Campus location not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Campus location deactivated successfully"
        });

    } catch (error) {
        console.error(
            "Deactivate campus location error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not deactivate campus location"
        });
    }
};


module.exports = {
    createLocation,
    getLocations,
    getLocation,
    deactivateLocation
};