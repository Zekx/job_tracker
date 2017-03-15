<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang='en'>
<head>
	<title>TechIT Login</title>
  	<meta charset="utf-8">
	<meta name="viewport" http-equiv="Content-Type" content="width=device-width,initial-scale=1" charset=ISO-8859-1>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/login.css' />">
	
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-3.1.1.min.js'/>" async></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-ui.js' />" async></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" async></script>
</head>

<body>
	<div class="container">
		<div class="jumbotron container-fluid">
			<h1 align=center>
				<!-- <small>				
					TechIT!
				</small> -->
			</h1>
		</div>
		<c:if test="${ not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span class="close" data-dissmiss="alert" aria-hidden="true">&times;</span>
				</button>
				${ errorMessage }
			</div>
		</c:if>
		<div class="container col-md-offset-2 col-md-8" id="infos">
			<p>TECHIT is a work-order system created for ECST's TechOPs.</p>
			<p>If you have a request not related to the above, please report it to the designated facility.</p>
			<p><b>If you have an emergency, please call 911 or contact the emergency lines!</b></p>
		</div>
		<div class="container col-md-offset-4 col-md-4" id="login-form">
			<form class="form-signin" action="Login" method="post">
				<div class="row" id="row1">
					<h4 class="form-signin-heading">Please sign in using your MyCSULA account below</h4>
					<label for="username" class="sr-only">Username</label> 
						<input type="text" name="username" id="username" class="form-control" placeholder="Username" /> 
						
					<label for="password" class="sr-only">Password</label> 
					<input type="password" name="password" id="password" class="form-control" placeholder="Password" />
				</div>
				<div class="row" id="row2">
					<button class="btn btn-lg btn-primary btn-block" name="Login" value="Login" type="submit">Sign in</button>
				</div>
			</form>

		</div>
	</div>

</body>
</html>
