function toggleSection(name, btn) {

	$(".navigation").find(".nav-btn").removeClass("active");
	$(btn).addClass("active");
	$(".main").find(".dashboard-section").hide();
	$(name).show();
}