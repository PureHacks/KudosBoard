var api = (function() {
	var apiUrl = "/api/",
		lang = "en",
		validLang = ["en", "fr"];
	return {
		request: function(_url, _arg) {
			if (typeof _arg === "object") {
				return $.ajax({
					//url: apiUrl + _url,
					url: "/data/" + _url + ".json",
					data: _arg,
					dataType: "json"
				});
			} else {
				return null;
			}
		},
		login: function(username, password) {

		},
		user: function(_userId) {
			return this.request({
				"id": parseInt(_userId, 10) || 0,
				"lang": lang
			});
		},
		card: function(_cardId) {
			return this.request("card", {
				"id": parseInt(_cardId, 10) || 0,
				"lang": lang
			});
		},
		cardsByUser: function(_userId) {
			return this.request("cards", {
				"id": parseInt(_userId, 10) || 0,
				"lang": lang
			});
		},
		cardsByTag: function(_tag) {
			return this.request("cards", {
				"tag": _tag,
				"lang": lang
			});
		},
		cards: function() {
			return this.request("cards", {
				"lang": lang
			});
		},
		search: function(_query) {
			if (_query) {
				return this.request("", {
					"input": _query,
					"lang": lang
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
