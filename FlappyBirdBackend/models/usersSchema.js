var mongoose = require('mongoose');
var uniqueValidator = require('mongoose-unique-validator');
var bcrypt = require('bcrypt');

var Schema = mongoose.Schema;

var Users = new Schema({
    email: {
        type: String,
        unique: [true, 'Email alread exists'],
        required: true
    },
    password: {
        type: String,
        required: true
    },
    nickname: {
        type: String,
        unique: true,
        required: true
    },
    firstName: {
        type: String,
        required: false
    },
    lastName: {
        type: String,
        required: false
    },
    street: {
        type: String,
        required: false
    },
    streetNumber: {
        type: Number,
        required: false
    },
    city: {
        type: String,
        required: false
    },
    country: {
        type: String,
        required: false
    },
    age: {
        type: Number,
        required: false
    },
});


// Run when converting the object to json
Users.methods.toJSON = function() {
    var user = this.toObject();
    delete user.password;
    return user;
};

// Compares the passwords with the hashed password
Users.methods.comparePassword = function(candidatePassword, cb) {
    bcrypt.compare(candidatePassword, this.password, function(err, isMatch) {
        if (err) {
            return cb(err);
        } else {
            return cb(null, isMatch);

        }
    });
};

// Saves a hashed password
Users.pre('save', function(next) {
    if (this.password) {
        if (this.isModified('password')) {
            var salt = bcrypt.genSaltSync(10);
            this.password = bcrypt.hashSync(this.password, salt);
        }
    }
    next();
});


Users.plugin(uniqueValidator);

//Export model
module.exports = mongoose.model('User', Users);
