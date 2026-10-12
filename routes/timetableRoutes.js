const express = require("express");

const {
    createTimetableEntry,
    getTimetable,
    getCourseTimetable,
    getLecturerTimetable,
    deactivateTimetableEntry
} = require("../controllers/timetableController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Lecturer creates timetable entry
router.post(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    createTimetableEntry
);


// Authenticated users view active timetable
router.get(
    "/",
    authenticateToken,
    getTimetable
);


// Authenticated users view timetable for a course
router.get(
    "/course/:course_id",
    authenticateToken,
    getCourseTimetable
);


// Lecturer views their timetable
router.get(
    "/lecturer/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    getLecturerTimetable
);


// Lecturer deactivates timetable entry
router.put(
    "/:timetable_id/deactivate/:lecturer_id",
    authenticateToken,
    requireRole("LECTURER"),
    deactivateTimetableEntry
);


module.exports = router;