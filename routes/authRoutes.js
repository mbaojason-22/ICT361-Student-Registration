const express = require("express");

const {
    registerStudent,
    verifyStudentEmail,
    resendVerificationCode,
    loginStudent
} = require("../controllers/authController");

const router = express.Router();

router.post("/register", registerStudent);

router.post("/verify-email", verifyStudentEmail);

router.post("/resend-verification-code", resendVerificationCode);

router.post("/login", loginStudent);

module.exports = router;