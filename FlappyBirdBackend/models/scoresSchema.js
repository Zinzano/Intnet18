var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var Scores = new Schema({
    gameId: {
        type: Schema.ObjectId,
        ref: 'Game',
        required: true,
    },
    userId: {
        type: Schema.ObjectId,
        ref: 'User',
        required: true
    },
    score: {
        type: Number,
        required: true
    },
    date: {
        type: Date,
        default: Date.now
    },
});

Scores.index({
    gameId: 1,
    userId: 1
}, {
    unique: true
});
//Export model
module.exports = mongoose.model('Score', Scores);
