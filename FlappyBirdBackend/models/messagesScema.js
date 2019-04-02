var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var Messages = new Schema({
    message: {
        type: String, 
        required:true
    },

    date: {
        type: Date,
        default: Date.now
    },
});


// Virtual for author's URL
Messages
    .virtual('url')
    .get(function() {
        return '/catalog/messages/' + this._id;
    });

//Export model
module.exports = mongoose.model('Message', Messages);
