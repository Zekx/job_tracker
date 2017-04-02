package android;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.AssignTechnician;
import function.RetrieveData;
import function.SendEmail;
import model.Ticket;

/**
 * Servlet implementation class AndroidAssign
 */
@WebServlet("/AndroidAssign")
public class AndroidAssign extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * This part is for when the technician accept the ticket and assign himself/herself to the ticket.
		 */
		JsonObject jsonObj = new JsonObject();
		
		if(request.getParameter("username") == null || request.getParameter("unitID") == null || request.getParameter("androidTechAssign") == null){
			jsonObj.addProperty("error", "The parameter fields are missing!");
			System.out.println("Error! Inputs are incorrect!");
			response.getWriter().println(jsonObj);
			
			return;
		}
		
		//Ticket's ID
		int id = Integer.parseInt(request.getParameter("androidTechAssign"));
		//Technician's username
		String user = request.getParameter("username");
		String position = "2";
		String unitID = request.getParameter("unitID");
		
		response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        System.out.println("Attaching values to the response.");
		
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
		Ticket ticket = rd.getTicket(id);

		Logger assignTechLog = LoggerFactory.getLogger(AssignTechnician.class);
		
		try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
			String insert_tech = "insert into assignments (ticketId, technicianUser) values (?, ?)";
			
			// Adding technician into assignment table.
			try(PreparedStatement pstmt = c.prepareStatement( insert_tech )){
	            pstmt.setInt(1, id);   
	            pstmt.setString(2, user);
	            pstmt.executeUpdate();
	            
	            assignTechLog.info("Technician " + user + " has been assigned to ticket #" + id);
			}
			
			// Get current time
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			
			// Insert new technicians into assignment table
			String insert_update = "insert into updates (ticketId, modifier, updateDetails, modifiedDate) values (?, ?, ?, ?) ";
			
			String updateMessage = "Technician(s) have been assigned to this ticket.";
			try(PreparedStatement pstmt2 = c.prepareStatement( insert_update )){
				pstmt2.setInt(1, id);
				pstmt2.setString(2, user);
				pstmt2.setString(3, updateMessage);
				pstmt2.setString(4, currentTime);
				pstmt2.executeUpdate();
				
				assignTechLog.info("Ticket #" + id + " has been updated. " + updateMessage);
			}
			
			// Update ticket table
			if(ticket.getProgress().equals("OPEN")){
				String ticket_update = "update tickets set lastUpdated = ?, Progress = ? where id = ?";
				try(PreparedStatement pstmt3 = c.prepareStatement( ticket_update )){
					pstmt3.setString(1, currentTime);
					pstmt3.setInt(2, 1);
					pstmt3.setInt(3, id);
					pstmt3.executeUpdate();
				}
				assignTechLog.info("Ticket #" + id + "'s progress has been set to 'IN PROGRESS' and"
						+ " lastUpdated has been set to " + currentTime + ".");
			}
			else{
				String ticket_update = "update tickets set lastUpdated = ? where id = ?";
				try(PreparedStatement pstmt3 = c.prepareStatement( ticket_update )){
					pstmt3.setString(1, currentTime);
					pstmt3.setInt(2, id);
					pstmt3.executeUpdate();
				}
				assignTechLog.info("Ticket #" + id + "'s lastUpdated has been set to " + currentTime + ".");
			}
			
			String domain = request.getServletContext().getAttribute("domain").toString();
			
			// Email the requester to notify them of the update
			final String requestorEmail = rd.getRequestorEmailFromTicket(id);
			
			final String requestorEmailSubject = "TECHIT - Technician has been assigned to your ticket #" + id;
			final String requestorEmailDetails = "Technician has been assigned to the following ticket: "
					+ "\n" + ticket.toString()
					+ "\n=================================================\n"
					+ "\n" + domain + "Details?id=" + id;
			final String emailFrom = request.getServletContext().getAttribute("email").toString();
			
			new Thread(new Runnable(){
				public void sendEmail(){
				SendEmail se = new SendEmail();
				se.sendEmail((Session) getServletContext().getAttribute("session"), emailFrom, requestorEmail, requestorEmailSubject, requestorEmailDetails);
				}
				public void run(){
					this.sendEmail();
				}
			}).start();
			Ticket updatedTicket = rd.getTicket(id);
			jsonObj.addProperty("id", id);
			jsonObj.addProperty("username", user);
			jsonObj.addProperty("firstname", updatedTicket.getUserFirstName());
			jsonObj.addProperty("lastname", updatedTicket.getUserLastName());
			jsonObj.addProperty("currentProgress", updatedTicket.getProgress());
			jsonObj.addProperty("phoneNumber", updatedTicket.getPhone());
			jsonObj.addProperty("email", updatedTicket.getEmail());
			jsonObj.addProperty("unitID", unitID);
			jsonObj.addProperty("startDate", updatedTicket.getStartDateTime());
			jsonObj.addProperty("endDate", (updatedTicket.getEndDate() != null) ? updatedTicket.getEndDate().toString() : "");
			jsonObj.addProperty("lastUpdated", (updatedTicket.getLastUpdated() != null) ? updatedTicket.getLastUpdated().toString() : "");
			jsonObj.addProperty("lastUpdatedTime", (updatedTicket.getLastUpdateTime() != null) ? updatedTicket.getLastUpdateTime() : "");
			jsonObj.addProperty("department", updatedTicket.getDepartment());
			jsonObj.addProperty("ticketLocation", updatedTicket.getTicketLocation());
			jsonObj.addProperty("priority", updatedTicket.getPriority());
			jsonObj.addProperty("technicians", new Gson().toJson(updatedTicket.getTechnicians()));
			jsonObj.addProperty("details", updatedTicket.getDetails());
			jsonObj.addProperty("updates", new Gson().toJson(updatedTicket.getUpdates()));
			jsonObj.addProperty("position", position);
			
			String ticketInfoInJSON = new Gson().toJson(rd.getUserTicket(user, Integer.parseInt(position), Integer.parseInt(unitID)));
			jsonObj.addProperty("updatedTickets", ticketInfoInJSON);
			response.getWriter().println(jsonObj);

		}catch(SQLException e){
			assignTechLog.error("SQL Error @ AssignTechnician (technician self-assigned).", e);
			e.printStackTrace();
		}catch(Exception e){
			assignTechLog.info("Non-SQL Error @ AssignTechnician (technician self-assigned).", e);
			e.printStackTrace();
		}
	}

}
