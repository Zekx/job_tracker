package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

@WebServlet("/EditTicket")
public class EditTicket extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(request.getParameter("id"));
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			Ticket ticket = null;
			
			ticket = rd.getTicket(id);
			if (request.getSession().getAttribute("user") == null) {
				response.sendRedirect("Login");
			}
			else if(!ticket.getUser().equals(request.getSession().getAttribute("user"))){
				request.setAttribute("errorMessage", "Invalid ticket request!");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
			else{
				System.out.println(ticket.getEmail());
				request.setAttribute("unitList", rd.getAllUnits());
				request.setAttribute("ticket", ticket);
				request.getRequestDispatcher("/WEB-INF/EditTicket.jsp").forward(request, response);
			}
		} catch (Exception e){
			e.printStackTrace();
			request.setAttribute("errorMessage", "Invalid request! Please try again later! ");
			request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String user = request.getSession().getAttribute("user").toString();
		int UnitId = Integer.parseInt(request.getSession().getAttribute("unit_id").toString());
		int position = Integer.parseInt(request.getSession().getAttribute("position").toString());
		
		String firstName = request.getParameter("firstName").replace(" ", "");
		String lastName = request.getParameter("lastName").replace(" ", "");
		String email = request.getParameter("email").replace(" ", "");
		String phoneNumber = request.getParameter("phoneNumber");
		String details = request.getParameter("details");
		String location = request.getParameter("location");
		int units = Integer.parseInt(request.getParameter("units"));
		int id = Integer.parseInt(request.getParameter("id"));
		String department = "";
		if(!request.getParameter("department").isEmpty()){
			department = request.getParameter("department");
		}
		
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
		// java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || details.isEmpty()
				|| location.isEmpty()) {
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("details", details);
			request.setAttribute("location", location);
			request.setAttribute("errorMessage", "Some fields are missing!");
			
			request.setAttribute("unitList", rd.getAllUnits());
			
			request.getRequestDispatcher("/WEB-INF/EditTicket.jsp").forward(request, response);
		} else if (phoneNumber.length() < 14) {
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("errorMessage", "Incorrect phone number format!");
			
			request.setAttribute("unitList", rd.getAllUnits());
			
			request.getRequestDispatcher("/WEB-INF/EditTicket.jsp").forward(request, response);

		} else {
			Logger editTicketLog = LoggerFactory.getLogger(EditTicket.class);
			try (Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				
				String createTicket = "update tickets set userFirstName = ? , userLastName =? , phone =? , email = ?, details = ?, lastUpdated = NOW() ,"
						+ "ticketLocation = ?, unitId =?, department = ? where id =?";
				try(PreparedStatement pstmt = c.prepareStatement(createTicket)){
					pstmt.setString(1, firstName);
					pstmt.setString(2, lastName);
					pstmt.setString(3, phoneNumber);
					pstmt.setString(4, email);
					pstmt.setString(5, details);
					pstmt.setString(6, location);
					pstmt.setInt(7, units);
					pstmt.setString(8, department);
					pstmt.setInt(9, id);
					pstmt.executeUpdate();
				}
				
				editTicketLog.info("User " + request.getSession().getAttribute("user").toString() + " has edited ticket #" + id + ".");

				request.getSession().setAttribute("tickets",
						rd.getUserTicket(request.getSession().getAttribute("user").toString(),
								Integer.parseInt(request.getSession().getAttribute("position").toString()),
								Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));
				
				java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTime = sdf.format(dt);
				
				String insertUpdate = "insert into updates (ticketId, modifier, updateDetails, modifiedDate) values (?, ?, ?, ?)";
				try(PreparedStatement pstmt2 = c.prepareStatement(insertUpdate)){
					pstmt2.setInt(1, id);
					pstmt2.setString(2, request.getSession().getAttribute("user").toString());
					pstmt2.setString(3, "User has edited the ticket's details.");
					pstmt2.setString(4, currentTime);
					pstmt2.executeUpdate();
				}
				
				//Sending emails
				
				Ticket ticket = rd.getTicket(id);
				String domain = request.getServletContext().getAttribute("domain").toString();
				
				List<String> emails = rd.getSupervisorEmails(ticket.getUnitId());
				final List<String> allEmails = emails;
				final String emailSubject = "TECHIT - Ticket #" + id + " has been edited by the requestor.";
				final String emailDetails = "The following ticket has been edited by the requestor: " + "\n" + ticket.toString() 
				+ "\n" + domain + "Details?id=" + id;
				
				new Thread(new Runnable(){
					public void sendEmail(){
					SendEmail se = new SendEmail();
					se.sendMultipleEmail( (Session) getServletContext().getAttribute("session"),
							getServletContext().getAttribute("email").toString(),
							allEmails, emailSubject, emailDetails);
					}
					public void run(){
						this.sendEmail();
					}
				}).start();
				
				request.getSession().setAttribute("tickets", rd.getUserTicket(user, position, UnitId));

				request.getSession().setAttribute("pSuccessMessage", "Ticket has been successfully edited!");
				response.sendRedirect("Details?id="+id);
				
			} catch (SQLException e) {
				editTicketLog.error("SQL Error @ EditTicket.", e);
				request.setAttribute("errorMessage", "Something went wrong during the ticket editing, please try again later!");
				request.getServletContext().getRequestDispatcher("/WEB-INF/Details.jsp?id=" + id);
			} catch (Exception e){
				editTicketLog.error("Non-SQL Error @ EditTicket.", e);
				request.setAttribute("errorMessage", "Something went wrong during the ticket editing, please try again later!");
				request.getServletContext().getRequestDispatcher("/WEB-INF/Details.jsp?id=" + id);
			}
		}
	}

}