// var Users = require('../models/usersSchema.js');
var model = require('../models/model_main.js');

/*
 * Cleans up the userlists when the users leaves or are disconnected.
 */
function cleanup(socket, io) {
    user = model.getUserSocket(socket.id);
    if (!user) {
        return;
    }
    if (model.userInLobby(user.nickname)) {
        model.removeUserInLobby(user.nickname);
        socket.to('lobby').emit('userLeft', user.nickname);
    }

    inChat = io.sockets.adapter.rooms.chat;
    if (inChat) {
        if (inChat.sockets[socket.id]) {
            var message = 'Left Chat';
            model.addMessage(function(res) {}, user, message);
            io.to('chat').emit('newMessage', {
                user: user.nickname,
                message: message
            });
        }
    }
    model.removeUserSocket(socket.id);
}

module.exports.handle = function(socket, io) {

    /*
     * Client logs in, the user data is returned.
     */
    socket.on('login', function(user) {
        model.getUser(function(result) {
            model.addUserSocket(function() {
                socket.emit('loginResponse', {
                    login: result.result,
                    user: result.user
                });
            }, socket.id, result.user);
        }, user.username, user.password);
    });

    /*
     * Returns the current user from the socket id for the edit user page     
     */
    socket.on("user", function() {
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        socket.emit('userData', {
            user
        });
    });

    /*
     * Creates a new user.
     */
    socket.on("createUser", function(user) {
        model.createUser(function(result) {
            if (result.result) {
                model.addUserSocket(function() {
                        socket.emit('userCreated', {
                            result
                        });
                    },
                    socket.id, result.newUser);
            } else {
                socket.emit('userCreated', result);
            }
        }, user);
    });

    /*
     * Run when a user updates its information, updates the database and 
     * returns a sucess/fail message.
     */
    socket.on('updateUser', function(newUser) {
        oldUser = model.getUserSocket(socket.id);
        // Checks if the user exists on the socket
        if (!oldUser) {
            return;
        }
        model.updateUser(function(result) {
            if (result.result) {
                model.addUserSocket(function() {
                        socket.emit('updateUserResponse', {
                            result: result.result
                        });
                    },
                    socket.id, result.newUser);
            } else {
                socket.emit('updateUserResponse', {
                    result: result.result
                });

            }
        }, oldUser, newUser);
    });

    /*
     * Client requests highscore data.
     */
    socket.on("highscore", function(req) {
        model.getScores(function(result) {
            socket.emit('highScoreData', {
                Score: result
            });
        });
    });

    /* 
     * Client enters lobby.
     * The current user is added to the lobby list, 
     * This is broadcasted to other sockets subscribing to lobby.
     * The list of current lobby members is returned.
     */
    socket.on("joinLobby", function(req) {
        // Gets user, will be null if not logged in.
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        console.log('Joining lobby');
        model.getUserHighScore(user, function(result) {
            var score = 0;
            var date = '';
            if (result) {
                score = result.score;
                date = result.date;
            }
            data = {
                nickname: user.nickname,
                score: score,
                date: date
            };

            // Puts user in the lobby list.
            // model.putUserInLobby(user.nickname);
            model.putUserInLobby(data);

            // Emits the lobby list to the new user.
            socket.emit('lobbyData', model.getAllInLobby());

            // Emits ONLY the new user to the subrscribing sockets.
            // socket.to('lobby').emit('newUserInLobby', user.nickname);
            socket.to('lobby').emit('newUserInLobby', data);

            // The new user joins the lobby.
            socket.join('lobby');
        });

    });

    /*
     * Run when a user is leaving the lobby.
     */
    socket.on('leaveLobby', function(req) {
        console.log('Someone left the lobby');
        socket.leave('lobby');
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        model.removeUserInLobby(user.nickname);
        // io.to('lobby').emit('userLeft', user.nickname);
        socket.to('lobby').emit('userLeft', user.nickname);
    });

    /*
     * Client requests chat room data
     */
    socket.on("joinChat", function(req) {

        // Gets user, will be null if not logged in.
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        var message = 'Joined Chat';
        model.addMessage(function(res) {}, user, message);

        // Emits the lobby list to the new user.
        socket.emit('chatData', model.getMessageList());

        // Emits ONLY the new user to the subrscribing sockets.
        // This means that the client needs to have a list of all the users in 
        // the lobby.
        io.to('chat').emit('newMessage', {
            user: user.nickname,
            message: message
        });

        // The new user joins the lobby.
        socket.join('chat');
    });

    /*
     * Should run when a user sends a message in chat.
     * Forwards the message to all subscribing sockets.
     */
    socket.on('sendMessage', function(message) {
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        model.addMessage(function(res) {}, user, message);
        io.to('chat').emit('newMessage', {
            user: user.nickname,
            message: message
        });
    });

    /*
     * Run when a user is leaving the chat.
     */
    socket.on('leaveChat', function(req) {
        socket.leave('chat');
        user = model.getUserSocket(socket.id);
        if (!user) {
            socket.emit('notLoggedIn');
            return;
        }
        var message = 'Left Chat';
        model.addMessage(function(res) {}, user, message);
        io.to('chat').emit('newMessage', {
            user: user.nickname,
            message: message
        });
    });

    /*
     * Run when a user wants to play
     */
    socket.on('startMatchMaking', function(req) {
        var player = model.getUserSocket(socket.id);
        // TODO: Implement in model
        var otherPlayerData = model.getWaitingPlayer();
        if (otherPlayerData) {
            var gameStatus = 0;
            // var roomName = otherPlayerData.player.nickname + player.nickname;
            model.createGame(function(res) {
                var roomName = res._id;
                otherPlayerSocket = io.sockets.connected[otherPlayerData.socketId];
                socket.join(roomName);
                otherPlayerSocket.join(roomName);
                io.to(roomName).emit('readyToPlay', roomName);
            }, gameStatus);
        } else {
            var Status = 'waitingForPlayer';
            model.putWaitingPlayer(player, socket.id);
        }
    });

    socket.on('sendGameData', function(gameData) {
        socket.to(gameData.roomName).emit('gameData', gameData);
    });

    socket.on('gameOver', function(gameData) {
        console.log('Game over');
        // console.log(gameData);
        user = model.getUserSocket(socket.id);
        if (!user) {
            console.log('Not Logged in');
            return;
        }
        console.log('Game Data To Update');
        model.updateGameStatus(function(gameRes) {
            console.log('Game Results');
            model.saveScore(function(scoreRes) {
                console.log('Score Results');
            }, gameRes, user, gameData.score);
        }, gameData.roomName);
    });


    /*
     * Logs out the user
     */
    socket.on('logout', function() {
        inChat = io.sockets.adapter.rooms.chat;
        cleanup(socket, io);
    });


    /*
     * Client requests chat room data
     */
    socket.on("disconnect", function(req, id) {
        // inChat = io.sockets.adapter.rooms.chat;
        cleanup(socket, io);
    });
};
