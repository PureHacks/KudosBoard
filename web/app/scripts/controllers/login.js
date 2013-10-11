'use strict';

kudos.controller("ldapLoginCtrl", function($rootScope, $scope, $http, $cookieStore) {
	$scope.ldapLogin = function(userAuth) {
		//console.log("user name=",userAuth.username, " password=",userAuth.password, " cookie=",$cookieStore);
		$http({
			method : 'POST',
			url : api.url() + "login",
			data : userAuth
			}).success(function (response) {
				console.log('create card success');
				console.log(response);
				$cookieStore.put("username",userAuth.username);
				window.location.href = "/props/";
			}).error(function (error) {
				console.log('create card error');
				console.log(error);
				$scope.loginForm.$setValidity("invalidLogin",false);
		});
	};
});