'use strict';

var app = angular.module("app", ['ngResource', 'authServiceProvider']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider
		.when("/user", {
			templateUrl: "public/views/user.html",
			controller: 'UserCtrl'
		})
		.when("/all", {
			templateUrl: "public/views/all.html",
			controller: 'AllCtrl'
		})
		.when("/admin", {
			templateUrl: "public/views/admin.html",
			controller: 'AdminCtrl'
		});
}]);

/* based on https://github.com/bleporini/angular-authent */
app.directive('authenticator', ['$location', '$window', function($location, $window) {
	return function(scope, elem, attrs) {
		scope.$on('event:auth-loginRequired', function() {
			$window.location.href = "/logout";
		})
	};
}]);

angular.module('authServiceProvider', []).
	config(['$httpProvider', function($httpProvider) {

		$httpProvider.responseInterceptors.push(function($q, $rootScope, $log) {
			function success(response) {
				return response;
			}

			function error(response) {
				if(response.status === 401) {
					$log.error("401!!!!");
					$rootScope.$broadcast('event:auth-loginRequired');
				}
				return $q.reject(response);
			}

			return function(promise) {
				return promise.then(success, error);
			};

		})

	}]);