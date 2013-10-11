'use strict';

var kudos = angular.module('kudosApp', ['ngCookies']);

kudos.config(function ($routeProvider, $httpProvider) {
	$routeProvider
	.when('/', {
		templateUrl: 'views/cards.html',
		controller: 'CardsCtrl'
	})
	.when('/search/:query', {
		templateUrl: 'views/cards.html',
		controller: 'CardsCtrl'
	})
	.when('/cards/tag/:tag', {
		templateUrl: 'views/cards.html',
		controller: 'CardsCtrl'
	})
	.when('/user/:userId', {
		templateUrl: 'views/user.html',
		controller: 'CardsCtrl'
	})
	.when('/card/:cardId/comment', {
		controller: 'CardsCtrl',
		templateUrl: 'partials/comment.html'
	})
	.when('/create/card', {
		templateUrl: 'views/create.html',
		controller: 'CreateCardCtrl'
	})
	.when('/comment/:cardId', {
		templateUrl: 'views/comment.html',
		controller: 'CommentCtrl'
	})
	.otherwise({
		redirectTo: '/'
	});
});

kudos.controller('AppCtrl', function ($rootScope, appLoading, $cookieStore, $http) {
	$rootScope.topScope = $rootScope;
	$rootScope.$on('$routeChangeStart', function () {
			appLoading.loading();
	});
})
.controller("LoginCtrl", function($rootScope) {
	$rootScope.topScope = $rootScope;
	$rootScope.awesomeThings = [
		'HTML5 Boilerplate',
		'AngularJS',
		'Karma',
		{ 'Team Kudos' : ['Alex','Dana','Ellen','Kate','Michelle','Sandra','Shan'] }
	];
});
