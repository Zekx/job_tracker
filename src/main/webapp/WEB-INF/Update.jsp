<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang='en'>
<head>
	<title>Update Ticket</title>
  	<meta charset="utf-8">
	<meta name="viewport" http-equiv="Content-Type" content="width=device-width,initial-scale=1" charset=ISO-8859-1>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- STYLESHEET -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href=" <c:url value='/resources/mythemes/css/jquery-ui.css' />">
	<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/navbar.css' />">
	<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/update.css' />">
	<!-- SCRIPTS -->
	<script type="text/javascript" src = "https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js" ></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-3.1.1.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-ui.js' />"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
	
	<nav class='navbar navbar-default'>
		<div class='navbar-header'>
			<a class='navbar-brand' href='#'>TechIT</a>
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
	
	
	
	<!-- ALERT MESSAGES SECTION -->
	<c:if test="${!empty fn:trim(errorMessage)}">
		<div class="alert alert-danger" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			${ errorMessage }
		</div>
	</c:if>
	
	
	<!-- Ticket information divs -->
	<div  class="container" id="ticketInfo">
		<div id="info" class="col-md-5 text-center">
			<p><b>Details</b><br></p>
			<table class="col-sm-offset-1 col-md-10">
				<tr>
					<td class="main"><b>Ticket ID</b></td>
					<td class="main2"><b>Progress</b></td>
				</tr>
				<tr>
					<td class="sub"> ${ticket.id } </td>
					<c:choose>
						<c:when test="${ticket.progress eq 'IN PROGRESS' }">
							<td class="sub" style="background-color: #F5FCA4;"> ${ticket.progress } </td>
						</c:when>
						<c:when test="${ticket.progress eq 'COMPLETED' }">
							<td class="sub" style="background-color: #DFF0D8;"> ${ticket.progress } </td>
						</c:when>
						<c:otherwise>
							<td class="sub"> ${ticket.progress } </td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td class="main" colspan="2"><b>Requester</b></td>
				</tr>
				<tr>
					<td class="sub" colspan="2"> ${ticket.userFirstName} ${ticket.userLastName }</td>
				</tr>
				<tr>
					<td class="main"><b>Phone</b></td>
					<td class="main2"><b>Email</b> </td>
					
				</tr>
				<tr>
					<td class="sub">${ticket.phone }</td>
					<td class="sub">${ticket.email }</td>
				</tr>
				<tr>
					<td class="main"><b>Date Submitted</b> </td>
					<td class="main2"><b>Department</b>
				</tr>
				<tr>
					<td class="sub">${ticket.startDate }</td>
					<td class="sub">${ticket.department }</td>
				</tr>
				<tr>
					<td class="main3" colspan="2"><b>Location of Problem</b> </td>
				</tr>
				<tr>
					<td class="sub" colspan="2">${ticket.ticketLocation }</td>
				</tr>
				<tr>
					<td class="main3" colspan="2"><b>Priority</b> </td>
				</tr>
				<tr>
					<c:choose>
							<c:when test="${ticket.priority eq 'HIGH' }">
								<td class="sub" style="color: red;" colspan="2">${ticket.priority }</td>
							</c:when>
							<c:when test="${ticket.priority eq 'MEDIUM' }">
								<td class="sub" style="color: orange;" colspan="2">${ticket.priority }</td>
							</c:when>
							<c:otherwise>
								<td class="sub" colspan="2">${ticket.priority }</td>
							</c:otherwise>
							
						</c:choose>
				</tr>
				<tr>
					<td class="main3" colspan="2"><b>Details</b> </td>
				</tr>
				<tr>
					
					<td class="sub" colspan="2">${ticket.details }</td>
				</tr>
				<tr>
					<c:if test="${ticket.numOfTechnician > 0}">
						<td class="main3" colspan="2"><b>Technicians </b></td>
					</c:if>
				</tr>
				<c:choose>
					<c:when test="${ticket.numOfTechnician > 0}">
						<c:set var="counter" value="0" scope="page"/>
							<tr>
								<td class="sub" colspan="2">
								<c:forEach items="${ticket.technicians}" var="technicians">
									<c:set var="counter" value="${counter+1}" scope="page"/>
									
									<c:choose>
										<c:when test="${counter == ticket.numOfTechnician}">
											${technicians.firstName} ${technicians.lastName } 
										</c:when>
										<c:otherwise>
											${technicians.firstName} ${technicians.lastName }, 
										</c:otherwise>
									</c:choose>
								</c:forEach>
								</td>
							</tr>
					</c:when>
				</c:choose>
			</table>
		</div>
		<div id="verticalLine" class="col-sm-offset-1"></div>
		<div id="updates" class="col-md-5 text-center pull-right">
			<p><b>Updates</b><br></p>
			<table class="table table-striped table-bordered table-hover">
			 <thead class="thead-inverse">
			    <tr>
			      <th align="center">Update Date</th>
			      <th align="center">Details</th>
			    </tr>
			  </thead>
			  <tbody>
				  <c:choose>
					<c:when test="${ticket.numOfUpdates > 0}">
					  	<c:forEach items="${ticket.updates}" var="upds">
					  		<tr>
					  			<td>${upds.modifiedDate}</td>
					  			<td>${upds.updateDetails}</td>
					  		</tr>
					  	</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="2">There are no updates for this ticket!</td>
						</tr>
					</c:otherwise>
				  </c:choose>
			  </tbody>
			</table>
		</div>
	</div>
	
	<!-- Update form div -->
	<div id="updateform" class="col-md-offset-3 col-md-6">
		<form class="form-inline" action="Update" method="post">
			<div id="formdiv">
				<input type="hidden" id="ticket_id" name="ticket_id" value="${ticket_id}">
				<input type="hidden" id="oldProg" name="oldProg" value="${ticket_progress}">
				
				<div class="form-group">
					 <label for="newProg">Progress <font color="red">*</font> </label>
					 <select id="newProg" name ="newProg">
					 	<option value="${ticket_progress}">${ticket_progress}</option>
					 	<c:forEach items="${ticketProg}" var="tprog">
					 	  	<c:if test="${tprog ne ticket_progress }">
					 	  		 <option value="${tprog}">${tprog}</option>
					 	  	</c:if>
						</c:forEach>
						</select> 
				</div>
				
				<p>Update Description: <font color="red">*</font> </p>
				<div>
					<textarea class="form-control" rows="5" style="width:100%" name="updateMessage" id="updateMessage"></textarea>
				</div>

				<div id="btn" class="text-center">
					<a class="btn btn-default" type="button" id="hmBtn" href="Details?id=${ticket.id}">Cancel Update</a>
					<button class="btn btn-outline btn-primary" name="Update" value="Update" type="submit">Submit</button>
				</div>
			</div>
		</form>
	</div>
</body>
</html>