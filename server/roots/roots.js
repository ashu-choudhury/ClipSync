const express = require('express');
const os = require("node:os");

const router = express.Router();
router.get("/", (req, res) => {
    return res.json({msg: "hello!", user: os.uptime()});
})
module.exports = router;