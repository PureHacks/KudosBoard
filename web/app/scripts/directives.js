kudos.directive("handleUsersQuery", function() {
	return function (scope, element, attrs) {
		// @element is the current element of jQuery type
		// @scope is the current containing scope
		// @attrs is Angular instance of current element attributes
		console.log("handleUsersQuery");
		element.typeahead({
			name: "users",
			prefetch: {
				url: api.url() + "search",
				ttl: 10000
				//ttl: 604800000,	// in milliseconds, cache data for 7 days
				/*
				// this filter method also works!!!
				filter: function(data){
					var filterData = [];
					for (var i=0; i<data.length; i++) {
						filterData.push({
							value : data[i].email,
							token : [data[i].firstName, data[i].lastName],
							firstName : data[i].firstName,
							lastName : data[i].lastName
						});
					}
					return filterData;
				}
				*/				
			},
			limit: 12,
			template: function(data) {
				//console.log("data=",data);
				return '<span>' + data.firstName + " " + data.lastName + '</span>';
			}
		});
	};
});