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

describe("Chat", function() {
    it('Should send display correctly when logging in and when sending message', function(done) {
        var num = 0;
        var recieved = 0;
        var numChecked = function(client) {
            num++;
            console.log(num);
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
                console.log('User 2 logged in');
                client2.emit('joinChat');
            });
            client2.on('chatData', function(data) {
                console.log('Chat data recieved when entering user 2');
                console.log(data);
                numChecked();
                client2.emit('sendMessage', 'hej user 1 from 2');
            });
            client2.on('newMessage', function(data) {
                console.log('User 2 recieved a new message');
                console.log(data);
                numChecked();
                console.log(data.message);
            });
        });
        client1.on('loginResponse', function(data) {
            console.log('User 1 logged in');
            client1.emit('joinChat');
        });
        client1.on('chatData', function(data) {
            numChecked();
            console.log('Chat data recieved in client 1');
            console.log(data);
        });

        client1.on('newMessage', function(data) {
            console.log('User 1 recieved a new message');
            console.log(data);
            recieved++;
            numChecked();
            client1.emit('leaveChat');
            client1.disconnect();
        });
    });
});
