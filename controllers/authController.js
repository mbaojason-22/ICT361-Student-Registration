const bcrypt = require("bcryptjs");
const crypto = require("crypto");
const jwt = require("jsonwebtoken");
const db = require("../config/database");
const { sendVerificationEmail } = require("../services/emailService");


// Register student
const registerStudent = async (req, res) => {
    try {
        const {
            student_number,
            first_name,
            last_name,
            email,
            password,
            programme_id
        } = req.body;

        if (
            !student_number ||
            !first_name ||
            !last_name ||
            !email ||
            !password ||
            !programme_id
        ) {
            return res.status(400).json({
                success: false,
                message: "All fields are required"
            });
        }

        // Check whether email already exists
        const [existingAccount] = await db.query(
            "SELECT account_id FROM accounts WHERE email = ?",
            [email]
        );

        if (existingAccount.length > 0) {
            return res.status(409).json({
                success: false,
                message: "Email already registered"
            });
        }

        // Check whether student number already exists
        const [existingStudent] = await db.query(
            "SELECT student_id FROM students WHERE student_number = ?",
            [student_number]
        );

        if (existingStudent.length > 0) {
            return res.status(409).json({
                success: false,
                message: "Student number already registered"
            });
        }

        // Create student
        const [studentResult] = await db.query(
            `INSERT INTO students
            (
                student_number,
                first_name,
                last_name,
                programme_id
            )
            VALUES (?, ?, ?, ?)`,
            [
                student_number,
                first_name,
                last_name,
                programme_id
            ]
        );

        const studentId = studentResult.insertId;

        // Hash password
        const passwordHash = await bcrypt.hash(password, 10);

        // Generate 6-digit verification code
        const verificationCode = crypto
            .randomInt(100000, 1000000)
            .toString();

        // Code expires in 10 minutes
        const verificationExpiresAt = new Date(
            Date.now() + 10 * 60 * 1000
        );

        // Create account
        const [accountResult] = await db.query(
            `INSERT INTO accounts
            (
                email,
                password_hash,
                role,
                student_id,
                verification_code,
                verification_expires_at
            )
            VALUES (?, ?, 'STUDENT', ?, ?, ?)`,
            [
                email,
                passwordHash,
                studentId,
                verificationCode,
                verificationExpiresAt
            ]
        );

        // Send verification email
        try {
            await sendVerificationEmail(
                email,
                verificationCode
            );
        } catch (emailError) {
            console.error(
                "Verification email error:",
                emailError
            );

            // Remove account and student if email fails
            await db.query(
                "DELETE FROM accounts WHERE account_id = ?",
                [accountResult.insertId]
            );

            await db.query(
                "DELETE FROM students WHERE student_id = ?",
                [studentId]
            );

            return res.status(500).json({
                success: false,
                message:
                    "Account could not be created because the verification email could not be sent"
            });
        }

        res.status(201).json({
            success: true,
            message:
                "Student registered successfully. A verification code has been sent to your email.",
            student_id: studentId
        });

    } catch (error) {
        console.error("Registration error:", error);

        res.status(500).json({
            success: false,
            message: "Registration failed"
        });
    }
};


// Verify student email
const verifyStudentEmail = async (req, res) => {
    try {
        const {
            email,
            verification_code
        } = req.body;

        if (!email || !verification_code) {
            return res.status(400).json({
                success: false,
                message:
                    "Email and verification code are required"
            });
        }

        const [accounts] = await db.query(
            `SELECT
                account_id,
                verification_code,
                verification_expires_at,
                verified_at
             FROM accounts
             WHERE email = ?
               AND role = 'STUDENT'`,
            [email]
        );

        if (accounts.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Account not found"
            });
        }

        const account = accounts[0];

        if (account.verified_at) {
            return res.status(400).json({
                success: false,
                message: "Email is already verified"
            });
        }

        if (
            account.verification_code !==
            verification_code
        ) {
            return res.status(400).json({
                success: false,
                message: "Invalid verification code"
            });
        }

        if (
            !account.verification_expires_at ||
            new Date(account.verification_expires_at) <
                new Date()
        ) {
            return res.status(400).json({
                success: false,
                message:
                    "Verification code has expired"
            });
        }

        // Mark email as verified
        await db.query(
            `UPDATE accounts
             SET
                verified_at = NOW(),
                verification_code = NULL,
                verification_expires_at = NULL
             WHERE account_id = ?`,
            [account.account_id]
        );

        res.status(200).json({
            success: true,
            message: "Email verified successfully"
        });

    } catch (error) {
        console.error(
            "Email verification error:",
            error
        );

        res.status(500).json({
            success: false,
            message: "Email verification failed"
        });
    }
};


// Resend verification code
const resendVerificationCode = async (req, res) => {
    try {
        const { email } = req.body;

        if (!email) {
            return res.status(400).json({
                success: false,
                message: "Email is required"
            });
        }

        const [accounts] = await db.query(
            `SELECT
                account_id,
                verified_at
             FROM accounts
             WHERE email = ?
               AND role = 'STUDENT'`,
            [email]
        );

        if (accounts.length === 0) {
            return res.status(404).json({
                success: false,
                message: "Account not found"
            });
        }

        const account = accounts[0];

        if (account.verified_at) {
            return res.status(400).json({
                success: false,
                message: "Email is already verified"
            });
        }

        // Generate new verification code
        const verificationCode = crypto
            .randomInt(100000, 1000000)
            .toString();

        const verificationExpiresAt = new Date(
            Date.now() + 10 * 60 * 1000
        );

        // Save new code
        await db.query(
            `UPDATE accounts
             SET
                verification_code = ?,
                verification_expires_at = ?
             WHERE account_id = ?`,
            [
                verificationCode,
                verificationExpiresAt,
                account.account_id
            ]
        );

        // Send new code
        await sendVerificationEmail(
            email,
            verificationCode
        );

        res.status(200).json({
            success: true,
            message:
                "A new verification code has been sent to your email"
        });

    } catch (error) {
        console.error(
            "Resend verification code error:",
            error
        );

        res.status(500).json({
            success: false,
            message:
                "Could not resend verification code"
        });
    }
};


// Student login
const loginStudent = async (req, res) => {
    try {
        const {
            email,
            password
        } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                success: false,
                message:
                    "Email and password are required"
            });
        }

        const [accounts] = await db.query(
            `SELECT
                a.account_id,
                a.email,
                a.password_hash,
                a.role,
                a.student_id,
                a.verified_at,
                s.student_number,
                s.first_name,
                s.last_name,
                s.programme_id,
                s.status
             FROM accounts a
             JOIN students s
                ON a.student_id = s.student_id
             WHERE a.email = ?
               AND a.role = 'STUDENT'`,
            [email]
        );

        if (accounts.length === 0) {
            return res.status(401).json({
                success: false,
                message:
                    "Invalid email or password"
            });
        }

        const account = accounts[0];

        // Check password
        const passwordMatch = await bcrypt.compare(
            password,
            account.password_hash
        );

        if (!passwordMatch) {
            return res.status(401).json({
                success: false,
                message:
                    "Invalid email or password"
            });
        }

        // Require email verification
        if (!account.verified_at) {
            return res.status(403).json({
                success: false,
                message:
                    "Please verify your email before logging in"
            });
        }

        // Create JWT token
        const token = jwt.sign(
            {
                account_id: account.account_id,
                student_id: account.student_id,
                role: account.role
            },
            process.env.JWT_SECRET,
            {
                expiresIn: "7d"
            }
        );

        res.status(200).json({
            success: true,
            message: "Login successful",
            token,
            student: {
                student_id: account.student_id,
                student_number: account.student_number,
                first_name: account.first_name,
                last_name: account.last_name,
                email: account.email,
                programme_id: account.programme_id,
                status: account.status
            }
        });

    } catch (error) {
        console.error("Login error:", error);

        res.status(500).json({
            success: false,
            message: "Login failed"
        });
    }
};


module.exports = {
    registerStudent,
    verifyStudentEmail,
    resendVerificationCode,
    loginStudent
};