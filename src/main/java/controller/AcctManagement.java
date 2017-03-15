package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import function.RetrieveData;
/**
 * The AcctManagement Servlet, which corresponds to AcctManagement.jsp, hosts a 
 * variety of functions that is only accessible by the System Administrator. Some of the 
 * functions include adding new users (special accounts under the system) / units and editing them.
 */

@WebServlet("/AcctManagement")
public class AcctManagement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if(request.getSession().getAttribute("position") != null &&
				request.getSession().getAttribute("user") != null) // Check to see if the attributes are valid.
		{
			if(Integer.parseInt(request.getSession().getAttribute("position").toString()) == 0){ // Check to see if the user is a system administrator.
				RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
				
				request.setAttribute("userList", rd.getAllUsers());
				request.setAttribute("positionList", Arrays.asList("USER", "TECHNICIAN", "SUPERVISING TECHNICIAN", "SYSTEM ADMINISTRATOR"));
				request.setAttribute("unitList", rd.getAllUnits());		
				request.getRequestDispatcher("/WEB-INF/AcctManagement.jsp").forward(request, response);
			}
			else{
				response.sendRedirect("Home"); // Not a system administrator.
			}
		}
		else{
			response.sendRedirect("Login"); // Not login.
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//String Create = request.getParameter("Create");
		String Search = request.getParameter("Search");
		if(Search == null)
		{
			String firstName = request.getParameter("firstName").replace(" ", "");
			String lastName = request.getParameter("lastName").replace(" ", "");
			String username = request.getParameter("username").replace(" ", "");
			String password = request.getParameter("password").replace(" ", ""); 
			String email = request.getParameter("email").replace(" ", "");
			String phoneNumber = request.getParameter("phoneNumber");
			String Position = request.getParameter("Position"); 
			int UnitId = Integer.parseInt(request.getParameter("units").toString());
			String department = "";
			if(request.getParameter("department") != null){
				department = request.getParameter("department").toString();
			}

			if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || Position.isEmpty() || username.isEmpty())
			{

				request.setAttribute("positionList", Arrays.asList("USER", "TECHNICIAN", "SUPERVISING TECHNICIAN", "SYSTEM ADMINISTRATOR"));
				request.setAttribute("errorMessage", "Some fields are missing!");
				request.getRequestDispatcher("/WEB-INF/AcctManagement.jsp").forward(request, response);
			}
			else if( phoneNumber.length() < 14){

				request.setAttribute("positionList", Arrays.asList("USER", "TECHNICIAN", "SUPERVISING TECHNICIAN", "SYSTEM ADMINISTRATOR"));
				request.setAttribute("errorMessage", "Incorrect phone number format!");
				request.getRequestDispatcher("/WEB-INF/AcctManagement.jsp").forward(request, response);

			}
			else{
				int position;
				Logger acctManageLog = LoggerFactory.getLogger(AcctManagement.class);
				
				if(Position.equals("SYSTEM ADMINISTRATOR")){
					position = 0;
				}
				else if(Position.equals("SUPERVISING TECHNICIAN")){
					position = 1;
				}
				else if(Position.equals("TECHNICIAN")){
					position = 2;
				}
				else{
					position = 3;
				}
				
				try(Connection c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection()){
					String insert_user = "insert into users (firstname, lastname, pass, username, "
							+ "phone, email, department, position, unit_id) values(?, ?, ?, ?, ?, ?, ?, ?,?)";
					
					try(PreparedStatement pstmt2 = c.prepareStatement(insert_user)){
						pstmt2.setString(1, firstName);
						pstmt2.setString(2, lastName);
						pstmt2.setString(3, org.apache.commons.codec.digest.DigestUtils.sha256Hex(password));
						pstmt2.setString(4, username);
						pstmt2.setString(5, phoneNumber);
						pstmt2.setString(6, email);
						pstmt2.setString(7, department);
						pstmt2.setInt(8, position);
						pstmt2.setInt(9, UnitId);
						pstmt2.executeUpdate();
						
						acctManageLog.info("System Admin. " + request.getSession().getAttribute("user").toString()
								+ " has created a new user: " + "\n"
								+ "User: " + username + "\n"
								+ "First name: " + firstName + "\n"
								+ "Last name: " + lastName + "\n"
								+ "Phone Number: " + phoneNumber + "\n"
								+ "Email: " + email + "\n"
								+ "Department: " + department + "\n"
								+ "Position: " + position + "\n"
								+ "Unit: " + UnitId);
					}
					
					RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
					request.setAttribute("userList", rd.getAllUsers());
					request.setAttribute("unitList", rd.getAllUnits());

					request.setAttribute("positionList", Arrays.asList("USER", "TECHNICIAN", "SUPERVISING TECHNICIAN", "SYSTEM ADMINISTRATOR"));
					request.setAttribute("successMessage", "Successfully created a new user!");
					request.getRequestDispatcher("/WEB-INF/AcctManagement.jsp").forward(request, response);	
					
				}catch(SQLException e){
					acctManageLog.error("SQL Error @ AcctManagement.", e);
				}catch(Exception e){
					acctManageLog.error("Non-SQL Error @ AcctManagement.", e);
				}
			}
		}

	}
}
