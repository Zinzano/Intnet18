var User = require('../models/usersSchema.js');
var Score = require('../models/scoresSchema.js');
var Message = require('../models/messagesScema.js');
var ChatHistory = require('../models/chatHistorySchema.js');
var Game = require('../models/gamesSchema.js');
var socketToUser = {};
var usersInLobby = [];
var usersInChat = [];
var chatMessages = [];
var waitingPlayers = [];
var jwt = require('jsonwebtoken');


exports.getDatabaseData = function(callback) {
    ChatHistory.find({}, '-_id -__v').populate('userId', 'nickname -_id').populate('messageId', 'message -_id').sort('date').exec(function(err, result) {
        if (err) {
            // console.log(err);
        }
        // console.log(result.toJSON());
        for (var i = 0, len = result.length; i < len; i++) {
            var message = result[i].messageId.message;
            var user = result[i].userId.nickname;
            chatMessages.push({
                user: user,
                message: message
            });
        }
    });
};


/*
 * Creates a new Score and sets the .
 */
exports.saveScore = function(callback, game, user, score) {
    Score.create({
        gameId: game,
        userId: user,
        score: score
    }, function(err, scoreRes) {
        if (err) {
            // console.log(err);
            return callback({
                scoreRes
            });
        } else if (scoreRes) {
            console.log('Game created sucessfully');
            return callback({
                scoreRes
            });
        }
    });
};



/*
 * Updates the game in the user database based on the information provided
 */
exports.updateGameStatus = function(callback, gameId) {
    Game.findOneAndUpdate({
            '_id': gameId
        }, {
            $inc: {
                status: 1
            }
        }, {
            new: true
        },
        function(err, res) {
            if (err) {
                console.log('Error updating Game');
                // console.log(err);
                return callback(null);
            } else {
                console.log('Returned game Status');
                return callback(res);
            }
        });
};



/*
 * Creates a new Game and sets the gameState.
 */
exports.createGame = function(callback, gameStatus) {
    Game.create({
        status: gameStatus
    }, function(err, res) {
        if (err) {
            // console.log('Error in create game');
            // console.log(err);
            return callback({
                res
            });
        } else if (res) {
            console.log('Game created sucessfully');
            // console.log(res);
            return callback(
                res
            );
        }
    });
};




/*
 * Returns all the emails and first name from the users schema.
 */
exports.view_users = function(req, res) {
    User.find({}, 'email firstName')
        .exec(function(err, list_users) {
            if (err) {
                res.json({
                    user: 'none'
                });
            }
            res.json({
                title: 'Data',
                'data': list_users
            });
        });
};

/*
 * Creates a new user and pushes it to the database.
 */
exports.createUser = function(callback, user) {
    User.create(user, function(err, res) {
        if (err) {
            var mess = '';
            // console.log(err);
            if (err.errors.email) {
                mess = 'Email exists';
            } else if (err.errors.nickname) {
                mess = 'Nickname exists';
            }
            return callback({
                result: false,
                newUser: mess
            });
        } else if (res) {
            console.log('User created sucessfully');
            // result.newUser.save();
            var usr = res.toJSON();
            delete usr.password;

            return callback({
                result: true,
                newUser: usr
            });
        }
    });
};

/*
 * Returns the user without password.
 */
exports.getUser = function(callback, username, password) {
    User.findOne({
        'email': username,
        // 'password': password
    }).exec(function(err, result) {
        if (err) {
            return callback({
                result: true,
                user: usr
            });
        }
        // Test a matching password
        if (result) {
            result.comparePassword(password, function(error, isMatch) {
                if (isMatch) {
                    var usr = result.toJSON();
                    return callback({
                        result: true,
                        user: usr
                    });
                } else {
                    return callback({
                        result: false,
                        user: null
                    });
                }
            });
        } else
            return callback({
                result: false,
                user: null
            });
    });
};


/*
 * Updates the user in the user database based on the information provided
 */
exports.updateUser = function(callback, oldUser, newUser) {
    User.find({}).findOneAndUpdate({
        _id: oldUser._id
    }, newUser, {
        new: true
    }, function(err, res) {
        if (err) {
            var field = err.message.split('.$')[1];
            field = field.split(' dup key')[0];
            field = field.substring(0, field.lastIndexOf('_'));
            var mess = '';
            if (field === 'email') {
                mess = 'Email exists';
            } else if (err.errors.nickname) {
                mess = 'Nickname exists';
            }
            return callback({
                result: false,
                newUser: mess
            });
        }
        if (res) {
            res.save();
            var usr = res.toJSON();
            delete usr.password;
            return callback({
                result: true,
                newUser: usr
            });
        }
    });
};

// Returns a token for each user.
exports.getToken = function(callback, username, password) {
    User.find({
        'email': username,
        'password': password
    }, '-password').exec(function(err, result) {
        if (err || result.length == 0) {
            // console.log(err);
            return callback(null);
        }
        token = jwt.sign({
            exp: Math.floor(Date.now() / 1000) + (60 * 60),
            data: result
        }, 'snhsnthsnthaueosunthinsth');
        return callback(token);
    });
};

/*
 * Returns the user including password and id.
 */
exports.getAdminUser = function(callback, username, password) {
    User.find({
        'email': username,
        'password': password
    }).exec(function(err, result) {
        if (err) {
            // console.log(err);
        }
        return callback(result);
    });
};

/*
 * Returns a list of scores from users.
 */
exports.getScores = function(callback) {
    Score.find({}, '-_id -gameId').populate('userId', 'nickname -_id').sort('-score').limit(100).exec(function(err, result) {
        if (err) {
            // console.log(err);
        }
        return callback(result);
    });
};

/*
 * Returns highscore for one user.
 */
exports.getUserHighScore = function(user, callback) {
    Score.find({
        userId: user
    }).sort('-score').limit(1).exec(function(err, result) {
        if (err) {
            // console.log(err);
            // return callback(null);
        }
        return callback(result[0]);
    });
};

/*
 * Adds a message to the Message schema and a
 * message + user to the chathistorySchema.
 * Also adds a message to the local list.
 */
exports.addMessage = function(callback, user, message) {
    var mess = new Message({
        message: message
    });
    chatMessages.push({
        user: user.nickname,
        message: message
    });
    // Saving message
    mess.save(function(err) {
        if (err) {
            return callback(err);
        } else {
            // If sucessful creating ChatHistory
            ChatHistory.create({
                userId: user,
                messageId: mess
            }, function(err) {
                if (err) {
                    return callback(err);
                } else {
                    return callback(true);
                }
            });
        }
    });
};

/*
 * Gets the waiting player from the waiting player list
 * Handles it as a queu.
 */
exports.getWaitingPlayer = function() {
    return waitingPlayers.shift();
};

/*
 * Adds a user to the match making list.
 */
exports.putWaitingPlayer = function(player, socketId) {
    waitingPlayers.push({
        player: player,
        socketId: socketId
    });

};
/*
 * Pushes the messages to the chatMessages list
 */
exports.putMessageInList = function(user, message) {
    chatMessages.push({
        user: user.nickname,
        message: message
    });
};

exports.getMessageList = function() {
    return chatMessages;
};


exports.userInLobby = function(nickname) {
    for (var i = 0; i < usersInLobby.length; i++) {
        if (usersInLobby[i].nickname === nickname) {
            return true;
        }
    }
    // return nickname in usersInLobby;
};

/*
 * Adds the users nickname to the lobby.
 */
// exports.putUserInLobby = function(nickname) {
// usersInLobby.push(nickname);
// };
exports.putUserInLobby = function(data) {
    usersInLobby.push(data);
};

/*
 * Removes a user from the lobby list.
 */
exports.removeUserInLobby = function(nickname) {
    for (var i = 0; i < usersInLobby.length; i++) {
        if (nickname === usersInLobby[i].nickname) {
            usersInLobby.splice(i, 1);
        }
    }
};

/*
 * This returns a list if the users in lobby.
 */
exports.getAllInLobby = function(callback) {
    return usersInLobby;
};

/*
 * Sets the user to a socketId to a key so that the user can be reached.
 */
exports.addUserSocket = function(callback, socketId, user) {
    console.log('Adding user to socket');
    if (user != null) {
        socketToUser[socketId] = user;
        callback();
    } else {
        callback();
        console.log('Something went wrong');
    }
};

/*
 * Gets the user by the socket id.
 */
exports.getUserSocket = function(socketId) {
    return socketToUser[socketId];
};

/*
 * Gets the user by the socket id.
 */
exports.removeUserSocket = function(socketId) {
    delete socketToUser[socketId];
};
