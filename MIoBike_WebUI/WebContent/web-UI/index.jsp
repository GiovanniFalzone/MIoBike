<!DOCTYPE html>
<html lang="en">
<head>

	<title> MIo Bike - Home</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<link type="text/css" href="css/style.css" rel="stylesheet">
	
	<link rel="shortcut icon" href="img/icons/favicon.png" />
	<!--link href="css/bootstrap.min.css" rel="stylesheet">
	<script src="js/jquery-3.3.1.min.js" ></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/map.js"></script>
	<script src="http://www.openlayers.org/api/OpenLayers.js"></script-->
	
</head>
<body class="full-bg">
	<div class="full-overlay">
		<div class="centered login bg-color1-transparent">
			<div class="login-container">
				<h1> MIo Bike</h1>
				<form action="../LoginServlet" method="post">
					<div class="container">
						<div class="row">
						<div class="error-message" style="color: #FF0000;">
        					<% 	if(session.getAttribute("errorMessage") != null){
									String err = session.getAttribute("errorMessage").toString();
									out.print(err);
								}
							%>
        					</div>
							<label for="uname"><b>Username</b></label>
						</div>
						<div class="row">
							<input class="login-input" type="text" placeholder="Enter Username" name="username" required>
						</div>
						<div class="row">
							<label for="psw"><b>Password</b></label>
						</div>
						<div class="row">
							<input class="login-input" type="password" placeholder="Enter Password" name="password" required>
						</div>
						<div class="row">
							<button class="usr-btn login-btn" name="user" type="submit">Log as User</button>
							<button class="login-btn admin-btn" name="admin" type="submit">Log as Admin</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div class="footer-copyright bg-color1-transparent py-3">© 2018 Copyright: <a> MIo Bike team</a></div>
	</div>
</body>

</html>