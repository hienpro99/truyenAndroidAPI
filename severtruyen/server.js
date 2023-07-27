// server.js
const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const multer = require('multer');
const cloudinary = require('cloudinary').v2;
const path = require('path');
const app = express();
const publicDirectoryPath = path.join(__dirname, 'uploads');
app.use(express.static(publicDirectoryPath));
const session = require('express-session');
const PORT = process.env.PORT || 3000;
// Sử dụng bodyParser để parse các request body 
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
// Import các model
const Comic = require('./models/comic');
const User = require('./models/user');
const Comment = require('./models/comment');
app.use(
    session({
        secret: 'dansjwoquwijxjkslajagsnnnc12cfg',
        resave: false,
        saveUninitialized: false,
        cookie: {
            maxAge: 3600000,
        },
    })
);
// Cloudinary configuration
cloudinary.config({
    cloud_name: 'dlr07nzvv',
    api_key: '881323273122696',
    api_secret: 'fj1shg-KiVA3EIoUQ1NDku7nv7o'
});

// Kết nối MongoDB
mongoose.connect('mongodb+srv://hienddph20890:jQnfzsMkLRttjEDa@cluster0.twhjzn8.mongodb.net/truyentranh?retryWrites=true&w=majority', {
    useNewUrlParser: true,
    useUnifiedTopology: true,
});

//kết nối thành công
mongoose.connection.on('connected', () => {
    console.log('Connected to MongoDB!');
});

//lỗi trong quá trình kết nối
mongoose.connection.on('error', (err) => {
    console.error('Error connecting to MongoDB:', err);
});


// Cấu hình multer
const storage = multer.diskStorage({
    destination: (req, file, callback) => {
        callback(null, path.join(__dirname, './uploads'));
    },
    filename: (req, file, callback) => {
        const validImageTypes = ['image/png', 'image/jpeg'];
        if (!validImageTypes.includes(file.mimetype)) {
            const errorMess = `The file <strong>${file.originalname}</strong> is invalid. Only allowed to upload image jpeg or png.`;
            return callback(errorMess, null);
        }
        const filename = `${Date.now()}-Hienddph20890-${file.originalname}`;
        callback(null, filename);
    },
});

const upload = multer({ storage });
const requireLogin = (req, res, next) => {
    if (!req.session.user) {
        res.json({ msg: 'Vui lòng đăng nhập ' });
        res.status(500).json({ error: 'Failed to login.' }); // Người dùng chưa đăng nhập
    } else {
        next(); // Người dùng đã đăng nhập, tiếp tục thực hiện thao tác thêm sản phẩm
    }
};
// API đăng ký tài khoản
app.post('/register', async(req, res) => {
    const { username, password, email, fullname } = req.body;
    try {
        // Kiểm tra xem username và email đã tồn tại trong cơ sở dữ liệu chưa
        const existingUser = await User.findOne({ $or: [{ username }, { email }] });
        if (existingUser) {
            return res.status(400).json({ error: 'username hoặc email đã tồn tại' });
        }

        // Tạo mới người dùng
        const newUser = await User.create({ username, password, email, fullname });
        res.status(201).json({ newUser, msg: 'đăng kí thành công' });
    } catch (err) {
        res.status(500).json({ error: 'Lỗi đăng kí' });
    }
});

// API đăng nhập
app.post('/login', async(req, res) => {
    const { username, password } = req.body;
    try {
        // Tìm người dùng trong cơ sở dữ liệu bằng username
        const user = await User.findOne({ username });
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        // Kiểm tra mật khẩu có khớp hay không
        if (password !== user.password) {
            return res.status(401).json({ error: 'Invalid password' });
        }
        // Lưu thông tin đăng nhập vào session
        req.session.user = {
            id: user._id,
            username: user.username,
            email: user.email,
            fullname: user.fullname,
        };
        if (user.admin) {
            req.session.isAdmin = true;
        }


        res.json({ user, msg: 'đăng nhập thành công' });
    } catch (err) {
        res.status(500).json({ error: 'Error logging in' });
    }
});
// API Xem Dữ liệu người dùng
app.get('/listuser', requireLogin, async(req, res) => {
    try {
        const user = req.session.user;
        if (!user) {
            return res.status(401).json({ error: 'chưa đăng nhập' });
        }

        const currentUser = await User.findOne({ email: user.email });
        if (!currentUser.admin) {
            return res.status(403).json({ error: 'chỉ ADMIN Mới có quyền truy cập' });
        }

        const arrUser = await User.find();

        res.json({ account: arrUser });
    } catch (error) {
        console.error('Lỗi lấy dữ liệu', error);
        res.status(500).json({ error: 'Lỗi lấy dữ liệu' });
    }
});

//API Thêm truyện mới
app.post('/comics', upload.array('comicImages'), async(req, res) => {
    try {
        const { name, description, author, yearPublished } = req.body;

        // Upload cover image to Cloudinary
        const coverImageResult = await cloudinary.uploader.upload(req.files[0].path);

        // Upload comic images to Cloudinary and get their public_ids
        const imagesResults = await Promise.all(
            req.files.slice(1).map(async(comicImage) => await cloudinary.uploader.upload(comicImage.path))
        );

        const comic = new Comic({
            name,
            description,
            author,
            yearPublished,
            coverImage: coverImageResult.secure_url,
            images: imagesResults.map((result) => result.secure_url),
        });

        const savedComic = await comic.save();
        res.status(201).json(savedComic);
    } catch (error) {
        res.status(500).json({ error: 'Failed to create comic' });
    }
});


//API Lấy Tất Cả truyện
app.get('/comics', async(req, res) => {
    try {
        const comics = await Comic.find();
        res.json({ comics: comics });
    } catch (err) {
        res.status(500).json({ error: 'Lỗi lấy truyện' });
    }
});
// API XEM chi tiết truyện theo ID
app.get('/comics/:id', async(req, res) => {
    const comicId = req.params.id;
    try {
        const comic = await Comic.findById(comicId);
        if (comic) {
            res.json(comic);
        } else {
            res.status(404).json({ error: 'Comic not found' });
        }
    } catch (err) {
        res.status(500).json({ error: 'Error fetching comic' });
    }
});
// API Sửa truyện theo id
app.put('/comics/:id', upload.array('comicImages'), async(req, res) => {
    try {
        const comicId = req.params.id;
        const { name, description, author, yearPublished } = req.body;

        // Tìm truyện tranh đã tồn tại trong cơ sở dữ liệu
        const existingComic = await Comic.findById(comicId);
        if (!existingComic) {
            return res.status(404).json({ error: 'Không tìm thấy truyện tranh' });
        }

        // Tải lên ảnh bìa mới lên Cloudinary nếu có được cung cấp
        if (req.files[0]) {
            const coverImageResult = await cloudinary.uploader.upload(req.files[0].path);
            existingComic.coverImage = coverImageResult.secure_url;
        }

        // Tải lên ảnh truyện tranh mới lên Cloudinary nếu có được cung cấp
        if (req.files.length > 1) {
            const imagesResults = await Promise.all(
                req.files.slice(1).map(async(comicImage) => await cloudinary.uploader.upload(comicImage.path))
            );
            existingComic.images = imagesResults.map((result) => result.secure_url);
        }

        // Cập nhật các trường thông tin khác nếu có được cung cấp
        existingComic.name = name || existingComic.name;
        existingComic.description = description || existingComic.description;
        existingComic.author = author || existingComic.author;
        existingComic.yearPublished = yearPublished || existingComic.yearPublished;

        const updatedComic = await existingComic.save();
        res.status(200).json(updatedComic);
    } catch (error) {
        res.status(500).json({ error: 'Cập nhật truyện tranh thất bại' });
    }
});


//API XÓa truyện
app.delete('/comics/:id', async(req, res) => {
    const comicId = req.params.id;
    try {
        const comic = await Comic.findByIdAndDelete(comicId);
        if (comic) {
            res.json({ msg: 'xóa thành công dữ liệu', comic });
        } else {
            res.status(404).json({ error: 'truyện không tồn tại' });
        }
    } catch (err) {
        res.status(500).json({ error: 'Lỗi xóa truyện' });
    }
});


// API Xem ALL comment theo id truyện
app.get('/comments/:comicId', async(req, res) => {
    const comicId = req.params.comicId;
    try {
        const comic = await Comic.findById(comicId).select('comments');
        res.json(comic.comments);
    } catch (err) {
        res.status(500).json({ error: 'Error fetching comments' });
    }
});

// API POST để thêm bình luận vào truyện dựa trên id truyện
app.post('/comics/:id/comments', requireLogin, async(req, res) => {
    try {
        const comicId = req.params.id;
        const { content } = req.body;
        const userId = req.session.user.id;
        const fullname = req.session.user.fullname;

        // Kiểm tra truyện tranh có tồn tại hay không
        const existingComic = await Comic.findById(comicId);
        if (!existingComic) {
            return res.status(404).json({ error: 'Không tìm thấy truyện tranh' });
        }
        // Kiểm tra nội dung bình luận không được để trống
        if (!content || content.trim() === '') {
            return res.status(400).json({ error: 'Nội dung bình luận không được để trống' });
        }

        // Thêm bình luận vào mảng comments của truyện
        existingComic.comments.push({
            userId: userId,
            fullname: fullname,
            content: content,
        });

        // Lưu truyện cập nhật vào cơ sở dữ liệu
        const updatedComic = await existingComic.save();

        res.status(201).json(updatedComic);
    } catch (error) {
        res.status(500).json({ error: 'Có lỗi xảy ra khi thêm bình luận' });
    }
});

// API chỉnh sửa comment theo id truyện và id bình luận
app.put('/comments/:comicId/:commentId', requireLogin, async(req, res) => {
    const comicId = req.params.comicId;
    const commentId = req.params.commentId;
    const updatedCommentContent = req.body.content;

    try {
        const comic = await Comic.findById(comicId);
        if (!comic) {
            return res.status(404).json({ error: 'Truyện không tồn tại' });
        }

        const comment = comic.comments.find((c) => c._id.toString() === commentId);
        if (!comment) {
            return res.status(404).json({ error: 'Bình luận không tồn tại' });
        }

        const loggedInUserId = req.session.user.id;

        // Kiểm tra xem userId của người đăng nhập có trùng với userId của comment hay không
        if (loggedInUserId.toString() !== comment.userId.toString()) {
            return res.status(403).json({ error: 'Bạn không có quyền sửa bình luận này' });
        }
        // Kiểm tra nội dung bình luận không được để trống
        if (!updatedCommentContent || updatedCommentContent.trim() === '') {
            return res.status(400).json({ error: 'Nội dung bình luận không được để trống' });
        }

        // Cập nhật nội dung comment
        comment.content = updatedCommentContent;
        await comic.save();

        res.json(comment);
    } catch (err) {
        res.status(500).json({ error: 'Error updating comment' });
    }
});
// API Xóa Comment theo id truyện và id comment. chỉ có ADMIN MỚi xóa được truyện
app.delete('/comments/:comicId/:commentId', requireLogin, async(req, res) => {
    const comicId = req.params.comicId;
    const commentId = req.params.commentId;

    try {
        // Kiểm tra xem người dùng đăng nhập có quyền admin hay không
        // (ví dụ: kiểm tra thông qua session hoặc JWT token)
        // Ở đây, chúng ta sẽ giả định có một trường isAdmin trong đối tượng user của session
        const isAdmin = req.session.isAdmin;

        // Nếu người dùng không phải admin, từ chối yêu cầu xóa bình luận
        if (!isAdmin) {
            return res.status(403).json({ error: 'Bạn không có quyền xóa bình luận này' });
        }

        // Xóa bình luận dựa trên idcomic và idcomment
        const comic = await Comic.findById(comicId);
        if (!comic) {
            return res.status(404).json({ error: 'Truyện không tồn tại' });
        }

        const comment = comic.comments.find((c) => c._id.toString() === commentId);
        if (!comment) {
            return res.status(404).json({ error: 'Bình luận không tồn tại' });
        }

        // Xóa bình luận khỏi mảng comments của comic
        comic.comments.pull({ _id: comment._id });
        await comic.save();

        res.json({ message: 'Bình luận đã được xóa thành công' });
    } catch (err) {
        res.status(500).json({ error: 'Error deleting comment' });
    }
});
// phân quyền theo id người dùng với vị trí cao nhất là admin
app.get('/PhanQuyen/:id', requireLogin, async(req, res) => {
    const UserID = req.params.id;
    const user = await User.findById(UserID);

    // Kiểm tra nếu không tìm thấy uer
    if (!user) {
        return res.status(404).send('Người dùng không tồn tại.');
    }
    const isAdmin = req.session.isAdmin;

    // Nếu người dùng không phải admin, từ chối yêu cầu xóa bình luận
    if (!isAdmin) {
        return res.status(403).json({ error: 'Bạn không có quyền dùng chức năng này' });
    }

    // Thông tin sửa đổi
    const updateData = {
        $set: {
            admin: true,
        }
    };
    // Cập nhật quyền và chuyển hướng trở lại
    await User.updateOne({ _id: UserID }, updateData).exec();
    res.json({ user, msg: 'Phân quyền thành công' })
});
//lấy thông tin đăng nhập
app.get('/profile', requireLogin, async(req, res) => {
    try {
        const user = req.session.user;
        if (!user) {
            res.json('Bạn chưa đăng nhập');
        } else {
            res.json(user);
            console.log(user);
        }
    } catch (error) {
        console.error('Lỗi khi lấy thông tin người dùng:', error);
        res.status(500).send('Lỗi khi lấy thông tin người dùng.');
    }
});

// Khởi chạy server
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});