const db = require("../config/database");

const createSyncChange = async (req, res) => {
    try {
        const {
            operation_id,
            account_id,
            entity_type,
            entity_id,
            operation_type,
            payload,
            base_version
        } = req.body;

        if (
            !operation_id ||
            !account_id ||
            !entity_type ||
            !entity_id ||
            !operation_type ||
            payload === undefined
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Operation ID, account ID, entity type, entity ID, operation type and payload are required"
            });
        }

        const validOperations = [
            "CREATE",
            "UPDATE",
            "DELETE",
            "ASSIGN",
            "TRANSFER"
        ];

        if (!validOperations.includes(operation_type)) {
            return res.status(400).json({
                success: false,
                message: "Invalid operation type"
            });
        }

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

        const [existingOperation] = await db.query(
            `SELECT
                sync_change_id,
                operation_id,
                account_id,
                entity_type,
                entity_id,
                operation_type,
                payload,
                base_version,
                status,
                server_version,
                error_code,
                error_message,
                created_at,
                processed_at
             FROM sync_changes
             WHERE operation_id = ?`,
            [operation_id]
        );

        if (existingOperation.length > 0) {
            return res.status(200).json({
                success: true,
                message: "Sync operation already exists",
                sync_change: existingOperation[0]
            });
        }

        const [result] = await db.query(
            `INSERT INTO sync_changes
             (
                operation_id,
                account_id,
                entity_type,
                entity_id,
                operation_type,
                payload,
                base_version
             )
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [
                operation_id,
                account_id,
                entity_type,
                entity_id,
                operation_type,
                JSON.stringify(payload),
                base_version || null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Sync change created successfully",
            sync_change_id: result.insertId,
            status: "PENDING"
        });

    } catch (error) {
        console.error("Create sync change error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create sync change"
        });
    }
};


const getPendingSyncChanges = async (req, res) => {
    try {
        const { account_id } = req.params;

        const [changes] = await db.query(
            `SELECT
                sync_change_id,
                operation_id,
                account_id,
                entity_type,
                entity_id,
                operation_type,
                payload,
                base_version,
                status,
                server_version,
                error_code,
                error_message,
                created_at,
                processed_at
             FROM sync_changes
             WHERE account_id = ?
               AND status = 'PENDING'
             ORDER BY created_at ASC`,
            [account_id]
        );

        res.status(200).json({
            success: true,
            changes
        });

    } catch (error) {
        console.error(
            "Get pending sync changes error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve pending sync changes"
        });
    }
};


const getSyncChanges = async (req, res) => {
    try {
        const { account_id } = req.params;

        const [changes] = await db.query(
            `SELECT
                sync_change_id,
                operation_id,
                account_id,
                entity_type,
                entity_id,
                operation_type,
                payload,
                base_version,
                status,
                server_version,
                error_code,
                error_message,
                created_at,
                processed_at
             FROM sync_changes
             WHERE account_id = ?
             ORDER BY created_at DESC`,
            [account_id]
        );

        res.status(200).json({
            success: true,
            changes
        });

    } catch (error) {
        console.error("Get sync changes error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve sync changes"
        });
    }
};


const markSyncChangeApplied = async (req, res) => {
    try {
        const { sync_change_id } = req.params;
        const { server_version } = req.body || {};

        const [result] = await db.query(
            `UPDATE sync_changes
             SET
                status = 'APPLIED',
                server_version = ?,
                processed_at = NOW(),
                error_code = NULL,
                error_message = NULL
             WHERE sync_change_id = ?
               AND status = 'PENDING'`,
            [
                server_version || null,
                sync_change_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending sync change not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Sync change marked as applied"
        });

    } catch (error) {
        console.error(
            "Mark sync change applied error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not update sync change"
        });
    }
};


const markSyncChangeConflict = async (req, res) => {
    try {
        const { sync_change_id } = req.params;

        const {
            error_code,
            error_message,
            server_version
        } = req.body || {};

        const [result] = await db.query(
            `UPDATE sync_changes
             SET
                status = 'CONFLICT',
                server_version = ?,
                error_code = ?,
                error_message = ?,
                processed_at = NOW()
             WHERE sync_change_id = ?
               AND status = 'PENDING'`,
            [
                server_version || null,
                error_code || "SYNC_CONFLICT",
                error_message || "Sync conflict detected",
                sync_change_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending sync change not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Sync change marked as conflict"
        });

    } catch (error) {
        console.error(
            "Mark sync change conflict error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not update sync change"
        });
    }
};


const markSyncChangeRejected = async (req, res) => {
    try {
        const { sync_change_id } = req.params;

        const {
            error_code,
            error_message
        } = req.body || {};

        const [result] = await db.query(
            `UPDATE sync_changes
             SET
                status = 'REJECTED',
                error_code = ?,
                error_message = ?,
                processed_at = NOW()
             WHERE sync_change_id = ?
               AND status = 'PENDING'`,
            [
                error_code || "SYNC_REJECTED",
                error_message || "Sync change rejected",
                sync_change_id
            ]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Pending sync change not found"
            });
        }

        res.status(200).json({
            success: true,
            message: "Sync change marked as rejected"
        });

    } catch (error) {
        console.error(
            "Mark sync change rejected error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not update sync change"
        });
    }
};


const createOperationReceipt = async (req, res) => {
    try {
        const {
            operation_id,
            account_id,
            request_hash,
            response_code,
            response_payload
        } = req.body;

        if (
            !operation_id ||
            !account_id ||
            !request_hash ||
            !response_code
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Operation ID, account ID, request hash and response code are required"
            });
        }

        if (!/^[a-fA-F0-9]{64}$/.test(request_hash)) {
            return res.status(400).json({
                success: false,
                message:
                    "Request hash must be a valid 64-character hexadecimal hash"
            });
        }

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

        const [existingReceipt] = await db.query(
            `SELECT
                receipt_id,
                operation_id,
                account_id,
                request_hash,
                response_code,
                response_payload,
                created_at
             FROM sync_operation_receipts
             WHERE operation_id = ?`,
            [operation_id]
        );

        if (existingReceipt.length > 0) {
            return res.status(200).json({
                success: true,
                message: "Operation receipt already exists",
                receipt: existingReceipt[0]
            });
        }

        const [result] = await db.query(
            `INSERT INTO sync_operation_receipts
             (
                operation_id,
                account_id,
                request_hash,
                response_code,
                response_payload
             )
             VALUES (?, ?, ?, ?, ?)`,
            [
                operation_id,
                account_id,
                request_hash,
                response_code,
                response_payload !== undefined
                    ? JSON.stringify(response_payload)
                    : null
            ]
        );

        res.status(201).json({
            success: true,
            message: "Operation receipt created successfully",
            receipt_id: result.insertId
        });

    } catch (error) {
        console.error(
            "Create operation receipt error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not create operation receipt"
        });
    }
};


const getOperationReceipt = async (req, res) => {
    try {
        const { operation_id } = req.params;

        const [receipts] = await db.query(
            `SELECT
                receipt_id,
                operation_id,
                account_id,
                request_hash,
                response_code,
                response_payload,
                created_at
             FROM sync_operation_receipts
             WHERE operation_id = ?`,
            [operation_id]
        );

        if (receipts.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Operation receipt not found"
            });
        }

        res.status(200).json({
            success: true,
            receipt: receipts[0]
        });

    } catch (error) {
        console.error(
            "Get operation receipt error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Could not retrieve operation receipt"
        });
    }
};


module.exports = {
    createSyncChange,
    getPendingSyncChanges,
    getSyncChanges,
    markSyncChangeApplied,
    markSyncChangeConflict,
    markSyncChangeRejected,
    createOperationReceipt,
    getOperationReceipt
};