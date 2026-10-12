const jwt = require("jsonwebtoken");

// Verify the access token
const authenticateToken = (req, res, next) => {
const authHeader = req.headers.authorization;

const token =
    authHeader && authHeader.split(" ")[1];

if (!token) {
    return res.status(401).json({
        success: false,
        message: "Access token required"
    });
}

if (!authHeader.startsWith("Bearer ")) {
    return res.status(401).json({
        success: false,
        message: "Authorization header must use Bearer token format"
    });
}

try {
    const decoded = jwt.verify(
        token,
        process.env.JWT_SECRET
    );

    req.user = decoded;
    next();
} catch (error) {
    return res.status(403).json({
        success: false,
        message: "Invalid or expired token"
    });
}

};

// Restrict access by role
const requireRole = (...allowedRoles) => {
return (req, res, next) => {
if (
!req.user ||
!allowedRoles.includes(req.user.role)
) {
return res.status(403).json({
success: false,
message: "You do not have permission to access this resource"
});
}

    next();
};

};

// Students may only access their own student ID
const requireStudentAccess = (req, res, next) => {
const requestedStudentId = Number(req.params.student_id);
const loggedInStudentId = Number(req.user?.student_id);

if (
    !Number.isInteger(requestedStudentId) ||
    requestedStudentId <= 0 ||
    !Number.isInteger(loggedInStudentId) ||
    requestedStudentId !== loggedInStudentId
) {
    return res.status(403).json({
        success: false,
        message: "You can only access your own student information"
    });
}

next();

};

// Lecturers may only use their own lecturer ID
const requireLecturerAccess = (req, res, next) => {
const requestedLecturerId = Number(req.params.lecturer_id);
const loggedInLecturerId = Number(req.user?.lecturer_id);

if (
    !Number.isInteger(requestedLecturerId) ||
    requestedLecturerId <= 0 ||
    !Number.isInteger(loggedInLecturerId) ||
    requestedLecturerId !== loggedInLecturerId
) {
    return res.status(403).json({
        success: false,
        message: "You can only access your own lecturer information"
    });
}

next();

};

module.exports = {
authenticateToken,
requireRole,
requireStudentAccess,
requireLecturerAccess
};