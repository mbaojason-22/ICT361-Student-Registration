const express = require("express");

const {
    sendMessage,
    getGroupMessages
} = require("../controllers/groupMessageController");

const router = express.Router();


// Send a message to a group
router.post("/:group_id/messages", sendMessage);


// Get messages in a group
router.get("/:group_id/messages", getGroupMessages);


module.exports = router;