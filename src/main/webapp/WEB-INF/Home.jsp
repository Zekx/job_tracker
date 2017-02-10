<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang='en'>
<head>
<title>TechIT Home</title>
<meta charset="utf-8">
<meta name="viewport" http-equiv="Content-Type"
	content="width=device-width,initial-scale=1" charset=ISO-8859-1>
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- STYLESHEET -->
<link rel="stylesheet"
	href=" <c:url value='/resources/mythemes/css/jquery-ui.css' />">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet"
	href="<c:url value='/resources/mythemes/css/home.css' />">
<link rel="stylesheet"
	href="<c:url value='/resources/mythemes/css/navbar.css' />">

<!-- SCRIPTS -->
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="text/javascript"
	src="<c:url value='/resources/scripts/jquery-3.1.1.min.js' />"></script>
<script type="text/javascript"
	src="<c:url value='/resources/scripts/jquery-ui.js' />"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function() {

		$("#accordion").accordion({
			collapsible : true,
			heightStyle : "content"
		});
		$("#accordion_2").accordion({
			collapsible : true,
			heightStyle : "content"
		});
		$("#accordion_3").accordion({
			collapsible : true,
			heightStyle : "content"
		});
	});

	function onLoadUp() {
		document.getElementById("rv").style.display = "block";
		document.getElementById("av").style.display = "none";
		document.getElementById("cv").style.display = "none";
	};

	function switchView(selectView) {
		if (selectView == 1) {
			document.getElementById("recent").className = "active";
			document.getElementById("active").className = "";
			document.getElementById("completed").className = "";
			currentView = 1;

			document.getElementById("rv").style.display = "block";
			document.getElementById("av").style.display = "none";
			document.getElementById("cv").style.display = "none";
		} else {
			if (selectView == 2) {
				document.getElementById("recent").className = "";
				document.getElementById("active").className = "active";
				document.getElementById("completed").className = "";
				currentView = 2;

				document.getElementById("rv").style.display = "none";
				document.getElementById("av").style.display = "block";
				document.getElementById("cv").style.display = "none";
			} else {
				document.getElementById("recent").className = "";
				document.getElementById("active").className = "";
				document.getElementById("completed").className = "active";
				currentView = 3;

				document.getElementById("rv").style.display = "none";
				document.getElementById("av").style.display = "none";
				document.getElementById("cv").style.display = "block";
			}
		}
	};

	function validateForm() {
		var input = document.forms["rejectForm"]["rejectInput"].value;
		if (input == '' || input == null) {
			alert("Please input a reason for the decline.");
			event.preventDefault();
		}
	}

	function cleanInput(type, id) {
		if (type == "reject") {
			if (document.forms["rejectForm"]["rejectInput"].value != ''
					&& document.forms["rejectForm"]["rejectInput"].value != null) {
				document.forms["rejectForm"]["rejectInput"].value = '';
			}

			document.forms["rejectForm"]["cancelBt"].value = id;
		} else {

			document.forms["cancelForm"]["cancelBt"].value = id;
		}

	}
</script>
<style>
.table th {
   text-align: center;   
}
form {
	display: inline;
}
</style>
</head>
<body onload="onLoadUp()">
	<nav class='navbar navbar-default'>
		<div class="container-fluid">
			<div class='navbar-header'>
				<a class='navbar-brand' href='#'>TechIT</a>
				<p class='navbar-text'>Signed in as ${sessionScope.firstname}
					${sessionScope.lastname }</p>

				<a href='Logout' type='button' id='logout-button'
					class='navbar-btn btn btn-default logout-button navbar-right'>Logout</a>

				<a class="navbar-btn btn btn-default setting-button" href='Settings'>My
					Settings</a>
				<c:if test="${sessionScope.position == 0 }">
					<a class="navbar-btn btn btn-default account-button"
						href='AcctManagement'>Account Manager</a>
				</c:if>
			</div>
		</div>
	</nav>



	<!-- This section holds the tabs and ticket view for the users. -->

	<div class="text-center col-lg-12" style="text-align: center;">
		<!-- Contains the Create Ticket button, and search capabilities -->
		<div class="container-fluid">
			<span class="pull-left"> <a href="CreateTicket"
				class="btn btn-default">Create Ticket</a>
			</span>

			<form action="Search" method="post">
				<div class="col-lg-offset-2 ">
					<input name="search" id="search" class="form-control"
						placeholder="Search Ticket" />
				</div>
				<button class="navbar-btn btn btn-default" name="searchBtn"
					value="Search" type="submit">Search</button>
				<button class="navbar-btn btn btn-default" name="searchBtn"
					value="Reset" type="submit">Reset</button>
			</form>
		</div>



		<ul class="nav nav-tabs pull-center">
			<li id="recent" class="active" onclick="switchView(1)"><a
				href="#">RECENT</a></li>
			<li id="active" class="" onclick="switchView(2)"><a href="#">ACTIVE</a></li>
			<li id="completed" class="" onclick="switchView(3)"><a href="#">COMPLETED</a></li>
		</ul>


		<!-- ------------------------------------------------ RECENT TICKETS SECTION ----------------------------------------------- -->


		<div id="rv">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th>Ticket Number</th>
						<th>Requester Name</th>
						<th>Summary</th>
						<th>Date Submitted</th>
						<th>Status</th>
						<th>More Details</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${tickets}" var="item">
						<tr>
							<td>${item.id }</td>
							<td>${item.user }</td>
							<td>${item.details }</td>
							<td>${item.startDate }</td>
							<td>${item.progress }</td>
							<td><a href='Details?id=${item.id }' type='button'
								class="btn btn-primary" id='Details'>Details</a></td>
						</tr>
					</c:forEach>


				</tbody>
			</table>
		</div>

		<!-- ------------------------------------------------- ACTIVE TICKETS SECTION ----------------------------------------------- -->
		<div id="av">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th>Ticket Number</th>
						<th>Requester Name</th>
						<th>Summary</th>
						<th>Date Submitted</th>
						<th>Status</th>
						<th>More Details</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${tickets}" var="item">
						<c:choose>
							<c:when
								test="${item.progress ne 'COMPLETED' and item.progress ne 'CLOSED' }">
								<tr>
									<td>${item.id }</td>
									<td>${item.user }</td>
									<td>${item.details }</td>
									<td>${item.startDate }</td>
									<td>${item.progress }</td>
									<td><a href='Details?id=${item.id }' type='button'
										class="btn btn-primary" id='Details'>Details</a></td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
		</div>




		<!-- -------------------------------------- COMPLETED / CLOSED TICKETS SECTION ----------------------------------------------- -->




		<div id="cv">
			<table class="table table-bordered">
				<thead >
					<tr>
						<th>Ticket Number</th>
						<th>Requester Name</th>
						<th>Summary</th>
						<th>Date Submitted</th>
						<th>Status</th>
						<th>More Details</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${tickets}" var="item">
						<c:choose>
							<c:when
								test="${item.progress eq 'COMPLETED' or item.progress eq 'CLOSED' }">
								<tr>
									<td>${item.id }</td>
									<td>${item.user }</td>
									<td>${item.details }</td>
									<td>${item.startDate }</td>
									<td>${item.progress }</td>
									<td><a href='Details?id=${item.id }' type='button'
										class="btn btn-primary" id='Details'>Details</a></td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>

		</div>
	</div>
</body>
</html>