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

	/*$.get("../SubscriptionDataServlet", {subscription_ID:id}, function(resp) {
		
		
		console.log(resp);
	});*/
	
	var user_id = $("#user_ID").text();
	$.get("../UserInfoServlet", {user_ID:user_id}, function(resp) {
		
		/*TODO
		 * 
		 * resp format
		 * {
		 * 	"subscription_info": 
		 * 		{
		 * 			"balance":"",
		 * 			"activation_date":"",
		 * 			"expiration_date":""
		 * 		},
		 * 	"user_statistics":
		 * 		{
		 * 			"tot_cal":"",
		 * 			"avg_speed":"",
		 * 			"trips":"",
		 * 			"ID_stat":"",
		 * 			"avg_km":"",
		 * 			"max_speed":"",
		 * 			"tot_km":"",
		 * 			"fav_bike":""
		 * 		},
		 * 	"trips":
		 * 		{
		 * 			"total_count":"",
		 * 			"total_km":"",
		 * 			"total_cal":""
		 * 		},
		 * 	"daily_trips":
		 * 		{
		 * 			"daily_km":"",
		 * 			"daily_cal":"",
		 * 			"daily_count":""
		 * 		}
		 * 	}
		 */

		var user = JSON.parse(resp);
		
		var bal = user.subscription_info.balance;
		$("#balance").text(bal);
		if(bal>0)
			$("#balance").addClass("green");
		else
			$("#balance").addClass("red");
		
		var today = new Date();
		var exp_date = user.subscription_info.expiration_date;
		exp_date = stringToDatetime(exp_date);
		
		var remaining_days = diffDays(today, exp_date);
		
		$("#exp-date").text(datetimeToString(exp_date, true));
		
		if(remaining_days>0)
			$("#remaining-time").text("You have "+remaining_days+" days remaining");
		else
			$("#remaining-time").text("Your subscription expired "+Math.abs(remaining_days)+" days ago");

		$("#daily-trips").text(user.daily_trips.daily_count);
		$("#daily-km").text(user.daily_trips.daily_km);
		$("#daily-cal").text(user.daily_trips.daily_cal);
		
		$("#trips").text(user.trips.total_count);
		$("#km").text(user.trips.total_km);
		$("#cal").text(user.trips.total_cal);
		
	});
});

function datetimeToString(date, notime) {
	if(notime)
		return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	else
		return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+".0";
}

function stringToDatetime(date) {
	return new Date(date.split(' ')[0])
}

function diffDays(date1, date2) {
	var millis = date2-date1;
	var secs = millis/1000;
	var min = secs/60;
	var hours = min/60;
	var days = hours/24;
	return Math.floor(days);
	
}

