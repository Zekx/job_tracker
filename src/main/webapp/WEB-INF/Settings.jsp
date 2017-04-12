<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang='en'>
<head>
<title>Account Management</title>
<meta charset="utf-8">
<meta name="viewport" http-equiv="Content-Type"
	content="width=device-width,initial-scale=1" charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet"
	href="<c:url value='/resources/mythemes/css/jquery-ui.css' />">
<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/navbar.css' />">
<script type="text/javascript"
	src="<c:url value='/resources/scripts/jquery-3.1.1.min.js' />"></script>
<script type="text/javascript"
	src="<c:url value='/resources/scripts/jquery-ui.js' />"></script>
<script type="text/javascript"
	src="<c:url value='/resources/scripts/mask.js' />"></script>
<script type="text/javascript">
	jQuery(function($){
		   $("#phoneNumber").mask("(999) 999-9999");
		});
	jQuery('.phoneNumber').keyup(function () {  
	    this.value = this.value.replace(/[^0-9\.]/g,''); 
	});
	</script>
</head>
<body onload="onLoadUp()">
	<nav class='navbar navbar-default'>
		<div class='navbar-header'>
			<a class='navbar-brand' href='Home'>TechIT</a>
			<p class='navbar-text'>Signed in as ${sessionScope.firstname}
				${sessionScope.lastname }</p>

			<a href='Logout' type='button' id='logout-button'
				class='navbar-btn btn btn-default logout-button navbar-right'>Logout</a>
				
			<a class="navbar-btn btn btn-default setting-button" href = 'Settings'>My Settings</a>
			<c:if test="${sessionScope.position == 0 }">
				<a class="navbar-btn btn btn-default account-button" href = 'AcctManagement'>Account Manager</a>
			</c:if>	
		</div>
	</nav>

	<!--  Side Navigation Bar not needed right now...
	<div class="col-md-2" id="line-left">
		<ul class="nav nav-pills nav-stacked pull-right">
			<li class="active"><a href="#">Home</a></li>
			<li><a href="Add">Add</a></li>
			<li><a href="#"> ${sessionScope.emailId} </a></li>
			<li><a href="#"> ${sessionScope.distinguishedName} </a></li>
			<li><a href="#">Add</a></li>
		</ul>
	</div>
	-->
	
	<c:if test="${!empty fn:trim(errorMessage)}">
		<div class="alert alert-danger" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			${ errorMessage }
		</div>
	</c:if>
	<c:if test="${!empty fn:trim(successMessage)}">
		<div class="alert alert-success" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			${ successMessage }
		</div>
	</c:if>
	
		<div class="container">
			<div class="col-sm-offset-2">
				<c:choose>
					<c:when test="${not adminModify}">
					
						<form action="Settings" method="post">
							<input type="hidden" name="adminModify" value="false">
							<div class="form-group col-xs-5 col-md-5" >
								<label for="firstName">First Name <font color="red">*</font> </label>
							    <input type="text" class="form-control" name="firstName" value="${sessionScope.firstname}" disabled>
							</div>
							 
							 <div class="form-group col-xs-5 col-md-5">
							    <label for="lastName">Last Name <font color="red">*</font></label>
							    <input type="text" class="form-control" name="lastName" value="${sessionScope.lastname}" disabled>
							</div>
						
							<div class="form-group col-xs-10 col-md-10">
								<label for="email">Email <font color="red">*</font></label> 
								<input type="text" class="form-control" name="email" value="${email}">
							</div>
							
							<div class="form-group col-xs-10 col-md-10">
								<label for="phoneNumber">Phone Number <font color="red">*</font></label>
								<input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${phoneNumber}">
							</div>
								
							<div class="form-group col-xs-10 col-md-10">
								<label for="department">Department (Optional)</label> 
								<select id="department" name ="department">
								 	<c:forEach items="${departmentList}" var="dVar">
								 		<c:choose>
									 	  	<c:when test="${dVar ne sessionScope.department }">
									 	  		 <option value="${dVar}">${dVar}</option>
									 	  	</c:when>
									 	  	<c:otherwise>
									 	  		<option value="${dVar}" selected>${dVar}</option>
									 	  	</c:otherwise>
								 	  	</c:choose>
									</c:forEach>
								</select> 
							</div>
							
							<div class="container form-group text-center col-md-10 col-xs-10">
								<a class="btn btn-default" type="button" id="hmBtn" href="Home"> <span class="glyphicon glyphicon-home" aria-hidden="true"></span> Back to Home</a>
							 	<button class="btn btn-primary" name="Save" value="Save" type="submit">Confirm</button>
							</div>
						</form>
					</c:when>
					
					<c:otherwise>
					
						<form action="Settings" method="post">
							<input type="hidden" name="adminModify" value="true">
							<input type="hidden" name="userId" value="${editUser.id}">
							<div class="form-group col-xs-5 col-md-5" >
								<label for="firstName">First Name <font color="red">*</font> </label>
							    <input type="text" class="form-control" name="firstName" value="${editUser.firstName}" disabled>
							</div>
							 
							 <div class="form-group col-xs-5 col-md-5">
							    <label for="lastName">Last Name <font color="red">*</font></label>
							    <input type="text" class="form-control" name="lastName" value="${editUser.lastName}" disabled>
							</div>
						
							<div class="form-group col-xs-10 col-md-10">
								<label for="email">Email <font color="red">*</font></label> 
								<input type="text" class="form-control" name="email" value="${editUser.email}">
							</div>
							
							
							<div class="form-group col-xs-10 col-md-10">
								<label for="phoneNumber">Phone Number <font color="red">*</font></label>
								<input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${editUser.phoneNumber}">
							</div>
							
							<div class="form-group col-xs-5 col-md-5">
								<label for="department">Department (Optional)</label> 
								<select id="department" name ="department">
								 	<c:forEach items="${departmentList}" var="dVar">
								 		<c:choose>
									 	  	<c:when test="${dVar ne editUser.department }">
									 	  		 <option value="${dVar}">${dVar}</option>
									 	  	</c:when>
									 	  	<c:otherwise>
									 	  		<option value="${dVar}" selected>${dVar}</option>
									 	  	</c:otherwise>
								 	  	</c:choose>
									</c:forEach>
								</select> 
							</div>
							
							<div class="form-group col-xs-5 col-md-5">
								<label for="phoneNumber">Position <font color="red">*</font></label><br>
								<select class="selectpicker" id="position" name="position">
									<option value="${editUser.statusString}">${editUser.statusString}</option>
									<c:forEach items="${positionList}" var="currPosition">
										<c:if test="${editUser.statusString ne currPosition}">
											<option value="${currPosition}">${currPosition}</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
							
							<div class="container form-group text-center col-md-10 col-xs-10">
								<a class="btn btn-default" type="button" id="hmBtn" href="Home"> <span class="glyphicon glyphicon-home" aria-hidden="true"></span> Back to Home</a>
							 	<button class="btn btn-primary" name="Save" value="Save" type="submit">Confirm</button>
							</div>
						</form>
						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
</body>
</html>