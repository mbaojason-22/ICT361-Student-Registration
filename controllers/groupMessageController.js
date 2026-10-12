const db = require("../config/database");


// Send a message to a group
const sendMessage = async (req, res) => {
    try {
        const { group_id } = req.params;
        const {
            sender_account_id,
            message_text,
            client_operation_id
        } = req.body;

        if (!sender_account_id || !message_text) {
            return res.status(400).json({
                success: false,
                message: "Sender account ID and message text are required"
            });
        }

        // Check that the group exists
        const [groups] = await db.query(
            `SELECT group_id, active
             FROM lab_groups
             WHERE group_id = ?`,
            [group_id]
        );

        if (groups.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Group not found"
            });
        }

        if (groups[0].active !== 1) {
            return res.status(400).json({
                success: false,
                message: "This group is not active"
            });
        }

        // Check that the sender account exists
        const [accounts] = await db.query(
            `SELECT account_id
             FROM accounts
             WHERE account_id = ?
               AND is_active = 1`,
            [sender_account_id]
        );

        if (accounts.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Sender account not found"
            });
        }

        // Make sure the sender is actually a member of the group
        const [membership] = await db.query(
            `SELECT gm.membership_id
             FROM group_memberships gm
             JOIN accounts a
                ON a.student_id = gm.student_id
             WHERE gm.group_id = ?
               AND gm.status = 'ACTIVE'
               AND a.account_id = ?`,
            [group_id, sender_account_id]
        );

        if (membership.length === 0) {
            return res.status(403).json({
                success: false,
                message: "Sender is not an active member of this group"
            });
        }

        // Prevent duplicate messages when a client retries the same operation
        if (client_operation_id) {
            const [existingMessage] = await db.query(
                `SELECT
                    message_id,
                    group_id,
                    sender_account_id,
                    message_text,
                    sent_at,
                    edited_at,
                    deleted_at,
                    client_operation_id,
                    version
                 FROM group_messages
                 WHERE client_operation_id = ?`,
                [client_operation_id]
            );

            if (existingMessage.length > 0) {
                return res.status(200).json({
                    success: true,
                    message: "Message already exists",
                    group_message: existingMessage[0]
                });
            }
        }

        const [result] = await db.query(
            `INSERT INTO group_messages
             (
                group_id,
                sender_account_id,
                message_text,
                client_operation_id
             )
             VALUES (?, ?, ?, ?)`,
            [
                group_id,
                sender_account_id,
                message_text,
                client_operation_id || null
            ]
        );

        const [messages] = await db.query(
            `SELECT
                gm.message_id,
                gm.group_id,
                gm.sender_account_id,
                s.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                gm.message_text,
                gm.sent_at,
                gm.edited_at,
                gm.deleted_at,
                gm.client_operation_id,
                gm.version
             FROM group_messages gm
             JOIN accounts a
                ON gm.sender_account_id = a.account_id
             LEFT JOIN students s
                ON a.student_id = s.student_id
             WHERE gm.message_id = ?`,
            [result.insertId]
        );

        res.status(201).json({
            success: true,
            message: "Message sent successfully",
            group_message: messages[0]
        });

    } catch (error) {
        console.error("Send message error:", error);

        res.status(500).json({
            success: false,
            message: "Could not send message"
        });
    }
};


// Get messages in a group
const getGroupMessages = async (req, res) => {
    try {
        const { group_id } = req.params;

        const [messages] = await db.query(
            `SELECT
                gm.message_id,
                gm.group_id,
                gm.sender_account_id,
                s.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                gm.message_text,
                gm.sent_at,
                gm.edited_at,
                gm.deleted_at,
                gm.client_operation_id,
                gm.version
             FROM group_messages gm
             JOIN accounts a
                ON gm.sender_account_id = a.account_id
             LEFT JOIN students s
                ON a.student_id = s.student_id
             WHERE gm.group_id = ?
               AND gm.deleted_at IS NULL
             ORDER BY gm.sent_at ASC`,
            [group_id]
        );

        res.status(200).json({
            success: true,
            messages
        });

    } catch (error) {
        console.error("Get group messages error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve group messages"
        });
    }
};


module.exports = {
    sendMessage,
    getGroupMessages
};