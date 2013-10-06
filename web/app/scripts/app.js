'use strict';

var kudos = angular.module('kudosApp', []);

kudos.config(function ($routeProvider, $httpProvider) {
  $httpProvider.defaults.useXDomain = true;
  delete $httpProvider.defaults.headers.common['X-Requested-With'];

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
  .when('/create', {
    templateUrl: 'views/create.html',
    controller: 'CreateCardCtrl'
  })
  .otherwise({
    redirectTo: '/'
  });
});
