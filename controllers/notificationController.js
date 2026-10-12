const db = require("../config/database");


// Create a notification
const createNotification = async (req, res) => {
    try {
        const {
            account_id,
            title,
            message,
            notification_type,
            related_id
        } = req.body;

        if (!account_id || !title || !message) {
            return res.status(400).json({
                success: false,
                message: "Account ID, title and message are required"
            });
        }

        const validTypes = [
            "NOTICE",
            "ASSIGNMENT",
            "GROUP_REQUEST",
            "SYSTEM",
            "SYNC"
        ];

        const type = notification_type || "SYSTEM";

        if (!validTypes.includes(type)) {
            return res.status(400).json({
                success: false,
                message: "Invalid notification type"
            });
        }

        // Check account
        const [accounts] = await db.query(
            `SELECT account_id
             FROM accounts
             WHERE account_id = ?
               AND is_active = 1`,
            [account_id]
        );

        if (accounts.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Account not found"
            });
        }

        const [result] = await db.query(
            `INSERT INTO notifications
             (
                account_id,
                title,
                message,
                notification_type,
                related_id
             )
             VALUES (?, ?, ?, ?, ?)`,
            [
                account_id,
                title,
                message,
                type,
                related_id || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Notification created successfully",
            notification_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Create notification error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create notification"
        });
    }
};


// Get notifications for an account
const getAccountNotifications = async (req, res) => {
    try {
        const { account_id } = req.params;

        const [notifications] = await db.query(
            `SELECT
                notification_id,
                account_id,
                title,
                message,
                notification_type,
                related_id,
                is_read,
                created_at
             FROM notifications
             WHERE account_id = ?
             ORDER BY created_at DESC`,
            [account_id]
        );

        res.status(200).json({
            success: true,
            notifications
        });

    } catch (error) {
        console.error(
            "Get notifications error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve notifications"
        });
    }
};


// Get unread notifications
const getUnreadNotifications = async (req, res) => {
    try {
        const { account_id } = req.params;

        const [notifications] = await db.query(
            `SELECT
                notification_id,
                account_id,
                title,
                message,
                notification_type,
                related_id,
                is_read,
                created_at
             FROM notifications
             WHERE account_id = ?
               AND is_read = 0
             ORDER BY created_at DESC`,
            [account_id]
        );

        res.status(200).json({
            success: true,
            notifications
        });

    } catch (error) {
        console.error(
            "Get unread notifications error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve unread notifications"
        });
    }
};


// Mark one notification as read
const markNotificationAsRead = async (req, res) => {
    try {
        const {
            notification_id,
            account_id
        } = req.params;

        const [result] = await db.query(
            `UPDATE notifications
             SET is_read = 1
             WHERE notification_id = ?
               AND account_id = ?`,
            [
                notification_id,
                account_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Notification not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Notification marked as read"
        });

    } catch (error) {
        console.error(
            "Mark notification as read error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not mark notification as read"
        });
    }
};


// Mark all notifications as read
const markAllNotificationsAsRead = async (req, res) => {
    try {
        const { account_id } = req.params;

        await db.query(
            `UPDATE notifications
             SET is_read = 1
             WHERE account_id = ?
               AND is_read = 0`,
            [account_id]
        );

        res.status(200).json({
            success: true,
            message: "All notifications marked as read"
        });

    } catch (error) {
        console.error(
            "Mark all notifications as read error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not mark notifications as read"
        });
    }
};


// Get notifications by type
const getNotificationsByType = async (req, res) => {
    try {
        const {
            account_id,
            notification_type
        } = req.params;

        const validTypes = [
            "NOTICE",
            "ASSIGNMENT",
            "GROUP_REQUEST",
            "SYSTEM",
            "SYNC"
        ];

        if (!validTypes.includes(notification_type)) {
            return res.status(400).json({
                success: false,
                message: "Invalid notification type"
            });
        }

        const [notifications] = await db.query(
            `SELECT
                notification_id,
                account_id,
                title,
                message,
                notification_type,
                related_id,
                is_read,
                created_at
             FROM notifications
             WHERE account_id = ?
               AND notification_type = ?
             ORDER BY created_at DESC`,
            [
                account_id,
                notification_type
            ]
        );

        res.status(200).json({
            success: true,
            notifications
        });

    } catch (error) {
        console.error(
            "Get notifications by type error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve notifications"
        });
    }
};


module.exports = {
    createNotification,
    getAccountNotifications,
    getUnreadNotifications,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    getNotificationsByType
};