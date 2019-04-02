/*
 * The required packages and variable declarations
 */
var express = require('express');
var app = express();
var router = express.Router();
var path = require('path');
var http = require('http');
var httpServer = http.Server(app);
var bodyParser = require('body-parser');
var logger = require('morgan');
var socketController = require('./controllers/socketController');
// var favicon = require('serve-favicon');
var favicon = require('./routes/ignoreFavico.js');
var port = 8080;
var model = require('./models/model_main.js');

/*
 * Makes an initial connection to the database.
 */
var mongoose = require('mongoose');
var url = "mongodb://freddejn:NAbnapkuwrarbA5@ds249128.mlab.com:49128/flappy-database";
mongoose.connect(url);
mongoose.Promise = global.Promise;
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

httpServer.listen(port, function() {
    console.log('server listening on port', port);
});

/*
 * The socket magic
 */
model.getDatabaseData(function(res) {});

var io = require('socket.io').listen(httpServer);
//Handler for socket events
io.on('connection', function(socket) {
    socketController.handle(socket, io);
});

/*
 * Added routes, put in route folder to use.
 */
var index = require('./routes/index');
var getToken = require('./routes/token');

/*
 * For detailed logging and debugging.
 */
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

app.use('/', index);
app.use('/token', getToken);
// app.use('/socket', socket);

/*
 * No view engine is used comment out to implement.
 */
// view engine setup
// app.set('views', path.join(__dirname, 'views'));
// app.set('view engine', 'jade');
app.disable('etag');


/*
 * Catch 404 and forward to error handler
 */
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/*
 * Error handler
 */
app.use(function(err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};
    res.status(err.status || 500);
    console.log('error');
    console.log(err);
});

module.exports = app;
