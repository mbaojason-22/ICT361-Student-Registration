const express = require("express");

const {
    saveDraftSubmission,
    submitAssignment,
    getStudentSubmission,
    getAssignmentSubmissions,
    gradeSubmission
} = require("../controllers/submissionController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Student saves or updates a draft
router.post(
    "/assignment/:assignment_id/student/:student_id/draft",
    authenticateToken,
    requireRole("STUDENT"),
    saveDraftSubmission
);


// Student submits an assignment
router.put(
    "/assignment/:assignment_id/student/:student_id/submit",
    authenticateToken,
    requireRole("STUDENT"),
    submitAssignment
);


// Student views their submission
router.get(
    "/assignment/:assignment_id/student/:student_id",
    authenticateToken,
    requireRole("STUDENT"),
    getStudentSubmission
);


// Lecturer views submissions for an assignment
router.get(
    "/assignment/:assignment_id/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    getAssignmentSubmissions
);


// Lecturer grades a submission
router.put(
    "/:submission_id/grade/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    gradeSubmission
);


module.exports = router;