'use strict';

kudos.controller('CardsCtrl', function ($scope, appLoading, utils) {
	api.cards().done(function (response) {
		//var updatedResponse = utils.usernameToFullname(response);
		$scope.cards = response;
		$scope.$apply();

		console.log(response);

		appLoading.ready();
	});
})

.controller('CreateCardCtrl', function ($rootScope, $scope, $http, appLoading, $cookieStore) {
	$rootScope.viewName = "create-card";
    appLoading.ready();

	$scope.createCard = function(card) {
		card.date = new Date().getTime();
		// remove this line when implementing multi-sender input
		card.senders = "shan.du";

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
    };
		
	$scope.cancel = function() {
		console.log("cancelling");
		window.history.back();
	};
});