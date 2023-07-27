const mongoose = require('mongoose');

const commentSchema = new mongoose.Schema({
    comicId: { type: mongoose.Schema.Types.ObjectId, ref: 'Comic', required: true },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    content: { type: String, required: true },
    timestamp: { type: Date, default: Date.now },
});

const Comment = mongoose.model('Comment', commentSchema);

module.exports = Comment;