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
	.when('/user/:username', {
		templateUrl: 'views/user.html',
		controller: 'UserCtrl'
	})
	.when('/card/:cardId', {
		controller: 'CardCtrl',
		templateUrl: 'partials/card.html'
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
	.when('/logout', {
		templateUrl: 'views/logout.html',
		controller: 'LogoutCtrl'
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
	$rootScope.awesomeThings = [
		'HTML5 Boilerplate',
		'AngularJS',
		'Karma',
		{ 'Team Kudos' : ['Alex','Dana','Ellen','Kate','Michelle','Sandra','Shan'] }
	];
});
