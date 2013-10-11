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

kudos.factory('utils', function ($rootScope) {
	return {
		usernameToFullname : function(obj) {
			var convert = function(username) {
				return username.replace("."," ").replace(/\w\S*/g, function(txt) {
					return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
				});
			};

			if (typeof(obj) === "string") {
				return convert(obj);
			}
			
			return "";
		}
	};
});