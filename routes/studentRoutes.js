const express = require("express");

const {
    getStudentProfile,
    getStudentCourses,
    getCoursesByYear,
    registerCourses
} = require("../controllers/studentController");

const {
    authenticateToken,
    requireRole,
    requireStudentAccess
} = require("../middleware/authMiddleware");

const router = express.Router();


// Student profile
router.get(
    "/:student_id",
    authenticateToken,
    requireRole("STUDENT"),
    requireStudentAccess,
    getStudentProfile
);


// Student registered courses
router.get(
    "/:student_id/courses",
    authenticateToken,
    requireRole("STUDENT"),
    requireStudentAccess,
    getStudentCourses
);


// Courses available for selected year
router.get(
    "/:student_id/courses/available",
    authenticateToken,
    requireRole("STUDENT"),
    requireStudentAccess,
    getCoursesByYear
);


// Register courses
router.post(
    "/:student_id/courses/register",
    authenticateToken,
    requireRole("STUDENT"),
    requireStudentAccess,
    registerCourses
);


module.exports = router;