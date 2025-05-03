const express = require('express');
const {Server} = require('socket.io');
const http = require('http');
const roots = require("./roots/roots");
const app = express();
app.use("/", roots);
const server = http.createServer(app);
const io = Server(server);


server.listen(3000, () => {
    console.log('Server started on port 3000');
});
