const express = require("express");

const {
    createGroup,
    getGroups,
    getGroup,
    getGroupMembers,
    addStudentToGroup,
    removeStudentFromGroup,
    getStudentGroups
} = require("../controllers/groupController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Create a group
router.post(
    "/",
    authenticateToken,
    requireRole("LECTURER"),
    createGroup
);


// Get all active groups
router.get(
    "/",
    authenticateToken,
    getGroups
);


// Get groups belonging to a student
router.get(
    "/student/:student_id",
    authenticateToken,
    requireRole("STUDENT"),
    getStudentGroups
);


// Get one group
router.get(
    "/:group_id",
    authenticateToken,
    getGroup
);


// Get members of a group
router.get(
    "/:group_id/members",
    authenticateToken,
    getGroupMembers
);


// Add a student to a group
router.post(
    "/:group_id/members",
    authenticateToken,
    requireRole("LECTURER"),
    addStudentToGroup
);


// Remove a student from a group
router.put(
    "/:group_id/members/:student_id/end",
    authenticateToken,
    requireRole("LECTURER"),
    removeStudentFromGroup
);


module.exports = router;