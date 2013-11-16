'use strict'

kudos.controller("UserCtrl", function ($scope, $routeParams, appLoading) {
	api.user($routeParams.username).done(function (response) {
		$scope.user = response;
		$scope.$apply();
		console.log("user=",response);
		appLoading.ready();
	});
});