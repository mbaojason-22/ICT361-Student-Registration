const express = require("express");

const {
createDeletionMarker,
getDeletionMarkers,
getEntityDeletionMarkers
} = require("../controllers/deletionMarkerController");

const {
authenticateToken
} = require("../middleware/authMiddleware");

const router = express.Router();

// Create a deletion marker
router.post(
"/",
authenticateToken,
createDeletionMarker
);

// Get deletion markers
router.get(
"/",
authenticateToken,
getDeletionMarkers
);

// Get deletion markers for a specific entity
router.get(
"/:entity_type/:entity_id",
authenticateToken,
getEntityDeletionMarkers
);

module.exports = router;