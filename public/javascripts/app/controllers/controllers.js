app.controller('LoginCtrl', ['$scope', '$http', '$log', '$location',
	function LoginCtrl($scope, $http, $log, $location) {
		$scope.user = {
			username: 'admin',
			password: 'pwd'
		};
		$scope.loginError = false;

		$scope.authenticate = function(user) {
			$http.post("/auth", user)
				.success(function(data, status, headers, config) {
					$log.log("status: " + status + " data: ");
					$log.log(data);
					$location.path("/secured");
				})
				.error(function(data, status, headers, config) {
					$log.error(" error status: " + status + " data: " + data);
					$scope.loginError = true;
				});
		}
	}]);

app.controller('ProtectedCtrl', ['$scope', '$http', '$log', '$location',
	function ProtectedCtrl($scope, $http, $log, $location) {
		$scope.payload = {};
		$http.get("/secured")
			.success(function(data, status, headers, config) {
				$log.info("status: " + status + " data: ")
				$log.info(data);
				$scope.payload.test = data;
			})
			.error(function(data, status, headers, config) {
				$log.error("status: " + status + " data: ");
				$log.error(data);
			});

		$scope.logout = function() {
			$http.get("/logout")
				.success(function(data, status, headers, config) {
					$location.path("/login");
				})
		}
	}]);