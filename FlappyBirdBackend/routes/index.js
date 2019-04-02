var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    res.setHeader('Last-Modified', (new Date()).toUTCString());
    res.json({index:'data'});
});

module.exports = router;
