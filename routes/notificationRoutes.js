const express = require("express");

const {
    createNotification,
    getAccountNotifications,
    getUnreadNotifications,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    getNotificationsByType
} = require("../controllers/notificationController");

const {
    authenticateToken,
    requireRole
} = require("../middleware/authMiddleware");

const router = express.Router();


// Create notification
// Normally used by lecturers/system
router.post(
    "/",
    authenticateToken,
    requireRole("LECTURER"),
    createNotification
);


// Get all notifications for an account
router.get(
    "/account/:account_id",
    authenticateToken,
    getAccountNotifications
);


// Get unread notifications
router.get(
    "/account/:account_id/unread",
    authenticateToken,
    getUnreadNotifications
);


// Get notifications by type
router.get(
    "/account/:account_id/type/:notification_type",
    authenticateToken,
    getNotificationsByType
);


// Mark one notification as read
router.put(
    "/:notification_id/read/:account_id",
    authenticateToken,
    markNotificationAsRead
);


// Mark all notifications as read
router.put(
    "/account/:account_id/read-all",
    authenticateToken,
    markAllNotificationsAsRead
);


module.exports = router;