'use strict';

var kudos = angular.module('kudosApp', []);

kudos.config(function ($routeProvider) {
  $routeProvider
  .when('/', {
    templateUrl: 'views/cards.html',
    controller: 'CardsCtrl'
  })
  .when('/login', {
    templateUrl: 'views/login.html',
    controller: 'LoginCtrl'
  })
  .when('/search/:query', {
    templateUrl: 'views/cards.html',
    controller: 'CardsCtrl'
  })
  .when('/cards/user/:userId', {
    templateUrl: 'views/cards.html',
    controller: 'CardsCtrl'
  })
  .when('/cards/tag/:tag', {
    templateUrl: 'views/cards.html',
    controller: 'CardsCtrl'
  })
  .when('/card/:cardId', {
    controller: 'CardsCtrl',
    templateUrl: 'partials/card.html'
  })
  .otherwise({
    redirectTo: '/'
  });
});
