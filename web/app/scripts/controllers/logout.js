'use strict';

kudos.controller("LogoutCtrl", function($scope, $http, appLoading) {
	//console.log("$scope=",$scope);
	
	$http({
		method : 'POST',
		url : api.url() + "logout"
	}).success(function(response) {
		appLoading.ready();	
		window.location.href = "/props/login";
	}).error(function(error) {
		appLoading.ready();	
		console.log('weird...can\'t logout? error=',error);
		$scope.logoutError = true;
		//window.location.href = "/props/login";
	});
});