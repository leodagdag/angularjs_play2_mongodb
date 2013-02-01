# angularjs_play2_mongodb [![Build Status](https://secure.travis-ci.org/leodagdag/angularjs_play2_mongodb.png)](http://travis-ci.org/leodagdag/angularjs_play2_mongodb)
Proof of concept using
* [Angulajs](http://angularjs.org/)
* [Play 2.1](http://www.playframework.org/)
	with [deadbolt-2](https://github.com/schaloner/deadbolt-2)
* [MongoDB](http://www.mongodb.org/)
	with [ReactiveMongo](http://reactivemongo.org/)

All security steps (login, authentication & logout) are handled by Play and deadbolt-2

Access by
* url => //<host>:<port>/ then follow navigation on screen via Angular
* absolute link (e.g. from email) //<host>:<port>/?redirect=/<path> to ensure correct redirect
* bookmarking of "Angularjs url" is not handled because of # in url

