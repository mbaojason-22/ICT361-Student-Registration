const express = require("express");

const {
createCorrectionRequest,
getStudentCorrectionRequests,
getPendingCorrectionRequests,
approveCorrectionRequest,
rejectCorrectionRequest,
cancelCorrectionRequest
} = require("../controllers/studentNumberCorrectionController");

const {
authenticateToken,
requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();

// Student creates a correction request
router.post(
"/student/:student_id",
authenticateToken,
requireRole("STUDENT"),
createCorrectionRequest
);

// Student views their correction requests
router.get(
"/student/:student_id",
authenticateToken,
requireRole("STUDENT"),
getStudentCorrectionRequests
);

// Lecturer views pending correction requests
router.get(
"/pending",
authenticateToken,
requireRole("LECTURER"),
getPendingCorrectionRequests
);

// Lecturer approves a correction request
router.put(
"/:correction_request_id/approve/:lecturer_id",
authenticateToken,
requireRole("LECTURER"),
approveCorrectionRequest
);

// Lecturer rejects a correction request
router.put(
"/:correction_request_id/reject/:lecturer_id",
authenticateToken,
requireRole("LECTURER"),
rejectCorrectionRequest
);

// Student cancels their correction request
router.put(
"/:correction_request_id/student/:student_id/cancel",
authenticateToken,
requireRole("STUDENT"),
cancelCorrectionRequest
);

module.exports = router;