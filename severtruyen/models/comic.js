const mongoose = require('mongoose');

const comicSchema = new mongoose.Schema({
    name: String,
    description: String,
    author: String,
    yearPublished: Number,
    coverImage: String, // Lưu public_id của ảnh bìa từ Cloudinary
    images: [String], // Lưu danh sách public_id của các ảnh nội dung truyện từ Cloudinary
    comments: [{
        userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        fullname: String,
        content: String,
        createdAt: { type: Date, default: Date.now },
    }, ],
});

const Comic = mongoose.model('Comic', comicSchema);

module.exports = Comic;