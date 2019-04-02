// var should = require('should');
var assert = require('chai').assert;
var expect = require('chai').expect;
var io = require('socket.io-client');

var socketURL = 'http://130.229.182.159:8080';

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

// describe("Test", function() {
// it('connecting', function(done) {
var client1 = io.connect(socketURL, options);
client1.on('connect', function(data) {
    console.log('connected');
    client1.emit('join', 0);
});

client1.on('newGame', function(data) {
    console.log(data);
});
// });
// });
