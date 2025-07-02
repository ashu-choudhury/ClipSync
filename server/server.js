const express = require('express');
const {Server} = require('socket.io');
const morgan = require("morgan");
const http = require('http');
const roots = require("./roots/roots");
const {clientHandler} = require("./socket_server");
const app = express();
app.use(morgan("dev"));
app.use("/", roots);
const server = http.createServer(app);
const io =new  Server(server);
io.on("connection", (socket) => {
    console.log(`Client connected: ${socket.id}`);
    clientHandler(socket);
});

server.listen(3000, () => {
    console.log('Server started on port 3000');
});
