const express = require("express");

const {
createRegistrationRequest,
getStudentRegistrationRequests,
getPendingRegistrationRequests,
approveRegistrationRequest,
rejectRegistrationRequest,
cancelRegistrationRequest
} = require("../controllers/courseRegistrationRequestController");

const {
authenticateToken,
requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();

// Student creates a registration request
router.post(
"/student/:student_id",
authenticateToken,
requireRole("STUDENT"),
createRegistrationRequest
);

// Student views their registration requests
router.get(
"/student/:student_id",
authenticateToken,
requireRole("STUDENT"),
getStudentRegistrationRequests
);

// Lecturer views pending registration requests
router.get(
"/lecturer/:lecturer_id/pending",
authenticateToken,
requireRole("LECTURER"),
getPendingRegistrationRequests
);

// Lecturer approves a registration request
router.put(
"/:registration_request_id/approve/:lecturer_id",
authenticateToken,
requireRole("LECTURER"),
approveRegistrationRequest
);

// Lecturer rejects a registration request
router.put(
"/:registration_request_id/reject/:lecturer_id",
authenticateToken,
requireRole("LECTURER"),
rejectRegistrationRequest
);

// Student cancels a registration request
router.put(
"/:registration_request_id/student/:student_id/cancel",
authenticateToken,
requireRole("STUDENT"),
cancelRegistrationRequest
);

module.exports = router;