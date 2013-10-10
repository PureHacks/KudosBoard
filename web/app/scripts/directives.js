kudos.directive("handleUsersQuery", function () {
	return function (scope, element, attrs) {
		// @element is the current element of jQuery type
		// @scope is the current containing scope
		// @attrs is Angular instance of current element attributes
		element.typeahead({
			name: "test",
			local: ["item1","item2","item3"]
		});
	};
});