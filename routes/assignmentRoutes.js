const express = require("express");

const {
    createAssignment,
    getLecturerAssignments,
    getCourseAssignments,
    updateAssignmentStatus
} = require("../controllers/assignmentController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Lecturer creates an assignment
router.post(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    createAssignment
);


// Lecturer views their assignments
router.get(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    getLecturerAssignments
);


// Students and lecturers view course assignments
router.get(
    "/course/:course_id",
    authenticateToken,
    getCourseAssignments
);


// Lecturer updates assignment status
router.put(
    "/:assignment_id/status/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    updateAssignmentStatus
);


module.exports = router;