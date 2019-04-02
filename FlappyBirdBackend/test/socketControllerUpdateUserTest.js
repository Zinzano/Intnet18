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

var newUser = {
    _id: '5aa135e5d6f6f66a010a0dd5',
    email: 'freddejn@mail.com',
    nickname: 'Patte',
    firstName: 'NewUser',
    lastName: 'Rothfuss',
    age: 10,
    street: 'Spelmanshojden',
    streetNumber: 9,
    city: 'Sundbyberg',
    password:'newPassword'
};

describe("Update User", function() {
    it('Should update the user data via the socket', function(done) {
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('login', user1);
        });


        // Testing onUpdate
        client1.on('loginResponse', function(data) {
            console.log('loged in');
            client1.emit('user');
        });

        // Testing onUpdate
        client1.on('userData', function(data) {
            data.user.password = 'newPassword';
            client1.emit('updateUser', data.user);
        });

        client1.on('updateUserResponse', function(data) {
            assert(data, true);
            console.log(data);
            done();
            client1.disconnect();
        });
    });
});
