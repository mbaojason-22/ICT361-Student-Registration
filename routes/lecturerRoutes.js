const express = require("express");

const {
getPendingRegistrations,
approveRegistration,
rejectRegistration,
getAllStudents,
addStudent
} = require("../controllers/lecturerController");

const {
authenticateToken,
requireRole,
requireLecturerAccess
} = require("../middleware/authMiddleware");

const router = express.Router();

// Course registration management
router.get(
"/:lecturer_id/registrations/pending",
authenticateToken,
requireRole("LECTURER"),
requireLecturerAccess,
getPendingRegistrations
);

router.put(
"/:lecturer_id/registrations/:enrollment_id/approve",
authenticateToken,
requireRole("LECTURER"),
requireLecturerAccess,
approveRegistration
);

router.put(
"/:lecturer_id/registrations/:enrollment_id/reject",
authenticateToken,
requireRole("LECTURER"),
requireLecturerAccess,
rejectRegistration
);

// Student management
router.get(
"/:lecturer_id/students",
authenticateToken,
requireRole("LECTURER"),
requireLecturerAccess,
getAllStudents
);

router.post(
"/:lecturer_id/students",
authenticateToken,
requireRole("LECTURER"),
requireLecturerAccess,
addStudent
);

module.exports = router;