// var should = require('should');
var assert = require('chai').assert;
var expect = require('chai').expect;
var io = require('socket.io-client');

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

var user2 = {
    'username': 'fred@mail.com',
    'password': 'aaaabbbb',
    'nickname': 'Fredde',
};

describe("Play", function() {
    it('Should play a game and send data', function(done) {
        // var num = 0;
        // var recieved = 0;
        // var numChecked = function(client) {
        //     num++;
        //     console.log(num);
        //     if (num === 5) {
        //         done();
        //     }
        // };
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('login', user1);
            var client2 = io.connect(socketURL, options);
            client2.on('connect', function(data) {
                client2.emit('login', user2);
            });
            client2.on('loginResponse', function(data) {
                console.log('User 2 logged in');
                client2.emit('startMatchMaking');
            });
            client2.on('readyToPlay', function(roomName) {
                console.log('Match found player 2');
            });

            client2.on('gameData', function(data) {
                console.log('Recieving data in player 2');
                console.log(data);
            });
        });
        client1.on('loginResponse', function(data) {
            console.log('User 1 logged in');
            client1.emit('startMatchMaking');
        });
        client1.on('readyToPlay', function(roomName) {
            console.log('Match found player 1');
            console.log('Player 1 sending game data');
            client1.emit('sendGameData', {
                roomName: roomName,
                x: 10,
                y: 10
            });
        });
        client1.on('gameData', function(data) {
            console.log('Recieving data in player 1');
            console.log(data);
        });
    });
});
