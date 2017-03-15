package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet("/CreateUnit")
public class CreateUnit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Does nothing since there is no create Unit page
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String unitName = request.getParameter("unitName").replace(" ", "");
		String phone = request.getParameter("phoneNumber");
		String location =  request.getParameter("location").replace(" ", "");
		String email = request.getParameter("email").replace(" ", "");
		String description = request.getParameter("description").replace(" ", "");
		if(unitName.isEmpty() || description.isEmpty())
		{	
			request.setAttribute("errorMessage", "Some fields are missing!");
			request.getRequestDispatcher("/WEB-INF/AcctManagement.jsp").forward(request, response);
		}
		else
		{
			Logger createUnitLog = LoggerFactory.getLogger(CreateUnit.class);
			try (Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				
				String createTicket = "insert into units (unitName,phone,location,email, description) values (?,?,?,?,?)";
				try(PreparedStatement pstmt = c.prepareStatement(createTicket)){
					pstmt.setString(1, unitName);
					pstmt.setString(2, phone);
					pstmt.setString(3, location);
					pstmt.setString(4, email);
					pstmt.setString(5, description);			
					pstmt.executeUpdate();
				}
				
				createUnitLog.info("System Admin. " + request.getSession().getAttribute("user").toString()
						+ " has created a new unit: " + unitName + ".");
			} catch (SQLException e) {
				createUnitLog.error("SQL Error @ CreateUnit.", e);
			} catch (Exception e){
				createUnitLog.error("Non-SQL Error @ CreateUnit.", e);
			}
			
			response.sendRedirect("Home");
		}
	}

}
