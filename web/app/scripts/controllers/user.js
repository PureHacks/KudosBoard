'use strict'

kudos.controller("UserCtrl", function($scope) {
	console.log("userscope=",$scope);
	api.user().done(function (response) {
		$scope.user = response;
		$scope.$apply();
		console.log("user=",response);
		appLoading.ready();
	});
});