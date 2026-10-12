const express = require("express");

const {
createAcademicLink,
getAcademicLinks,
getAcademicLinksByCategory,
deactivateAcademicLink
} = require("../controllers/academicLinkController");

const {
authenticateToken,
requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();

// Lecturer adds an academic link
router.post(
"/",
authenticateToken,
requireRole("LECTURER"),
createAcademicLink
);

// Authenticated users view all academic links
router.get(
"/",
authenticateToken,
getAcademicLinks
);

// Authenticated users view links by category
router.get(
"/category/:category",
authenticateToken,
getAcademicLinksByCategory
);

// Lecturer deactivates an academic link
router.put(
"/:link_id/deactivate",
authenticateToken,
requireRole("LECTURER"),
deactivateAcademicLink
);

module.exports = router;