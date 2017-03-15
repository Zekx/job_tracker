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

import function.SendEmail;

import function.RetrieveData;

@WebServlet("/CreateTicket")
public class CreateTicket extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("user") == null) {
			response.sendRedirect("Login");
		}
		else{
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			request.setAttribute("unitList", rd.getAllUnits());
			request.getRequestDispatcher("/WEB-INF/CreateTicket.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
		String user = request.getSession().getAttribute("user").toString();
		int UnitId = Integer.parseInt(request.getSession().getAttribute("unit_id").toString());
		int position = Integer.parseInt(request.getSession().getAttribute("position").toString());
		
		String firstName = request.getParameter("firstName").replace(" ", "");
		String lastName = request.getParameter("lastName").replace(" ", "");
		String email = request.getParameter("email").replace(" ", "");
		String phoneNumber = request.getParameter("phoneNumber");
		String subject = request.getParameter("subject");
		String details = request.getParameter("details");
		String location = request.getParameter("location");
		String department = "";
		if(!request.getParameter("department").isEmpty())
		{
			department = request.getParameter("department").toString();
		}
		
		int units = Integer.parseInt(request.getParameter("units"));
		// java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || details.isEmpty() ||
				subject.isEmpty() || location.isEmpty()) {
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("subject", subject);
			request.setAttribute("details", details);
			request.setAttribute("location", location);
			request.setAttribute("errorMessage", "Some fields are missing!");
			request.getRequestDispatcher("/WEB-INF/CreateTicket.jsp").forward(request, response);
		} else if (phoneNumber.length() < 14) {
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("subject", subject);
			request.setAttribute("details", details);
			request.setAttribute("errorMessage", "Incorrect phone number format!");
			request.getRequestDispatcher("/WEB-INF/CreateTicket.jsp").forward(request, response);

		} else {
			Logger createTicketLog = LoggerFactory.getLogger(CreateTicket.class);
			try (Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){

				// Get current time
				java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTime = sdf.format(dt);
				// Query
				String createTicket = "insert into tickets (username,userFirstName,userLastName, phone, email, department, unitId, subject, details,"
						+ "startDate,lastUpdated, ticketLocation) values (?,?,?,?,?,?,?,?,?,?,?,?)";
				try(PreparedStatement pstmt = c.prepareStatement(createTicket)){
					pstmt.setString(1, request.getSession().getAttribute("user").toString());
					pstmt.setString(2, firstName);
					pstmt.setString(3, lastName);
					pstmt.setString(4, phoneNumber);
					pstmt.setString(5, email);
					pstmt.setString(6, department);
					pstmt.setInt(7, units);
					pstmt.setString(8, subject);
					pstmt.setString(9, details);
					pstmt.setString(10, currentTime);
					pstmt.setString(11, currentTime);
					pstmt.setString(12, location);
					pstmt.executeUpdate();
				}
				
				request.getSession().setAttribute("tickets", rd.getUserTicket(user, position, UnitId));
				
				if (request.getSession().getAttribute("errorMessage") != null) {
					request.removeAttribute("errorMessage");
				}
				
				String domain = request.getServletContext().getAttribute("domain").toString();

				String link = "";
				
				int id = rd.getTicketIdFromUsernameUsingTime(request.getSession().getAttribute("user").toString());
				
				if(id != 0){
					link = domain + "Details?id=" + id;
				}
				else{
					link = domain + "Home";
				}
				
				createTicketLog.info("User " + request.getSession().getAttribute("user").toString()
						+ " has created ticket #" + id);
				
				final String emailDetails = "Ticket Creator: " + firstName + " " + lastName + "\n "
						+ "Phone number: " + phoneNumber + "\n"
						+ "Email: " + email + "\n"
						+ "Location of Problem: " + location + "\n"
						+ "Subject: " + subject + "\n"
						+ "Details: " + details + "\n"
						+ link;
				
				final List<String> emails = rd.getSupervisorEmails(units);
				final String emailSubject = "TECHIT - Ticket #" + id + " has been created.";
				
				if(emails.size() > 0){
					new Thread(new Runnable(){
						public void sendEmail(){
						SendEmail se = new SendEmail();
						se.sendMultipleEmail( (Session) getServletContext().getAttribute("session"),
								getServletContext().getAttribute("email").toString(),
								emails, emailSubject, emailDetails);
						}
						public void run(){
							this.sendEmail();
						}
					}).start();
				}
				if(id != 0){
					request.setAttribute("successMessage", "Successfully created a new ticket!");
					response.sendRedirect("Details?id="+id);
				}
				else{
					response.sendRedirect("Home");
				}

			} catch (SQLException e) {
				createTicketLog.error("SQL Error @ CreateTicket.", e);
				request.setAttribute("errorMessage", "Something went wrong during the creation of the ticket, please try again later.");
				request.getServletContext().getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			} catch (Exception e){
				createTicketLog.error("Non-SQL Error @ CreateTicket.", e);
				request.setAttribute("errorMessage", "Something went wrong during the creation of the ticket, please try again later.");
				request.getServletContext().getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
	}
}
