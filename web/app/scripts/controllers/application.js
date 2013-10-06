kudos.controller('AppCtrl', function ($rootScope, appLoading) {
  $rootScope.topScope = $rootScope;  
  $rootScope.$on('$routeChangeStart', function () {
      appLoading.loading();
  });
})