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
import function.StringFilter;
import model.Ticket;

@WebServlet("/Cancel")
public class Cancel extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Does nothing, no page
		if(request.getSession().getAttribute("user") == null){
			response.sendRedirect("Login");
		}
		else{
			response.sendRedirect("Home");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int id = Integer.parseInt(request.getParameter("cancelBt"));
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
		StringFilter sf = new StringFilter();
		
		String rejected = sf.filterNull(request.getParameter("rejectInput"));

		String insertQuery = "insert into updates (ticketId, modifier, updateDetails, modifiedDate) values (?, ?, ?, ?) ";
		Logger cancelLog = LoggerFactory.getLogger(Cancel.class);
		
		try (Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
			String cancel = "update tickets set Progress = ? where id = ?";
            
			try(PreparedStatement pstmt = c.prepareStatement( cancel )){
	            pstmt.setInt( 1, 4 );
	            pstmt.setInt( 2, id );
	            pstmt.executeUpdate();
			}
            
            
			// Get current time
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			
            // Insert into new update

        	Ticket ticket = rd.getTicket(id);
        	
        	String domain = request.getServletContext().getAttribute("domain").toString();
        	
            if(rejected.isEmpty()){
            	// This means that the user canceled the ticket
            	try(PreparedStatement insertUpdate = c.prepareStatement(insertQuery)){
	            	insertUpdate.setInt(1, id);
	            	insertUpdate.setString(2, request.getSession().getAttribute("user").toString());
	            	insertUpdate.setString(3, "The ticket has been canceled by the requestor.");
	            	insertUpdate.setString(4, currentTime);
	            	insertUpdate.executeUpdate();
            	}
            	
            	cancelLog.info("Ticket #" + id + " has ben canceled by requestor.");
            	
            	final String subjectDetails = "TECHIT - Ticket #" + id + " was canceled by the requestor.";
            	final String emailDetails = "The following ticket was canceled by the requestor. \n" 
            			+ ticket.toString() + "\n" 
            			+ "\n" + domain + "Details?id=" + id;
            	final List<String> requestorEmail = rd.getSupervisorEmails(ticket.getUnitId());
            	if(requestorEmail.size() > 0){
	            	new Thread(new Runnable(){
	    				public void sendEmail(){
	    				SendEmail se = new SendEmail();
	    				se.sendMultipleEmail( (Session) getServletContext().getAttribute("session"),
	    						getServletContext().getAttribute("email").toString(),
	    						requestorEmail, subjectDetails, emailDetails);
	    				}
	    				public void run(){
	    					this.sendEmail();
	    				}
	    			}).start();
            	}
            }
            else{
            	// This means the supervisor declined/rejected the ticket
            	try(PreparedStatement insertUpdate = c.prepareStatement(insertQuery)){
	            	insertUpdate.setInt(1, id);
	            	insertUpdate.setString(2, request.getSession().getAttribute("user").toString());
	            	insertUpdate.setString(3, "The ticket has been closed by the supervisor. Reason: " + rejected);
	            	insertUpdate.setString(4, currentTime);
	            	insertUpdate.executeUpdate();
            	}
            	
            	cancelLog.info("Ticket #" + id + " has been closed by a supervisor. \nReason: " + rejected);
            	
            	final String subjectDetails = "TECHIT - Your ticket #" + id + " was declined by a supervisor.";
            	final String emailDetails = "Your following ticket was declined by a supervisor. \n" 
            			+ "========================================\n"
            			+ ticket.toString() + "\n"
            			+ "========================================\n"
            			+"Reason: " + rejected + "\n" + domain + "Details?id=" + id;
            	final String requestorEmail = sf.filterNull(rd.getRequestorEmailFromTicket(id));
            	if(!requestorEmail.isEmpty()){
	            	new Thread(new Runnable(){
	    				public void sendEmail(){
	    				SendEmail se = new SendEmail();
	    				se.sendEmail( (Session) getServletContext().getAttribute("session"),
	    						getServletContext().getAttribute("email").toString(),
	    						requestorEmail, subjectDetails, emailDetails);
	    				}
	    				public void run(){
	    					this.sendEmail();
	    				}
	    			}).start();
            	}
            }
			request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(), 
					Integer.parseInt(request.getSession().getAttribute("position").toString()), 
					Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));
			
			request.getSession().setAttribute("pSuccessMessage", "Ticket has closed successfully!");
			response.sendRedirect("Details?id="+id);
			
		}catch(SQLException e){
			cancelLog.error("SQL Error @ Cancel.", e);
			request.setAttribute("errorMessage", "Something went wrong during cancelation, please try again later!");
			request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			
		}catch(Exception e){
			cancelLog.error("Non-SQL Error @ Cancel.", e);
			request.setAttribute("errorMessage", "Something went wrong during cancelation, please try again later!");
			request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
		}
	}
}
