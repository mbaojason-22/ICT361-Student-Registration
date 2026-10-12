require("dotenv").config();

const express = require("express");
const cors = require("cors");

const db = require("./config/database");

const authRoutes = require("./routes/authRoutes");
const studentRoutes = require("./routes/studentRoutes");
const lecturerRoutes = require("./routes/lecturerRoutes");
const groupRoutes = require("./routes/groupRoutes");
const groupChangeRoutes = require("./routes/groupChangeRoutes");
const groupMessageRoutes = require("./routes/groupMessageRoutes");
const noticeRoutes = require("./routes/noticeRoutes");
const assignmentRoutes = require("./routes/assignmentRoutes");
const submissionRoutes = require("./routes/submissionRoutes");
const resourceRoutes = require("./routes/resourceRoutes");
const timetableRoutes = require("./routes/timetableRoutes");
const notificationRoutes = require("./routes/notificationRoutes");
const campusMapRoutes = require("./routes/campusMapRoutes");
const academicLinkRoutes = require("./routes/academicLinkRoutes");
const courseRegistrationRequestRoutes =require("./routes/courseRegistrationRequestRoutes");
const studentNumberCorrectionRoutes =require("./routes/studentNumberCorrectionRoutes");
const syncRoutes = require("./routes/syncRoutes");
const deletionMarkerRoutes =require("./routes/deletionMarkerRoutes");

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// API Routes
app.use("/api/auth", authRoutes);
app.use("/api/students", studentRoutes);
app.use("/api/lecturers", lecturerRoutes);
app.use("/api/groups", groupRoutes);
app.use("/api/group-change-requests", groupChangeRoutes);
app.use("/api/groups", groupMessageRoutes);
app.use("/api/notices", noticeRoutes);
app.use("/api/assignments", assignmentRoutes);
app.use("/api/submissions", submissionRoutes);
app.use("/api/resources", resourceRoutes);
app.use("/api/timetable", timetableRoutes);
app.use("/api/notifications", notificationRoutes);
app.use("/api/campus-map", campusMapRoutes);
app.use("/api/academic-links", academicLinkRoutes);
app.use("/api/course-registration-requests",courseRegistrationRequestRoutes);
app.use("/api/student-number-corrections",studentNumberCorrectionRoutes);
app.use("/api/sync", syncRoutes);
app.use("/api/deletion-markers", deletionMarkerRoutes);

// Home route
app.get("/", (req, res) => {
res.json({
message: "Campus Companion API is running!"
});
});

// Database test route
app.get("/api/test-db", async (req, res) => {
try {
const [rows] = await db.query(
"SELECT 1 AS result"
);

    res.json({
        success: true,
        message: "MySQL database connected successfully!",
        result: rows[0].result
    });
} catch (error) {
    console.error("Database connection error:", error);

    res.status(500).json({
        success: false,
        message: "Could not connect to MySQL"
    });
}

});

// Start server
const PORT = process.env.PORT || 3000;

app.listen(3000, "0.0.0.0", () => {
    console.log(`Campus Companion API running on port ${PORT}`);
});