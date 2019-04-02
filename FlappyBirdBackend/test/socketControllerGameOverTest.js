// var should = require('should');
var assert = require('chai').assert;
var expect = require('chai').expect;
var io = require('socket.io-client');
var model = require('../models/model_main.js');

var socketURL = 'http://127.0.0.1:8080';

var options = {
    transports: ['websocket'],
    'force new connection': true
};

var user1 = {
    'username': 'freddejn@mail.com',
    'password': '1234',
    'nickname': 'Patte'
};

var oldUser = {
    _id: '5aa135e5d6f6f66a010a0dd5',
    email: 'freddejn@mail.com',
    nickname: 'Patte'
};

var gameId = "5aa9783bf5ca14b9a3a663df";

describe("GameOver", function() {
    it('Should update the game score', function(done) {
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('login', user1);
        });

        // Testing onUpdate
        client1.on('loginResponse', function(data) {
            console.log('logged in');
            console.log(data);
            client1.emit('gameOver', {
                roomName: gameId,
                score: 10
            });
        });

    });
});
