'use strict';

var kudos = angular.module('kudosApp', []);

kudos.config(function ($routeProvider, $httpProvider) {
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

kudos.controller('AppCtrl', function ($rootScope, appLoading) {
  $rootScope.topScope = $rootScope;  
  $rootScope.$on('$routeChangeStart', function () {
      appLoading.loading();
  });
})
