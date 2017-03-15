<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang='en'>
<head>
	<title>Account Management</title>
	<meta charset="utf-8">
	<meta name="viewport" http-equiv="Content-Type" content="width=device-width,initial-scale=1" charset=ISO-8859-1>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/jquery-ui.css' />">
	<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/navbar.css' />">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.12/css/dataTables.bootstrap.min.css" rel="stylesheet" />

	<script type="text/javascript" src = "https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js" ></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-3.1.1.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-ui.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/mask.js' />"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.12/js/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript">
		jQuery(function($) {
			$("#phoneNumber").mask("(999) 999-9999");
		});
		jQuery('.phoneNumber').keyup(function() {
			this.value = this.value.replace(/[^0-9\.]/g, '');
		});
		$(document).ready(function() {
			  $('#users').dataTable();
			});
		$(document).ready(function() {
			  $('#unitsTable').dataTable();
			});
		jQuery(function($) {
			$("#phoneNumber2").mask("(999) 999-9999");
		});
		jQuery('.phoneNumber2').keyup(function() {
			this.value = this.value.replace(/[^0-9\.]/g, '');
		});
	</script>

</head>
<style>
ul.tab {
	list-style-type: none;
	margin: 0;
	padding: 0;
	overflow: hidden;
	border: 1px solid #ccc;
	background-color: #f1f1f1;
}

/* Float the list items side by side */
ul.tab li {
	float: left;
}

/* Style the links inside the list items */
ul.tab li a {
	display: inline-block;
	color: black;
	text-align: center;
	padding: 14px 16px;
	text-decoration: none;
	transition: 0.3s;
	font-size: 17px;
}

/* Change background color of links on hover */
ul.tab li a:hover {
	background-color: #ddd;
}

/* Create an active/current tablink class */
ul.tab li a:focus, .active {
	background-color: #ccc;
}

/* Style the tab content */
.tabcontent {
	display: none;
	padding: 6px 12px;
	border: 1px solid #ccc;
	border-top: none;
}
</style>

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


	<!--  Side Navigation Bar not needed right now...
	-->

	<ul class="tab">
		<li><a href="javascript:void(0)" class="tablinks"
			onclick="adminaction(event, 'Search')">Search User</a></li>
		<li><a href="javascript:void(0)" class="tablinks"
			onclick="adminaction(event, 'New')">New User</a></li>
		<li><a href="javascript:void(0)" class="tablinks"
			onclick="adminaction(event, 'SearchUnit')">Search Unit</a></li>
		<li><a href="javascript:void(0)" class="tablinks"
			onclick="adminaction(event, 'Unit')">New Unit</a></li>
	</ul>

	<div id="Search" class="tabcontent">
		<h3>Search User</h3>
		<table class="table table-striped table-bordered table-hover" id="users">
			<thead class="thead-inverse">
				<tr>
					<th>Edit</th>
					<th>User name</th>
					<th>First Name</th>
					<th>Last Name</th>
					<th>Phone Number</th>
					<th>Email</th>
					<th>Department</th>
					<th>Position</th>
					<th>Unit</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items ="${userList}" var="user">
					<tr>
						<td><a href="Settings?id=${user.id}"class="navbar-btn btn btn-default">Edit</a></td>
						<td>${user.username}</td>
						<td>${user.firstName}</td>
						<td>${user.lastName}</td>
						<td>${user.phoneNumber}</td>
						<td>${user.email}</td>
						<td>${user.department }</td>
						<td>${user.statusString}</td>
						<td>${user.unitId}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<div id="New" class="tabcontent">
		<h3>New User</h3>
		<div class="row">
			<form action="AcctManagement" method="post">
				<div class="form-group col-xs-5 col-md-5">
					<label for="firstName">First Name <font color="red">*</font>
					</label> <input type="text" class="form-control" name="firstName">
				</div>

				<div class="form-group col-xs-5 col-md-5">
					<label for="lastName">Last Name <font color="red">*</font></label>
					<input type="text" class="form-control" name="lastName">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="username">Username <font color="red">*</font></label> <input
						type="text" class="form-control" name="username">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="password">Password <font color="red">*</font></label> <input
						type="text" class="form-control" name="password">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="email">Email <font color="red">*</font></label> <input
						type="text" class="form-control" name="email">
				</div>

				<div class="form-group col-xs-5 col-md-5">
					<label for="phoneNumber">Phone Number <font color="red">*</font></label>
					<input type="text" class="form-control" id="phoneNumber"
						name="phoneNumber">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="Position">Position <font color="red">*</font>
					<select class="selectpicker" id="Position" name="Position">
						<c:forEach items="${positionList }" var="position">
							<option value="${position}">${position}</option>
						</c:forEach>
					</select>
					</label>
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="Unit">Unit <font color="red">*</font>
					<select class="selectpicker" id="units" name="units" >
						<c:forEach items="${unitList}" var="unit">
							<option value="${unit.id}">${unit.name}</option>
						</c:forEach>
					</select>
					</label>
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="department">Department</label>
					<select id="department" name ="department">
					 	<c:forEach items="${departmentList}" var="dVar">
							<option value="${dVar}">${dVar}</option>
						</c:forEach>
					</select> 
				</div>
				<div class="form-group col-xs-10 col-md-10">
					<b>NOTE: <font color="red">*</font> means that the field is
						required.
					</b>
				</div>
				<div class="form-group col-xs-10 col-md-10" style="color: #FF0000;">${errorMessage}</div>
				<div class="form-group col-xs-10 col-md-10">
					<input type="submit" id="Create" name="Create" value="Create" />
				</div>

			</form>
		</div>
	</div>
	<div id="SearchUnit" class="tabcontent">
		<h3>Search User</h3>
		<table class="table table-striped table-bordered table-hover" id="unitsTable">
			<thead class="thead-inverse">
				<tr>
					<th>Edit</th>
					<th>Unit name</th>
					<th>Phone</th>
					<th>Location</th>
					<th>Email</th>
					<th>Description</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items ="${unitList}" var="unit">
					<tr>
						<td><a href="EditUnit?id=${unit.id}"class="navbar-btn btn btn-default">Edit</a></td>
						<td>${unit.name}</td>
						<td>${unit.phone}</td>
						<td>${unit.location}</td>
						<td>${unit.email}</td>
						<td>${unit.description}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="Unit" class="tabcontent">
		<h3>New Unit</h3>
		<div class="row">
			<form action="CreateUnit" method="post">
				<div class="form-group col-xs-5 col-md-5">
					<label for="unitName">Unit Name <font color="red">*</font>
					</label> <input type="text" class="form-control" name="unitName">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="phoneNumber2">Phone Number <font color="black">(optional)</font></label>
					<input type="text" class="form-control" id="phoneNumber2"
						name="phoneNumber2">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="location">Location <font color="black">(optional)</font></label> <input
						type="text" class="form-control" name="location">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="email">Email <font color="black">(optional)</font></label> <input
						type="text" class="form-control" name="email">
				</div>
				<div class="form-group col-xs-5 col-md-5">
					<label for="description">Description <font color="red">*</font></label> <input
						type="text" class="form-control" name="description">
				</div>
				<div class="form-group col-xs-10 col-md-10">
					<b>NOTE: <font color="red">*</font> means that the field is
						required.
					</b>
				</div>
				<div class="form-group col-xs-10 col-md-10" style="color: #FF0000;">${errorMessage}</div>
				<div class="form-group col-xs-10 col-md-10">
					<input type="submit" id="Create" name="Create" value="Create" />
				</div>

			</form>
		</div>
	</div>

	<script>
function adminaction(evt, action) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(action).style.display = "block";
    evt.currentTarget.className += " active";
}
</script>
</body>
</html>