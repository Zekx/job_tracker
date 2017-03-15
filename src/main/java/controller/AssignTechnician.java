package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import function.RetrieveData;
import function.SendEmail;
import model.Ticket;
import model.User;

@WebServlet("/AssignTechnician")
public class AssignTechnician extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("user") == null) {
			response.sendRedirect("Login");
		}else if(Integer.parseInt(request.getSession().getAttribute("position").toString()) >= 2){
			response.sendRedirect("Home");
		}
		else {
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			int unitId = Integer.parseInt(request.getSession().getAttribute("unit_id").toString());
			int ticketId = Integer.parseInt(request.getParameter("id"));
			String ticketProgress = request.getParameter("prog");
			
			List<User> techs = rd.getUnitTechnicians(unitId);
			Map<String, Boolean> ticketTechs = rd.getTechId(ticketId);
			
			if(techs == null || ticketTechs == null){
				request.setAttribute("errorMessage", "Something went wrong when getting the technicians, please try again later.");
				request.getServletContext().getRequestDispatcher("/Details.jsp?id="+ticketId).forward(request, response);
			}
			else{
				request.setAttribute("ticket_progress", ticketProgress);
				request.setAttribute("ticket_id", ticketId);
				request.setAttribute("techList", rd.getUnitTechnicians(unitId) );
				request.setAttribute("techVerify", rd.getTechId(ticketId));
				
				request.getRequestDispatcher("/WEB-INF/AssignTechnician.jsp").forward(request, response);
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * There are two cases for assigning technician. The first case is where a supervisor assigns technician(s)
		 * to a ticket. The second case is where a technician assign himself/herself to the ticket.  
		 */
		if(Integer.parseInt(request.getSession().getAttribute("position").toString()) != 2){
			/*
			 * This part is for when the supervisor assigns technician(s) to the ticket.
			 */
			String[] technicians = request.getParameterValues("tech");
			
			String ticketProgress = request.getParameter("ticket_progress");
			int ticketId = Integer.parseInt(request.getParameter("ticket_id")); 
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			Map<String, Boolean> techVerify = rd.getTechId(ticketId);
			Logger assignTechLog = LoggerFactory.getLogger(AssignTechnician.class);
			
			try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				
				
				String insert_tech = "insert into assignments (ticketId, technicianUser) values (?, ?)";
				boolean insert = false;
				List<String> techs = new ArrayList<String>();
				
				for(String tech : technicians ){
					if(!techVerify.containsKey(tech)){
						insert = true;
						try(PreparedStatement pstmt = c.prepareStatement( insert_tech )){
				            pstmt.setInt(1, ticketId);   
				            pstmt.setString(2, tech);
				            pstmt.executeUpdate();
				            
				            techs.add(rd.getEmailFromUsername(tech));
				            assignTechLog.info("Technician " + tech + " has been assigned to ticket #" + ticketId);
						}
					}
				}
				
				if(insert){
			
					// Get current time
					java.util.Date dt = new java.util.Date();
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentTime = sdf.format(dt);
					
					// Insert new technicians into assignment table
					String insert_update = "insert into updates (ticketId, modifier, updateDetails, modifiedDate) values (?, ?, ?, ?) ";
					String updateMessage = "Technician(s) have been assigned to this ticket.";
					try(PreparedStatement pstmt2 = c.prepareStatement( insert_update )){
						pstmt2.setInt(1, ticketId);
						pstmt2.setString(2, request.getSession().getAttribute("user").toString());
						pstmt2.setString(3, updateMessage);
						pstmt2.setString(4, currentTime);
						pstmt2.executeUpdate();
						
						assignTechLog.info("Ticket #" + ticketId + " has been updated. " + updateMessage);
					}
					
					// Update ticket table
					if(ticketProgress.equals("OPEN")){
						String ticket_update = "update tickets set lastUpdated = ?, Progress = ? where id = ?";
						try(PreparedStatement pstmt3 = c.prepareStatement( ticket_update )){
							pstmt3.setString(1, currentTime);
							pstmt3.setInt(2, 1);
							pstmt3.setInt(3, ticketId);
							pstmt3.executeUpdate();
						}
						assignTechLog.info("Ticket #" + ticketId + "'s progress has been set to 'IN PROGRESS' and"
								+ " lastUpdated has been set to " + currentTime + ".");
					}
					else{
						String ticket_update = "update tickets set lastUpdated = ? where id = ?";
						try(PreparedStatement pstmt3 = c.prepareStatement( ticket_update )){
							pstmt3.setString(1, currentTime);
							pstmt3.setInt(2, ticketId);
							pstmt3.executeUpdate();
						}
						assignTechLog.info("Ticket #" + ticketId + "'s lastUpdated has been set to " + currentTime + ".");
					}
					
					String domain = request.getServletContext().getAttribute("domain").toString();
					
					//Email the technicians
					if(techs.size() > 0) {
						final List<String> allEmails = techs;
						final String requestorEmail = rd.getRequestorEmailFromTicket(ticketId);
						final String emailSubject = "TECHIT - You have been assigned to ticket #" + ticketId;
						final String emailDetails = "You have been assigned to ticket #" + ticketId 
								+ "\n" + rd.getTicket(ticketId).toString()
								+ "\n=================================================\n"
								+ "\n" + domain + "Details?id=" + ticketId;
						
						final String requestorEmailSubject = "TECHIT - Technician has been assigned to your ticket #" + ticketId;
						final String requestorEmailDetails = "Technician has been assigned to the following ticket: "
								+ "\n" + rd.getTicket(ticketId).toString()
								+ "\n=================================================\n"
								+ "\n" + domain + "Details?id=" + ticketId;
						final String emailFrom = request.getServletContext().getAttribute("email").toString();
						
						new Thread(new Runnable(){
							public void sendEmail(){
							SendEmail se = new SendEmail();
							se.sendMultipleEmail( (Session) getServletContext().getAttribute("session"),
									(Properties) getServletContext().getAttribute("properties"),
									getServletContext().getAttribute("email").toString(),
									allEmails, emailSubject, emailDetails);
							
							se.sendEmail((Session) getServletContext().getAttribute("session"), emailFrom, requestorEmail, requestorEmailSubject, requestorEmailDetails);
							}
							public void run(){
								this.sendEmail();
							}
						}).start();
						
					}
					request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(), 
							Integer.parseInt(request.getSession().getAttribute("position").toString()), 
							Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));
					request.getSession().setAttribute("pSuccessMessage", "Technicians have been successfully assigned to the ticket!");
					response.sendRedirect("Details?id="+ticketId);
				}
			}catch(SQLException e){
				assignTechLog.error("SQL Error @ AssignTechnician (supervisor assigned).", e);
				request.setAttribute("errorMessage", "Something went wrong when getting the technicians, please try again later.");
				request.getRequestDispatcher("/WEB-INF/AssignTechnician.jsp").forward(request, response);
			}catch(Exception e){
				assignTechLog.info("Non-SQL Error @ AssignTechnician (supervisor assigned).", e);
				request.setAttribute("errorMessage", "Something went wrong when getting the technicians, please try again later.");
				request.getRequestDispatcher("/WEB-INF/AssignTechnician.jsp").forward(request, response);
			}
		}
		else{
			/*
			 * This part is for when the technician accept the ticket and assign himself/herself to the ticket.
			 */
			int id = Integer.parseInt(request.getParameter("assignTechnicianBt"));
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			Ticket ticket = rd.getTicket(id);

			Logger assignTechLog = LoggerFactory.getLogger(AssignTechnician.class);
			
			try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				String insert_tech = "insert into assignments (ticketId, technicianUser) values (?, ?)";
				
				// Adding technician into assignment table.
				try(PreparedStatement pstmt = c.prepareStatement( insert_tech )){
		            pstmt.setInt(1, id);   
		            pstmt.setString(2, request.getSession().getAttribute("user").toString());
		            pstmt.executeUpdate();
		            
		            assignTechLog.info("Technician " + request.getSession().getAttribute("user").toString() + " has been assigned to ticket #" + id);
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
					pstmt2.setString(2, request.getSession().getAttribute("user").toString());
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
					
				
				request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(), 
						Integer.parseInt(request.getSession().getAttribute("position").toString()), 
						Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));
				request.getSession().setAttribute("pSuccessMessage", "You have successfully accepted the ticket!");
				response.sendRedirect("Details?id="+id);
				
				
			}catch(SQLException e){
				assignTechLog.error("SQL Error @ AssignTechnician (technician self-assigned).", e);
				request.setAttribute("errorMessage", "Something went wrong when getting the technicians, please try again later.");
				request.getRequestDispatcher("/WEB-INF/AssignTechnician.jsp?id=" + id).forward(request, response);
			}catch(Exception e){
				assignTechLog.info("Non-SQL Error @ AssignTechnician (technician self-assigned).", e);
				request.setAttribute("errorMessage", "Something went wrong when getting the technicians, please try again later.");
				request.getRequestDispatcher("/WEB-INF/Details.jsp?id=" + id).forward(request, response);
			}
		}
	}
	
}
