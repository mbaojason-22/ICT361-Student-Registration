const db = require("../config/database");


// Create a group
const createGroup = async (req, res) => {
    try {
        const {
            group_code,
            group_name,
            max_members,
            assigned_by_account_id
        } = req.body;

        if (!group_code || !group_name) {
            return res.status(400).json({
                success: false,
                message: "Group code and group name are required"
            });
        }

        // Check if group code already exists
        const [existingGroup] = await db.query(
            `SELECT group_id
             FROM lab_groups
             WHERE group_code = ?`,
            [group_code]
        );

        if (existingGroup.length > 0) {
            return res.status(409).json({
                success: false,
                message: "A group with this code already exists"
            });
        }

        // If an account was supplied, make sure it exists
        if (assigned_by_account_id) {
            const [account] = await db.query(
                `SELECT account_id
                 FROM accounts
                 WHERE account_id = ?
                   AND is_active = 1`,
                [assigned_by_account_id]
            );

            if (account.length === 0) {
                return res.status(404).json({
                    success: false,
                    message: "Assigning account not found"
                });
            }
        }

        const [result] = await db.query(
            `INSERT INTO lab_groups
             (group_code, group_name, max_members)
             VALUES (?, ?, ?)`,
            [
                group_code,
                group_name,
                max_members || 15
            ]
        );

        res.status(201).json({
            success: true,
            message: "Group created successfully",
            group_id: result.insertId
        });

    } catch (error) {
        console.error("Create group error:", error);

        res.status(500).json({
            success: false,
            message: "Could not create group"
        });
    }
};


// Get all active groups
const getGroups = async (req, res) => {
    try {
        const [groups] = await db.query(
            `SELECT
                group_id,
                group_code,
                group_name,
                max_members,
                current_members,
                active,
                created_at,
                updated_at
             FROM lab_groups
             WHERE active = 1
             ORDER BY group_code`
        );

        res.status(200).json({
            success: true,
            groups
        });

    } catch (error) {
        console.error("Get groups error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve groups"
        });
    }
};


// Get one group
const getGroup = async (req, res) => {
    try {
        const { group_id } = req.params;

        const [groups] = await db.query(
            `SELECT
                group_id,
                group_code,
                group_name,
                max_members,
                current_members,
                active,
                created_at,
                updated_at
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

        res.status(200).json({
            success: true,
            group: groups[0]
        });

    } catch (error) {
        console.error("Get group error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve group"
        });
    }
};


// Get group members
const getGroupMembers = async (req, res) => {
    try {
        const { group_id } = req.params;

        const [members] = await db.query(
            `SELECT
                gm.membership_id,
                gm.student_id,
                s.student_number,
                s.first_name,
                s.last_name,
                gm.status,
                gm.joined_at,
                gm.ended_at
             FROM group_memberships gm
             JOIN students s
                ON gm.student_id = s.student_id
             WHERE gm.group_id = ?
               AND gm.status = 'ACTIVE'
             ORDER BY s.last_name, s.first_name`,
            [group_id]
        );

        res.status(200).json({
            success: true,
            members
        });

    } catch (error) {
        console.error("Get group members error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve group members"
        });
    }
};


// Add student to group
const addStudentToGroup = async (req, res) => {
    try {
        const { group_id } = req.params;
        const {
            student_id,
            assigned_by_account_id
        } = req.body;

        if (!student_id) {
            return res.status(400).json({
                success: false,
                message: "Student ID is required"
            });
        }

        // Check group
        const [groups] = await db.query(
            `SELECT
                group_id,
                max_members,
                current_members,
                active
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

        const group = groups[0];

        if (group.active !== 1) {
            return res.status(400).json({
                success: false,
                message: "This group is not active"
            });
        }

        if (group.current_members >= group.max_members) {
            return res.status(400).json({
                success: false,
                message: "This group has reached its maximum number of members"
            });
        }

        // Check student exists
        const [students] = await db.query(
            `SELECT student_id
             FROM students
             WHERE student_id = ?`,
            [student_id]
        );

        if (students.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Student not found"
            });
        }

        // Check active membership
        const [existingMembership] = await db.query(
            `SELECT membership_id
             FROM group_memberships
             WHERE student_id = ?
               AND group_id = ?
               AND status = 'ACTIVE'`,
            [student_id, group_id]
        );

        if (existingMembership.length > 0) {
            return res.status(409).json({
                success: false,
                message: "Student is already an active member of this group"
            });
        }

        // Add membership
        await db.query(
            `INSERT INTO group_memberships
             (student_id, group_id, status, assigned_by_account_id)
             VALUES (?, ?, 'ACTIVE', ?)`,
            [
                student_id,
                group_id,
                assigned_by_account_id || null
            ]
        );

        // Update member count
        await db.query(
            `UPDATE lab_groups
             SET current_members = current_members + 1
             WHERE group_id = ?`,
            [group_id]
        );

        res.status(201).json({
            success: true,
            message: "Student added to group successfully"
        });

    } catch (error) {
        console.error("Add student to group error:", error);

        res.status(500).json({
            success: false,
            message: "Could not add student to group"
        });
    }
};


// End student's membership
const removeStudentFromGroup = async (req, res) => {
    try {
        const { group_id, student_id } = req.params;

        const [membership] = await db.query(
            `SELECT membership_id
             FROM group_memberships
             WHERE group_id = ?
               AND student_id = ?
               AND status = 'ACTIVE'`,
            [group_id, student_id]
        );

        if (membership.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Active group membership not found"
            });
        }

        await db.query(
            `UPDATE group_memberships
             SET status = 'ENDED',
                 ended_at = NOW()
             WHERE membership_id = ?`,
            [membership[0].membership_id]
        );

        await db.query(
            `UPDATE lab_groups
             SET current_members = GREATEST(current_members - 1, 0)
             WHERE group_id = ?`,
            [group_id]
        );

        res.status(200).json({
            success: true,
            message: "Student removed from group successfully"
        });

    } catch (error) {
        console.error("Remove student from group error:", error);

        res.status(500).json({
            success: false,
            message: "Could not remove student from group"
        });
    }
};


// Get groups for a student
const getStudentGroups = async (req, res) => {
    try {
        const { student_id } = req.params;

        const [groups] = await db.query(
            `SELECT
                gm.membership_id,
                gm.group_id,
                lg.group_code,
                lg.group_name,
                gm.status,
                gm.joined_at
             FROM group_memberships gm
             JOIN lab_groups lg
                ON gm.group_id = lg.group_id
             WHERE gm.student_id = ?
               AND gm.status = 'ACTIVE'
               AND lg.active = 1
             ORDER BY lg.group_code`,
            [student_id]
        );

        res.status(200).json({
            success: true,
            groups
        });

    } catch (error) {
        console.error("Get student groups error:", error);

        res.status(500).json({
            success: false,
            message: "Could not retrieve student groups"
        });
    }
};


module.exports = {
    createGroup,
    getGroups,
    getGroup,
    getGroupMembers,
    addStudentToGroup,
    removeStudentFromGroup,
    getStudentGroups
};