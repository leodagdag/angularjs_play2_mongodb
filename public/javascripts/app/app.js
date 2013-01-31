"use strict";

var app = angular.module("app", ['ngResource']);

app.config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when("/user", {
				templateUrl: "public/views/user.html",
				controller: 'UserCtrl'
			})
			.when("/all", {
				templateUrl: "public/views/all.html",
				controller: 'UserAdminCtrl'
			})
			.when("/admin", {
				templateUrl: "public/views/admin.html",
				controller: 'AdminCtrl'
			});
	}]);