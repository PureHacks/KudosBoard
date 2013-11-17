'use strict';

var api = (function($) {
	var apiUrl = "/props/ws/";

	return {
		url: function() {
			return apiUrl;
		},
		request: function(_url, _args) {
			if (typeof _args === "object") {
				return $.ajax({
					url: apiUrl + _url,
					data: _args,
					dataType: "json"
				});
			} else {
				return null;
			}
		},
		cards: function(_sort) {
			return this.request("cards", {
				sortBy: _sort.by,
				sortDir: _sort.direction
			});
		},
		user: function(_username) {
			return this.request("user/" + _username, {});
		},
		card: function(_cardId) {
			_cardId = parseInt(_cardId, 10) || 0;
			return this.request("card/" + _cardId, {});
		},
		myCards: function() {
			return this.request("cards/myCards", {});
		},
		cardsByUser: function(_username) {
			//TODO: needs a back end function for this
			return this.cards();
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
