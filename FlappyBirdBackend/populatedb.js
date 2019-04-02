#! /usr/bin/env node

var async = require('async');
var User = require('./models/usersSchema');
var Game = require('./models/gamesSchema.js');
var Message = require('./models/messagesScema.js');
var Score = require('./models/scoresSchema.js');
var ChatHistory = require('./models/chatHistorySchema.js');
var mongoose = require('mongoose');
var url = "mongodb://freddejn:NAbnapkuwrarbA5@ds249128.mlab.com:49128/flappy-database";
mongoose.connect(url);
mongoose.Promise = global.Promise;
var db = mongoose.connection;
mongoose.connection.on('error', console.error.bind(console, 'MongoDB connection error:'));

var users = [];
var games = [];
var messages = [];
var scores = [];
var chatHistorys = [];

// User.remove({}, function(err) {
//     if (err) {
//         console.loge(err);
//     }
//     console.log('Removed Users');
// });
//

function removeUser(callback) {
    User.remove({}, function(err) {
        if (err) {
            console.loge(err);
        }
        console.log('Removed User');
    });
    callback(null, 'rem user');
}

function removeGame(callback) {
    Game.remove({}, function(err) {
        if (err) {
            console.loge(err);
        }
        console.log('Removed Game');
    });
    callback(null, 'rem game');
}

function removeMessages(callback) {
    Message.remove({}, function(err) {
        if (err) {
            console.loge(err);
        }
        console.log('Removed Messages');
    });
    callback(null, 'rem message');
}

function removeScores(callback) {
    Score.remove({}, function(err) {
        if (err) {
            console.loge(err);
        }
        console.log('Removed Scores');
    });
    callback(null, 'rem Scores');
}

function removeChatHistorys(callback) {
    ChatHistory.remove({}, function(err) {
        if (err) {
            console.loge(err);
        }
        console.log('Removed ChatHistory');
    });
    callback(null, 'rem ChatHistory');
}


function userCreate(email, password, nickname, firstName, lastName, age, street, streetNumber, city, country, cb) {
    userDetail = {
        email: email,
        password: password,
        nickname: nickname,
        firstName: firstName,
        lastName: lastName,
        age: age,
        street: street,
        streetNumber: streetNumber,
        city: city,
        country: country,
    };
    var user = new User(userDetail);
    user.save(function(err) {
        if (err) {
            cb(err, null);
            return;
        }
        users.push(user);
        console.log('Created user');
        cb(null, user);
    });
}

function createUsers(cb) {
    async.parallel([
            function(callback) {
                userCreate('freddejn@mail.com', '1234', 'Patte', 'Patrik', 'Pettson', 12, 'Spelmanshojden', 9, 'Sundbyberg', 'Sweden', callback);
            },
            function(callback) {
                userCreate('fred@mail.com', 'aaaabbbb', 'Fredde', 'Fred', 'Flinta', 10, 'Gata', 20, 'Stad', 'Norway', callback);
            },
            function(callback) {
                userCreate('kaj@mail.com', '3333', 'Kajsa', 'Orren', 'Orup', 28, 'Easy Street', 66, 'Ort', 'Finland', callback);
            },
            function(callback) {
                userCreate('tessan@mail.com', '4444', 'Therese', 'Tessan', 'Pettson', 31, 'Idungatan', 17, 'Märsta', 'Sverige', callback);
            },
        ],
        cb);
}

function gameCreate(status, cb) {
    gameDetail = {
        status: status
    };
    var game = new Game(gameDetail);
    game.save(function(err) {
        if (err) {
            cb(err, null);
            return;
        }
        // console.log('New User: ' + user);
        games.push(game);
        console.log('Created game');
        cb(null, game);
    });
}

function createGames(cb) {
    async.parallel([
            function(callback) {
                gameCreate(0, callback);
            },
            function(callback) {
                gameCreate(0, callback);
            },
        ],
        cb);
}

function messageCreate(message, cb) {
    messageDetail = {
        message: message
    };
    var message = new Message(messageDetail);
    message.save(function(err) {
        if (err) {
            cb(err, null);
            return;
        }
        // console.log('New User: ' + user);
        messages.push(message);
        console.log('Created message');
        cb(null, message);
    });
}

function createMessage(cb) {
    async.parallel([
            function(callback) {
                messageCreate('First message', callback);
            },
            function(callback) {
                messageCreate('Second message', callback);
            },
        ],
        cb);
}

function scoreCreate(game, user, score, cb) {
    scoreDetail = {
        gameId: game,
        userId: user,
        score: score
    };
    var score = new Score(scoreDetail);
    score.save(function(err) {
        if (err) {
            cb(err, null);
            return;
        }
        // console.log('New User: ' + user);
        scores.push(score);
        console.log('Created score');
        cb(null, score);
    });
}

function createScore(cb) {
    async.parallel([
            function(callback) {
                scoreCreate(games[0], users[0], 10, callback);
            },
            function(callback) {
                scoreCreate(games[0], users[1], 40, callback);
            },
        ],
        cb);
}

function chatHistoryCreate(user, message, cb) {
    chatDetail = {
        userId: user,
        messageId: message,
    };
    var chatHistory = new ChatHistory(chatDetail);
    chatHistory.save(function(err) {
        if (err) {
            cb(err, null);
            return;
        }
        // console.log('New User: ' + user);
        chatHistorys.push(chatHistory);
        console.log('Created chatHistory');
        cb(null, chatHistory);
    });
}

function createChatHistory(cb) {
    async.parallel([
            function(callback) {
                chatHistoryCreate(users[0], messages[0], callback);
            },
            function(callback) {
                chatHistoryCreate(users[1], messages[0], callback);
            },
        ],
        cb);
}

async.series([
        removeGame,
        removeMessages,
        removeUser,
        removeScores,
        removeChatHistorys,
        createUsers,
        createMessage,
        createGames,
        createScore,
        createChatHistory,
    ],
    // Optional callback
    function(err, results) {
        if (err) {
            console.log('FINAL ERR: ' + err);
        } else {
            console.log('Completed');

        }
        // All done, disconnect from database
        mongoose.connection.close();
    });
