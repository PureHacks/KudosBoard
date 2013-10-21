kudos.directive("handleUsersQuery", function() {
	return function (scope, element, attrs) {
		// @element is the current element of jQuery type
		// @scope is the current containing scope
		// @attrs is Angular instance of current element attributes
		console.log("handleUsersQuery");
		element.typeahead({
			name: "users",
			prefetch: {
				url: api.url() + "users",
				ttl: 10000
				//ttl: 604800000,	// in milliseconds, cache data for 7 days
			},
			limit: 15,
			template: function(data) {
				console.log("data=",data);
				var html = "", key, item;
				for (key in data) {
					item = data[key];
					html += '<span>' + item.userName + "</span>";
				}
				return '<div>' + html + '</div>';
			}
		});
	};
});