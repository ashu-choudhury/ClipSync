/*
 * @param {import("socket.io").Socket} socket - The connected WebSocket client.
 */

function clientHandler(Socket) {
    Socket.on("register", (email) => {
        Socket.join(email)
        console.log(`Registered ${email}`);
    });
    Socket.on("Clipboard", (text) => {
        const rooms = Array.from(socket.rooms).filter((r) => r !== socket.id);
        for (const room of rooms) {
            Socket.to(room).emit("Clipboard", text);
        }
    });
    Socket.on("disconnect", (room) => {
        console.log(`Client disconnected: ${room}`);
    });
    }
module.exports = {clientHandler}