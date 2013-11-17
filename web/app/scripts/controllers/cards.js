'use strict';

kudos.controller('CardsCtrl', function ($scope, $location, $routeParams, appLoading) {
	var sort = {
		by: ($routeParams.sortBy == undefined ? "date" : $routeParams.sortBy),
		direction: ($routeParams.sortDir == undefined ? "desc" : $routeParams.sortDir)
	}

	api.cards(sort).done(function (response) {
		$scope.cards = response;
		$scope.$apply();
		appLoading.ready();
	});
})

.controller('CreateCardCtrl', function ($rootScope, $scope, $http, appLoading, $cookies) {
	$rootScope.viewName = "create-card";
	appLoading.ready();	
	
	var currentUser = $cookies.username;
	
	api.user(currentUser).done(function(response) {
		$scope.card = {
			senders: [response.userName]
			//sendersName : [response.firstName + " " + response.lastName]
		};
		$scope.$apply();
		console.log("fetch user = ",response);
	});
	
	$scope.createCard = function(card) {
		$http({
			method : 'PUT',
			url : api.url() + "card",
			data : card
		}).success(function(response) {
			console.log('create card success, response=',response);
			window.location.href = "/props/";
		}).error(function(error) {
			console.log('create card error, error=',error);
			window.location.href = "/props/login";
		});
	};
	
	$scope.cancel = function() {
		console.log("cancelling");
		window.location.href = "/props/";
	};
})

.controller('ViewCardCtrl', function ($rootScope, $scope, $routeParams, appLoading, $cookieStore) {
	api.card($routeParams.cardId).done(function (response) {
		$scope.card = response;
		$scope.$apply();
		appLoading.ready();
	});
})


.controller('CardsByUserCtrl', function ($scope, $routeParams, appLoading) {
	api.cards().done(function (response) {
		$scope.cards = response;
		$scope.$apply();
		appLoading.ready();
	});
});