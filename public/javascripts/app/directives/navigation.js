angular.module("cra.ui.navigation", [])
	.directive('navigation', function() {
		var directiveDefinitionObject = {
			restrict: 'EA',
			templateUrl: '/public/views/layout/navigation.html',
			transclude: true,
			replace: true,
			scope: true,
			link: function(scope, element, attrs) {
				scope.dismiss = function() {
					scope.close();
				};
			}
		};

		return directiveDefinitionObject;
	});