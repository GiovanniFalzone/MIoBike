/*$(document).ready( function() {
	console.log("ciao");
	console.log($.("#subs_ID").text());
	/*$.get("UserDataServlet", function(resp) {
		
	});*/
	
	/*$.get("CoapServlet", function(resp) {
		console.log("ciao"+resp);
		alert(resp);
    });*/
$(document).ready(function() {
	var id = $("#subs_ID").text();
	$.get("../SubscriptionDataServlet", {subscription_ID:id}, function(resp) {
		console.log(resp);
	});
});