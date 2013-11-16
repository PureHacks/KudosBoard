'use strict';

kudos.controller('CardsCtrl', function ($scope, $location, appLoading) {
	api.cards().done(function (response) {
		$scope.cards = response;
		$scope.$apply();
		console.log("cards=",response);
		appLoading.ready();
	});

	$scope.viewCard = function(cardId) {
		$location.path('/card/' + cardId);
	};
})

.controller('CreateCardCtrl', function ($rootScope, $scope, $http, appLoading, $cookieStore) {
	$rootScope.viewName = "create-card";
	appLoading.ready();	
	
	var currentUser = getCookie("username");
	
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
		console.log("card=", response);
		appLoading.ready();
	});
})


.controller('MyCardCtrl', function ($rootScope, $scope, appLoading, $cookieStore) {
	api.myCards().done(function (response) {
		$scope.card = response;
		$scope.$apply();
		console.log("my card=", response);
		appLoading.ready();
	});
});

/* I just wanna get some cookie values, Angular Y U NO cookies */
function getCookie(c_name)
{
	var c_value = document.cookie;
	var c_start = c_value.indexOf(" " + c_name + "=");
	if (c_start == -1)
  {
		c_start = c_value.indexOf(c_name + "=");
	}
	if (c_start == -1)
  {
		c_value = null;
	}
	else
  {
		c_start = c_value.indexOf("=", c_start) + 1;
		var c_end = c_value.indexOf(";", c_start);
		if (c_end == -1)
		{
			c_end = c_value.length;
		}
		c_value = unescape(c_value.substring(c_start,c_end));
	}
	return c_value;
}