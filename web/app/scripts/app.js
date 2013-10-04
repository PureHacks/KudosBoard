'use strict';

angular.module('kudosApp', [])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl'
      })
      .when('/props', {
        templateUrl: 'views/props.html',
        controller: 'PropsCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
