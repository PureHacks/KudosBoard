var api = (function() {
	var apiUrl = "http://internal-dev.tor.nurun.com/props/ws/";
	
	return {
		url: function() {
			return apiUrl;
		},
		request: function(_url, _arg) {
			if (typeof _arg === "object") {
				return $.ajax({
					url: apiUrl + _url,
					//url: "/data/" + _url + ".json",
					data: _arg,
					dataType: "json"
				});
			} else {
				return null;
			}
		},
		login: function(username, password) {

		},
		cards: function() {
			return this.request("cards", {});
		},
		user: function(_userId) {
			return this.request("user", {
				"id": parseInt(_userId, 10) || 0
			});
		},
		card: function(_cardId) {
			return this.request("card", {
				"id": parseInt(_cardId, 10) || 0
			});
		},
		cardsByUser: function(_userId) {
			return this.request("cards", {
				"id": parseInt(_userId, 10) || 0
			});
		},
		cardsByTag: function(_tag) {
			return this.request("cards", {
				"tag": _tag
			});
		},
		search: function(_query) {
			if (_query) {
				return this.request("", {
					"input": _query
				});
			} else {
				console.error("Search method requires a keyword to search");
			}
		},
		keyword: function(_query) {
			return this.search(_query);
		}
	};
})();
