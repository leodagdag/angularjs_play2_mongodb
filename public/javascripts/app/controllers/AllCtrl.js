app.controller('AllCtrl', ['$scope', '$http', '$log', '$location',
	function AllCtrl($scope, $http, $log, $location) {
		$scope.all = {};
		$http.get("/all")
			.success(function(data, status, headers, config) {
				$log.info("status: " + status + " data: ")
				$log.info(data);
				$scope.all.data = data;
			})
			.error(function(data, status, headers, config) {
				$log.error("status: " + status + " data: ");
				$log.error(data);
			});
	}]);