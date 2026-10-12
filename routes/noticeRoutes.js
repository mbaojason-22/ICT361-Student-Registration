const express = require("express");

const {
    createNotice,
    getNotices,
    getCourseNotices,
    getGroupNotices,
    deactivateNotice
} = require("../controllers/noticeController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Lecturer creates a notice
router.post(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    createNotice
);


// Get all active notices
router.get(
    "/",
    authenticateToken,
    getNotices
);


// Get notices for a course
router.get(
    "/course/:course_id",
    authenticateToken,
    getCourseNotices
);


// Get notices for a group
router.get(
    "/group/:group_id",
    authenticateToken,
    getGroupNotices
);


// Lecturer deactivates a notice
router.put(
    "/:notice_id/deactivate/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    deactivateNotice
);


module.exports = router;