const express = require("express");

const {
createSyncChange,
getPendingSyncChanges,
getSyncChanges,
markSyncChangeApplied,
markSyncChangeConflict,
markSyncChangeRejected,
createOperationReceipt,
getOperationReceipt
} = require("../controllers/syncController");

const {
authenticateToken
} = require("../middleware/authMiddleware");

const router = express.Router();

// Create a sync change
router.post(
"/",
authenticateToken,
createSyncChange
);

// Get sync changes for an account
router.get(
"/account/:account_id",
authenticateToken,
getSyncChanges
);

// Get pending sync changes
router.get(
"/account/:account_id/pending",
authenticateToken,
getPendingSyncChanges
);

// Mark a sync change as applied
router.put(
"/:sync_change_id/applied",
authenticateToken,
markSyncChangeApplied
);

// Mark a sync change as conflicted
router.put(
"/:sync_change_id/conflict",
authenticateToken,
markSyncChangeConflict
);

// Mark a sync change as rejected
router.put(
"/:sync_change_id/rejected",
authenticateToken,
markSyncChangeRejected
);

// Create an operation receipt
router.post(
"/receipts",
authenticateToken,
createOperationReceipt
);

// Get an operation receipt
router.get(
"/receipts/:operation_id",
authenticateToken,
getOperationReceipt
);

module.exports = router;