var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var Games = new Schema({
    status: {
        type: Number,
        required: true,
        default: 0
    },
});


//Export model
module.exports = mongoose.model('Game', Games);
