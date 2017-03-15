<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang='en'>
<head>
<title>Details</title>
<meta charset="utf-8">
<meta name="viewport" http-equiv="Content-Type" content="width=device-width,initial-scale=1" charset=ISO-8859-1>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/jquery-ui.css' />">
<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/navbar.css' />">
<link rel="stylesheet" href="<c:url value='/resources/mythemes/css/details.css' />">
<link href="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.12/css/dataTables.bootstrap.min.css" rel="stylesheet" />

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-3.1.1.min.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/scripts/jquery-ui.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/scripts/mask.js' />"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<body>
	<nav class='navbar navbar-default'>
		<div class='navbar-header'>
			<a class='navbar-brand' href='Home'>TechIT</a>
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
	</nav>

	<!-- Cancel Modal Pop up -->
	<div class="modal fade" id="myModalCancel" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">

				<!-- Modal header -->

				<div class="modal-header" id="cancelModalHeader">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Ticket Cancellation
						Confirmation</h4>
				</div>

				<!-- Modal Body Content -->

				<div class="modal-body" id="cancelModalBody">
					<p>Are you sure you want to cancel this ticket?</p>
				</div>

				<!-- Modal Footer & Button options -->

				<div class="modal-footer">
					<form class="Cancel" name="cancelForm" action="Cancel"
						method="post">
						<button type="button" class="btn btn-danger" data-dismiss="modal">
							No</button>
						<button type="submit" class="btn btn-success" name="cancelBt"
							id="cancelBt" value="${ticket.id}">Yes</button>
					</form>

				</div>

			</div>
		</div>
	</div>
	
	<!-- Accept Ticket Modal Pop up -->
	<div class="modal fade" id="myModalAccept" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">

				<!-- Modal header -->

				<div class="modal-header" id="acceptModalHeader">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Accept Ticket
						Confirmation</h4>
				</div>

				<!-- Modal Body Content -->

				<div class="modal-body" id="acceptModalBody">
					<p>By accepting this ticket, you will be assigning yourself as the technician who is working to complete this job.</p>
					<p>Are you sure you want to accept this ticket?</p>
				</div>

				<!-- Modal Footer & Button options -->

				<div class="modal-footer">
					<form class="AssignTechnician" name="AssignTechnicianForm" action="AssignTechnician"
						method="post">
						<button type="button" class="btn btn-danger" data-dismiss="modal">No</button>
						<button type="submit" class="btn btn-success" name="assignTechnicianBt"
							id="assignTechnicianBt" value="${ticket.id}">Yes</button>
					</form>

				</div>

			</div>
		</div>
	</div>

	<!-- Reject Modal Pop up -->
	<div class="modal fade" id="myModalReject" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">

				<!-- Modal header -->

				<div class="modal-header" id="rejectModalHeader">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Ticket Decline
						Confirmation</h4>
				</div>


				<form class="Cancel" name="rejectForm" onsubmit="validateForm();"
					action="Cancel" method="post">
					<!-- Modal Body Content -->
					<div class="modal-body" id="rejectModalBody">
						<p>Are you sure you want to decline this ticket?</p>
						<p>Please input the reason for decline:</p>
						<textarea class="form-control" rows="2" style="width: 100%"
							name="rejectInput" id="rejectInput"></textarea>
					</div>

					<!-- Modal Footer & Button options -->

					<div class="modal-footer" id="rejectModalFooter">
						<button type="button" class="btn btn-danger" data-dismiss="modal">
							No</button>
						<button type="submit" class="btn btn-success" name="cancelBt"
							id="cancelBt" value="${ticket.id}">Yes</button>


					</div>
				</form>
			</div>
		</div>
	</div>
	
	
	<!-- SET TICKET'S PRIORITY -->
	<div class="modal fade" id="myModalPriority" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">

				<!-- Modal header -->

				<div class="modal-header" id="priorityModalHeader">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Set Priority</h4>
				</div>


				<form class="Priority" name="priorityForm" action="Priority" method="post">
					<!-- Modal Body Content -->
					<div class="modal-body" id="priorityModalBody">
						<p>Please select the level of priority:</p>
						<select class="selectPriority" id="selectPriority" name="selectPriority">
						  <option value="0">NOT ASSIGNED</option>
						  <option value="1">LOW</option>
						  <option value="2">MEDIUM</option>
						  <option value="3">HIGH</option>
						</select>
						<input type="hidden" id="ticketId" name="ticketId" value="${ticket.id}">
					</div>

					<!-- Modal Footer & Button options -->

					<div class="modal-footer" id="priorityModalFooter">
						<button type="button" class="btn btn-danger" data-dismiss="modal">No</button>
						<button type="submit" class="btn btn-success" name="priorityBt"
							id="priorityBt" value="${ticket.id}">Yes</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	

	<!-- ALERT MESSAGES SECTION -->
	<c:if test="${!empty fn:trim(errorMessage)}">
		<div class="alert alert-danger" role="alert">
			<button type="button" class="close" data-dismiss="alert"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			${ errorMessage }
		</div>
	</c:if>
	<c:if test="${!empty fn:trim(successMessage)}">
		<div class="alert alert-success" role="alert">
			<button type="button" class="close" data-dismiss="alert"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			${ successMessage }
		</div>
	</c:if>
	
	 <!-- Buttons -->
	<div class="container text-center" id="btnDiv">	
		<div class="btn-group" id="btnGroup">		
			<!-- Back to home -->
			<!-- Glyph icon by http://glyphicons.com/ -->
			<a class="btn btn-default" type="button" id="hmBtn" href="Home"> <span class="glyphicon glyphicon-home" aria-hidden="true"></span> Back to Home</a>
			<c:if test="${(sessionScope.user eq ticket.user and ticket.progress eq 'OPEN') or sessionScope.position == 0}">
				<!--  Cancel  -->
				<button type="button" class="btn btn-default"
					onClick="cleanInput('cancel', ${ticket.id})" data-toggle="modal"
					data-target="#myModalCancel">Cancel Ticket</button>
				<!--   Edit   -->
				<a href="EditTicket?id=${ticket.id}"
					class="btn btn-default">Edit Ticket</a>
			</c:if>
	
			<c:if test="${ sessionScope.position == 1 and ticket.progress eq 'OPEN'  and sessionScope.user ne ticket.user}">
				<button type="button" class="btn btn-default"
					onClick="cleanInput('reject', ${ticket.id})" data-toggle="modal"
					data-target="#myModalReject">Decline Ticket</button>
			</c:if>
	
	
	
			<c:if test="${sessionScope.position <= 2}">
				<a href="Update?id=${ticket.id}" type="button"
					class="btn btn-default">Update Ticket</a>
			</c:if>
			<c:if test="${sessionScope.position <= 1 and ticket.progress ne 'COMPLETED' and ticket.progress ne 'CLOSED'}">
				<a href="AssignTechnician?id=${ticket.id}&prog=${ticket.progress}" type="button" class="btn btn-default" >Assign Technician</a> 
				<button type="button" class="btn btn-default" data-toggle="modal" data-target="#myModalPriority">Set Priority</button>
			</c:if>
			<c:if test="${sessionScope.position == 2 and assigned eq 'false' and ticket.progress ne 'CLOSED'}">
				<button type="button" class="btn btn-default" data-toggle="modal" data-target="#myModalAccept">Accept Ticket</button>
			</c:if>
		</div>
	</div>

	<!-- TICKET INFORMATION -->
	<div class="container" id="ticketAndUpdateContainer">
		<div id="ticketInfo" class="col-md-5 text-center">
			<h4><b>Details</b></h4>
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
					<td class="main3" colspan="2"><b>Requester</b></td>
				</tr>
				<tr>
					<td class="sub" colspan="2"> ${ticket.userFirstName } ${ticket.userLastName } </td>
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
					
					<td class="sub">${ticket.startDate } ${ticket.startDateTime}</td>
					<td class="sub">${ticket.department }</td>
				</tr>
				<tr>
					<td class="main3" colspan="2"><b>Location of Problem</b> </td>
				</tr>
				<tr>
					<td class="sub" colspan="2">${ticket.ticketLocation }</td>
				</tr>
				<tr>
					<td class="main3" colspan="2"><b>Priority</b>
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
					<td class="main3" colspan="2"><b>Subject</b></td>
				</tr>
				<tr>
					<td class="sub" colspan="2">${ticket.subject }</td>
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
		<div id="ticketUpdates" class="col-xs-5 text-center">
			<c:choose>
				<c:when test="${ticket.numOfUpdates > 0}">
					<h4><b>Updates</b></h4>
					<table class="table table-striped table-bordered table-hover text-center">
						<thead>
							<tr>
								<th>Update Date</th>
								<th>Details</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${ticket.updates}" var="upds">
								<tr>
									<td>${upds.modifiedDate}</td>
									<td>${upds.updateDetails}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<h4><b>Updates</b></h4>
					<table class="table table-striped table-bordered table-hover text-center">
						<thead>
							<tr>
								<th align="center">Update Date</th>
								<th align="center">Details</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="2"> There are no updates for this ticket!</td>
							</tr>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</body>