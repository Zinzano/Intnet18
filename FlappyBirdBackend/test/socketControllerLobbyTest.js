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

describe("Lobby", function() {
    it('Shold notify when a user joins lobby', function(done) {
        var num = 0;
        var numChecked = function(client) {
            num++;
            if (num === 5) {
                done();
            }
        };
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('login', user1);
            var client2 = io.connect(socketURL, options);
            client2.on('connect', function(data) {
                client2.emit('login', user2);

            });
            client2.on('loginResponse', function(data) {
                console.log('login response 2');
                console.log(data);
                client2.emit('joinLobby');
            });
            client2.on('lobbyData', function(data) {
                console.log('Lobby data 2');
                console.log(data);
                numChecked();
                client2.emit('leaveLobby');
            });
        });
        client1.on('loginResponse', function(data) {
            console.log('login response 1');
            console.log(data);
            client1.emit('joinLobby');
            numChecked();
        });

        client1.on('lobbyData', function(data) {
            console.log('lobbyData 1');
            console.log(data);
            numChecked();
        });

        client1.on('newUserInLobby', function(data) {
            console.log('someone joined 1');
            console.log(data);
            numChecked();
        });
        client1.on('userLeft', function(data) {
            console.log('someone left 1');
            console.log(data);
            numChecked();
        });
    });
});
