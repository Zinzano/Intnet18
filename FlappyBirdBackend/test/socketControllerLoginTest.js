// var should = require('should');
var assert = require('chai').assert;
var io = require('socket.io-client');

var socketURL = 'http://127.0.0.1:8080';

var options = {
    transports: ['websocket'],
    'force new connection': true
};

var user1 = {
    'username': 'freddejn@mail.com',
    'password': 'newPassword'
};

var user2 = {
    'username': 'fred@mail.com',
    'password': 'aaaabbbb'
};

describe("Login", function() {
    it('Should connect to server, login and return the user object', function(done) {
        var num = 0;
        var numChecked = function(client) {
            num++;
            console.log(num);
            if (num === 4) {
                done();
            }
        };
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('login', user1);
            console.log('user 1 logging in');
            var client2 = io.connect(socketURL, options);
            client2.on('connect', function(data) {
                client2.emit('login', user2);
                console.log('user 2 logging in');
            });
            client2.on('loginResponse', function(data) {
                console.log('user 2 got a login response');
                numChecked();
                client2.emit('user');
            });
            client2.on('userData',
                function(data) {
                    console.log('user 2 got user data');
                    assert.equal(data.user.email, user2.username);
                    numChecked();
                    client2.disconnect();
                });
        });
        client1.on('loginResponse', function(data) {
            console.log('user 1 got login response');
            console.log('login resp');
            numChecked();
            client1.emit('user');
        });
        client1.on('userData',
            function(data) {
                console.log('user 1 got user data');
                console.log(data);
                assert.equal(data.user.email, user1.username);
                numChecked();
                client1.disconnect();
            });
    });
});
