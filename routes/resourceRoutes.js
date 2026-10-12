const express = require("express");

const {
    createResource,
    getResources,
    getCourseResources,
    getLecturerResources,
    deactivateResource
} = require("../controllers/resourceController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Lecturer creates a resource
router.post(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    createResource
);


// Authenticated users view active resources
router.get(
    "/",
    authenticateToken,
    getResources
);


// Authenticated users view resources for a course
router.get(
    "/course/:course_id",
    authenticateToken,
    getCourseResources
);


// Lecturer views their uploaded resources
router.get(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    getLecturerResources
);


// Lecturer deactivates a resource
router.put(
    "/:resource_id/deactivate/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    deactivateResource
);


module.exports = router;