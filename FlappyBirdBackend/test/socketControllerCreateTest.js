// var should = require('should');
var assert = require('chai').assert;
var expect = require('chai').expect;
var io = require('socket.io-client');

var socketURL = 'http://127.0.0.1:8080';

var options = {
    transports: ['websocket'],
    'force new connection': true
};

// var user1 = {
//     'username': 'freddejn@mail.com',
//     'password': '1234',
//     'nickname': 'Patte'
// };

var user1 = {
    "email": "test2@mail.com",
    "password": "aaaaa",
    "nickname": "Steffe",
    "firstName": "Stefan",
    "lastName": "Larsson",
    "age": 20,
    "street": "Stora Gatan",
    "streetNumber": 88,
    "city": "Sveg",
    "country": "Sweden",
};

describe("Create", function() {
    it('Should create a new user and push it to the database.', function(done) {
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('createUser', user1);
        });
        client1.on('userCreated', function(res) {
            console.log(res);
            done();
        });
    });
});
