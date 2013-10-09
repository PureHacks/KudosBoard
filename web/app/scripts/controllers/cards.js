'use strict';

kudos.controller('CardsCtrl', function ($scope, appLoading) {
	api.cards().done(function (response) {
		$scope.cards = response;
		$scope.$apply();

        console.log(response);

        appLoading.ready();
	});
})

.controller('CreateCardCtrl', function ($rootScope, $scope, $http, appLoading) {
	$rootScope.viewName = "create-card";
    appLoading.ready();

	$scope.createCard = function(card) {
		card.date = new Date().getTime();
		// remove this line when implementing multi-sender input
		card.senders = "shan.du";
		console.log("$scope=",$scope," $rootScope=",$rootScope," card=",card);
        $http({
            method : 'POST',
            url : api.url() + "create/card",
            data : card
        }).success(function (response) {
        	console.log('create card success');
        	//console.log(response);
        }).error(function (error) {
        	console.log('create card error');
        	//console.log(error);
        });
    }
});