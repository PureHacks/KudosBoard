kudos.directive("handleUsersQuery", function () {
	return function (scope, element, attrs) {
		// @element is the current element of jQuery type
		// @scope is the current containing scope
		// @attrs is Angular instance of current element attributes
		element.typeahead({
			name: "users",
			prefetch: {
				url: api.url() + "users",
				ttl: 0
				//ttl: 604800000,	// in milliseconds, cache data for 7 days
			},
			limit: 15
		});
	};
});