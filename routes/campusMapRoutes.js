const express = require("express");

const {
    createLocation,
    getLocations,
    getLocation,
    deactivateLocation
} = require("../controllers/campusMapController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Lecturer adds a campus location
router.post(
    "/",
    authenticateToken,
    requireRole("LECTURER"),
    createLocation
);


// Authenticated users view all locations
router.get(
    "/",
    authenticateToken,
    getLocations
);


// Authenticated users view a specific location
router.get(
    "/:location_id",
    authenticateToken,
    getLocation
);


// Lecturer deactivates a location
router.put(
    "/:location_id/deactivate",
    authenticateToken,
    requireRole("LECTURER"),
    deactivateLocation
);

module.exports = router;