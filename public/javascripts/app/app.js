"use strict";

var app = angular.module("app", ['ngResource', 'authServiceProvider']);

app.config(['$routeProvider', function ($routeProvider) {
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
    }])
    .directive('authenticator', function ($location) {
        return function (scope, elem, attrs) {
            scope.$on('event:auth-loginRequired', function () {
                $location.path("/login")
            })
        }
    });


angular.module('authServiceProvider', []).
    config(['$httpProvider', function ($httpProvider) {

        $httpProvider.responseInterceptors.push(function ($q, $rootScope, $log) {
            function success(response) {
//            $log.info(response)
                return response
            }

            function error(response) {
                if (response.status === 401) {
                    $log.error("401!!!!")
                    $rootScope.$broadcast('event:auth-loginRequired')
                }
                return $q.reject(response)
            }

            return function (promise) {
                return promise.then(success, error)
            }

        })

    }])