app.controller('UserCtrl', ['$scope', '$http', '$log', '$location',
	function UserCtrl($scope, $http, $log, $location) {
		$scope.user = {};
		$http.get("/user")
			.success(function(data, status, headers, config) {
				$log.info("status: " + status + " data: ")
				$log.info(data);
				$scope.user.data = data;
			})
			.error(function(data, status, headers, config) {
				$log.error("status: " + status + " data: ");
				$log.error(data);
			});
	}]);