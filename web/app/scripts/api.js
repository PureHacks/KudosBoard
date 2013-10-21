'use strict';

var api = (function($) {
	var apiUrl = "/props/ws/";

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
		cards: function() {
			return this.request("cards", {});
		},
		user: function(_username) {
			return this.request("user/" + _username, {});
		},
		card: function(_cardId) {
			return this.request("card", {
				"id": parseInt(_cardId, 10) || 0
			});
		},
		cardsByUser: function(_username) {
			return this.request("cards", {
				"username": _username
			});
		},
		cardsByTag: function(_tag) {
			return this.request("cards", {
				"tag": _tag
			});
		},
		createCard: function(_cardData) {
			return this.request("create/card", {
				"recipients": _cardData.recipients,
				"senders": _cardData.senders,
				"message": _cardData.message
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
})(window.jQuery || {});
