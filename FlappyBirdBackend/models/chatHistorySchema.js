var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var ChatHistorys = new Schema({
    userId: {
        type: Schema.ObjectId,
        ref: 'User',
        required: true
    },
    messageId: {
        type: Schema.ObjectId,
        ref: 'Message',
        required: true
    },
});

// // // Run when converting the object to json
ChatHistorys.methods.toJSON = function() {
    var history = this.toObject();
    var user = history.userId.user;
    var message = history.messageId.message;
    return {
        user: user,
        message: message
    };
};

// Virtual for author's URL
ChatHistorys
    .virtual('chatHistory')
    .get(function() {
        // return '/catalog/chatHistory/' + this._id;
        return this.userId.nickname + ' ' + this.messageId.message;
    });

//Export model
module.exports = mongoose.model('ChatHistory', ChatHistorys);
