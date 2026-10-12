const express = require("express");

const {
    createGroupChangeRequest,
    getStudentGroupChangeRequests,
    getPendingGroupChangeRequests,
    approveGroupChangeRequest,
    rejectGroupChangeRequest,
    cancelGroupChangeRequest
} = require("../controllers/groupChangeController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Student submits a group change request
router.post(
    "/student/:student_id",
    authenticateToken,
    requireRole("STUDENT"),
    createGroupChangeRequest
);


// Student views their requests
router.get(
    "/student/:student_id",
    authenticateToken,
    requireRole("STUDENT"),
    getStudentGroupChangeRequests
);


// Lecturer views pending requests
router.get(
    "/pending",
    authenticateToken,
    requireRole("LECTURER"),
    getPendingGroupChangeRequests
);


// Lecturer approves a request
router.put(
    "/:request_id/approve/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    approveGroupChangeRequest
);


// Lecturer rejects a request
router.put(
    "/:request_id/reject/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    rejectGroupChangeRequest
);


// Student cancels their request
router.put(
    "/student/:student_id/:request_id/cancel",
    authenticateToken,
    requireRole("STUDENT"),
    cancelGroupChangeRequest
);


module.exports = router;