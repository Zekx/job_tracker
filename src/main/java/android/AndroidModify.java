package android;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.Update;
import function.RetrieveData;
import function.SendEmail;
import function.StringFilter;
import model.Ticket;


@WebServlet("/AndroidModify")
public class AndroidModify extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AndroidModify() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		String currentProgress = request.getParameter("NewProgress");
		String modifier = request.getParameter("Modifier");
		boolean change = Boolean.parseBoolean(request.getParameter("Change"));
		int ticketId = Integer.parseInt(request.getParameter("TicketID"));
		String unitID = request.getParameter("UnitID");
		String username = request.getParameter("Username");
		System.out.println(currentProgress + modifier + change + ticketId + unitID + username);
		
		
		
		
		JsonObject jsonObj = new JsonObject();
		
		if(request.getParameter("TicketID") == null || request.getParameter("Modifier") == null || request.getParameter("UpdateDetails") == null ||request.getParameter("NewProgress") == null ||request.getParameter("Change") == null || request.getParameter("UnitID")== null || request.getParameter("Username") == null ){
			jsonObj.addProperty("error", "The parameter fields are missing!");
			System.out.println("Error! Inputs are incorrect!");
			response.getWriter().println(jsonObj);
			
			return;
		}
		else
		{
			System.out.println("Inputs are correct!");
		}
		
		
		
		response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        System.out.println("Attaching values to the response.");
        

		
		
		
		System.out.println("");
		
		
		
			

		
		StringFilter sf = new StringFilter();
		String updateMessage = sf.filterNull(request.getParameter("UpdateDetails"));
		
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		Logger updateTicketLog = LoggerFactory.getLogger(Update.class);
		if(!updateMessage.isEmpty())
		{
			//int ticketId = Integer.parseInt(request.getParameter("ticket_id").toString());
			
			// Get current time
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			
			try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				
				if(!change){
					int status = 0;
					
					switch(currentProgress){
					case "INPROGRESS":
					default:
						status = 1;
						break;
					case "ONHOLD":
						status = 2;
						break;
					case "COMPLETED":
						status = 3;
						break;
					case "CLOSED":
						status = 4;
						break;
					}
					
					String update_ticket = "update tickets set Progress = ?, lastUpdated = ? where id = ?";
					try(PreparedStatement ptsmt = c.prepareStatement(update_ticket)){
						ptsmt.setInt(1, status);
						ptsmt.setString(2, currentTime);
						ptsmt.setInt(3, ticketId);
						ptsmt.executeUpdate();
					}	
				}
				else{
					String update_ticket = "update tickets set lastUpdated = ? where id = ?";
					try(PreparedStatement ptsmt = c.prepareStatement(update_ticket)){
						ptsmt.setString(1, currentTime);
						ptsmt.setInt(2, ticketId);
						ptsmt.executeUpdate();
					}
				}
				
				String insert_update = "insert into updates (ticketId, modifier, updateDetails, modifiedDate) values (?, ?, ?, ?) ";
				try(PreparedStatement pstmt2 = c.prepareStatement(insert_update)){
					pstmt2.setInt(1, ticketId);
					pstmt2.setString(2, modifier);
					pstmt2.setString(3, updateMessage);
					pstmt2.setString(4, currentTime);
					pstmt2.executeUpdate();
				}
				
				updateTicketLog.info("User " + modifier + " has updated"
						+ " ticket #" + ticketId + ".");
				
				Ticket ticket = rd.getTicket(ticketId);

				String domain = request.getServletContext().getAttribute("domain").toString();
				if(currentProgress.equals("COMPLETED")){				
					List<String> emails = rd.getSupervisorEmails(ticket.getUnitId());
					String requestorEmail = rd.getRequestorEmailFromTicket(ticketId);
					emails.add(requestorEmail);
					
					final List<String> allEmails = emails;
					final String emailSubject = "TECHIT - Ticket #" + ticketId + " has been completed.";
					final String emailDetails = "The following ticket has been completed: " 
							+ "\n" + ticket.toString() + "\n" 
							+ domain + "Details?id=" + ticketId;
					
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
				}
				else if(currentProgress.equals("CLOSED")){
					List<String> emails = rd.getSupervisorEmails(ticket.getUnitId());
					String requestorEmail = rd.getRequestorEmailFromTicket(ticketId);
					emails.add(requestorEmail);
					
					
					final List<String> allEmails = emails;
					final String emailSubject = "TECHIT - Ticket #" + ticketId + " has been closed.";
					final String emailDetails = "Update Message: " + updateMessage 
							+ "\n==========================================\n"
							+ "\nThe following ticket has been closed: " 
							+ "\n" + ticket.toString()
							+ "\n==========================================\n"
							+ "\n" + domain + "Details?id=" + ticketId;
					
					new Thread(new Runnable(){
						public void sendEmail(String emailDetails){
						SendEmail se = new SendEmail();
						se.sendMultipleEmail( (Session) getServletContext().getAttribute("session"),
								getServletContext().getAttribute("email").toString(),
								allEmails, emailSubject, emailDetails);
						}
						public void run(){
							this.sendEmail(emailDetails);
						}
					}).start();
				}
				else if(!change){
					final String requestorEmail = rd.getRequestorEmailFromTicket(ticketId);
					final String emailSubject = "TECHIT - Ticket #" + ticketId + " has been updated.";
					final String emailDetails = "\nUpdate Message: " + updateMessage 
							+ "\n==========================================\n"
							+ "\nThe following ticket has been updated: " 
							+ "\n" + ticket.toString()
							+ "\n==========================================\n"
							+ "\n" + domain + "Details?id=" + ticketId;
					
					new Thread(new Runnable(){
						public void sendEmail(String emailDetails){
						SendEmail se = new SendEmail();
						se.sendEmail( (Session) getServletContext().getAttribute("session"),
								getServletContext().getAttribute("email").toString(),
								requestorEmail, emailSubject, emailDetails);
						}
						public void run(){
							this.sendEmail(emailDetails);
						}
					}).start();	
				}
				
				String ticketUpdateJSON = new Gson().toJson(rd.getTicketUpdates(ticketId));
				System.out.println(ticketUpdateJSON);
				jsonObj.addProperty("updates", ticketUpdateJSON);
				response.getWriter().println(jsonObj);
				
				
				
				//request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(), 
				//		Integer.parseInt(request.getSession().getAttribute("position").toString()), 
				//		Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));
				
				//request.getSession().setAttribute("pSuccessMessage", "Successfully updated the ticket!");
				//response.sendRedirect("Details?id="+ticketId);				
			}catch (SQLException e){
				updateTicketLog.error("SQL Error @ Update (update ticket servlet).", e);

				//request.setAttribute("errorMessage", "Something went wrong when updating, please try again later!");
			//	request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
				
			}catch(Exception e){
				updateTicketLog.error("Non-SQL Error @ Update (update ticket servlet).", e);
				
				//request.setAttribute("errorMessage", "Something went wrong when updating, please try again later!");
				//request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
		
	}


}
