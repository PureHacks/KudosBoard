'use strict';

kudos.controller('CardsCtrl', function ($scope) {
	api.cards().done(function (response) {
		$scope.cards = response;
		$scope.$apply();
	});
});