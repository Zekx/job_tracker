package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is the controller for FirstLoginUpdate.jsp
 * This code is meant to add missing information of the Active Directory system
 * into our local database when the user login for the first time.
 */
@WebServlet(urlPatterns="/FirstLoginUpdate")
public class FirstLoginUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("user") == null) {
			request.getRequestDispatcher("/Login").forward(request, response);
		} else {
			if(request.getAttribute("referred") != null){
				request.setAttribute("referred", request.getAttribute("referred").toString());
			}
			request.getRequestDispatcher("/WEB-INF/FirstLoginUpdate.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String firstName = request.getParameter("firstName").replace(" ", "");
		String lastName = request.getParameter("lastName").replace(" ", "");
		String email = request.getParameter("email").replace(" ", "");
		String phoneNumber = request.getParameter("phoneNumber");
		String department = "";
		if(!request.getParameter("department").isEmpty()){
			department = request.getParameter("department");
		}
		
		if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty())
		{
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("errorMessage", "Some fields are missing!");
			request.setAttribute("department", department);
			request.getRequestDispatcher("/WEB-INF/FirstLoginUpdate.jsp").forward(request, response);
		}
		else if( phoneNumber.length() < 14){
			request.setAttribute("lastName", lastName);
			request.setAttribute("firstName", firstName);
			request.setAttribute("email", email);
			request.setAttribute("phoneNumber", phoneNumber);
			request.setAttribute("department", department);
			request.setAttribute("errorMessage", "Incorrect phone number format!");

			request.getRequestDispatcher("/WEB-INF/FirstLoginUpdate.jsp").forward(request, response);
			
		}
		else{
			Logger firstLoginLog = LoggerFactory.getLogger(FirstLoginUpdate.class);
			
			try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
				
				String search_user = "select * from users where username = ?";
	            try(PreparedStatement pstmt = c.prepareStatement( search_user )){
	            	pstmt.setString( 1, request.getSession().getAttribute("user").toString() );
	            
		            try(ResultSet rs = pstmt.executeQuery()){
		            
			            if(rs.next())
			            {
			            	String update_user = "update users set firstname = ?, lastname = ?, email = ?, phone = ? where username = ?";
			            	try(PreparedStatement pstmt2 = c.prepareStatement(update_user)){
				            	pstmt2.setString(1, firstName);
				            	pstmt2.setString(2, lastName);
				            	pstmt2.setString(3, email);
				            	pstmt2.setString(4, phoneNumber);
				            	pstmt2.setString(5, request.getSession().getAttribute("user").toString());
					            pstmt2.executeUpdate();
			            	}
			            }
			            else
			            {
			            	String insert_user = "insert into users (firstname, lastname, pass, username, phone, email, position) values(?, ?, ?, ?, ?, ?, ?)";
			            	try(PreparedStatement pstmt2 = c.prepareStatement(insert_user)){
				            	pstmt2.setString(1, firstName);
				            	pstmt2.setString(2, lastName);
				            	pstmt2.setString(3, "");
				            	pstmt2.setString(4, request.getSession().getAttribute("user").toString());
				            	pstmt2.setString(5, phoneNumber);
				            	pstmt2.setString(6, email);
				            	pstmt2.setInt(7, 3);
				            	pstmt2.execute();
			            	}
			            }
		            }
	            }
	            
	            firstLoginLog.info("User " + request.getSession().getAttribute("user").toString() + " has updated their information "
	            		+ "in FirstLoginUpdate.");
	            
				if(request.getSession().getAttribute("errorMessage")!= null){
					request.removeAttribute("errorMessage");
				}
				request.getSession().setAttribute("lastname", lastName);
				request.getSession().setAttribute("firstname", firstName);
				request.getSession().setAttribute("email", email);
				request.getSession().setAttribute("phoneNumber", phoneNumber);
				request.getSession().setAttribute("department", department);
				
				
				if(request.getSession().getAttribute("dReferred") != null){
					int id = Integer.parseInt(request.getSession().getAttribute("dReferred").toString());
					request.getSession().removeAttribute("dReferred");
					response.sendRedirect("Details?id="+id);
				}
				else{
					response.sendRedirect("Home");
				}
			} catch (SQLException e) {
				firstLoginLog.error("SQL Error @ FirstLoginUpdate.", e);
				response.sendRedirect("Home");
			} catch (Exception e) {
				firstLoginLog.error("Non-SQL Error @ FirstLoginUpdate.", e);
				response.sendRedirect("Home");
			}
		}
	}

}
