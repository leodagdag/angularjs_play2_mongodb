app.controller('AdminCtrl', ['$scope', '$http', '$log', '$location',
	function AdminCtrl($scope, $http, $log, $location) {
		$scope.admin = {};
		$http.get("/admin")
			.success(function(data, status, headers, config) {
				$log.info("status: " + status + " data: ")
				$log.info(data);
				$scope.admin.data = data;
			})
			.error(function(data, status, headers, config) {
				$log.error("status: " + status + " data: ");
				$log.error(data);
			});
	}]);