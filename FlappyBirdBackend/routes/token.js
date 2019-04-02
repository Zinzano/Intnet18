var express = require('express');
var model = require('../models/model_main.js');
var router = express.Router();

router.get('/', function(req, res) {
    var username = req.query.username;
    var password = req.query.password;
    console.log(username);
    model.getToken(function(token) {
        res.json({
            token: token
        });

    }, username, password);

});

module.exports = router;
