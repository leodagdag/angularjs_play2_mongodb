"use strict";

var app = angular.module("angularAuth", ['authServiceProvider','cra.ui.navigation']);

app.config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when("/", {
				templateUrl: "public/views/protectedContent.html",
				controller: 'ProtectedCtrl'
			})
			.when("/login", {
				templateUrl: "public/views/login.html",
				controller: 'LoginCtrl'
			})
			.otherwise({redirectTo: "/"})
	}])
	.directive('authenticator', function($location) {
		return function(scope, elem, attrs) {
			scope.$on('event:auth-loginRequired', function() {
				$location.path("/login")
			})
		}
	});

angular.module('authServiceProvider', [])
	.config(['$httpProvider', function($httpProvider) {

		$httpProvider.responseInterceptors.push(function($q, $rootScope, $log) {
			function success(response) {
//            $log.info(response)
				return response
			}

			function error(response) {
				if(response.status === 401) {
					$log.error("401!!!!")
					$rootScope.$broadcast('event:auth-loginRequired')
				}
				return $q.reject(response)
			}

			return function(promise) {
				return promise.then(success, error)
			}
		})
	}]);