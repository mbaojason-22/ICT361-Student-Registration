const nodemailer = require("nodemailer");

const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_APP_PASSWORD
    }
});

const sendVerificationEmail = async (email, code) => {
    await transporter.sendMail({
        from: process.env.EMAIL_USER,
        to: email,
        subject: "Campus Companion Email Verification",
        text: `Your Campus Companion verification code is ${code}. This code expires in 10 minutes.`
    });
};

module.exports = {
    sendVerificationEmail
};