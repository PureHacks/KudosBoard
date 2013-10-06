'use strict';

kudos.controller('CardsCtrl', function ($scope, appLoading) {
	api.cards().done(function (response) {
		$scope.cards = response;
		$scope.$apply();

        appLoading.ready();
	});
})

.controller('CreateCardCtrl', function ($rootScope, $scope, $http) {
	$rootScope.viewName = "create-card";

	$scope.createCard = function(card) {
		card.sender = "pauline.ramos";
		card.date = new Date().getTime();
        $http({
            method : 'POST',
            url : api.url() + "card",
            data : card
        }).success(function (response) {
        	console.log('create card success');
        	console.log(response);
        }).error(function (error) {
        	console.log('create card error');
        	console.log(error);
        });
    }
});