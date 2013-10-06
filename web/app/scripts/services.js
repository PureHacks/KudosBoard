kudos.factory('appLoading', function ($rootScope) {
    return {
      loading : function() {
        $rootScope.status = 'loading';
        if(!$rootScope.$$phase) $rootScope.$apply();
      },
      ready : function(delay) {
        $rootScope.status = 'ready';
        if(!$rootScope.$$phase) $rootScope.$apply();
      }
    };
});