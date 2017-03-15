package android;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.google.gson.JsonObject;

/**
 * This servlet is the controller for FirstLoginUpdate.jsp
 * This code is meant to add missing information of the Active Directory system
 * into our local database when the user login for the first time.
 */
@WebServlet(urlPatterns="/FirstAndroidLoginUpdate")
public class AndroidFirstLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String firstName = "";
		String lastName = "";
		String email = "";
		String phoneNumber = "";
		String department = "";
		
		if(request.getParameter("firstName") != null){
			firstName = request.getParameter("firstName").replace(" ", "");
		}
		if(request.getParameter("lastName") != null){
			lastName = request.getParameter("lastName").replace(" ", "");
		}
		if(request.getParameter("email")!= null){
			email = request.getParameter("email").replace(" ", "");
		}
		if(request.getParameter("phoneNumber")!= null){
			phoneNumber = String.valueOf(request.getParameter("phoneNumber")).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
		}
		if(request.getParameter("department")!= null){
			department = request.getParameter("department");
		}
		
		JsonObject jsonObj = new JsonObject();
		
		response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
		
		if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty())
		{
			
    		jsonObj.addProperty("firstname", firstName);
    		jsonObj.addProperty("lastname", lastName);
    		jsonObj.addProperty("phoneNumber", phoneNumber);
    		jsonObj.addProperty("email", email);
    		jsonObj.addProperty("error", "Some fields are missing!");
    		jsonObj.addProperty("Valid", false);
    		jsonObj.addProperty("department", department);
			
    		response.getWriter().println(jsonObj);
		}
		else if( phoneNumber.length() < 14){
			jsonObj.addProperty("firstname", firstName);
    		jsonObj.addProperty("lastname", lastName);
    		jsonObj.addProperty("phoneNumber", phoneNumber);
    		jsonObj.addProperty("email", email);
    		jsonObj.addProperty("department", department);
    		jsonObj.addProperty("error", "Some fields are missing!");
    		jsonObj.addProperty("Valid", false);
			
			response.getWriter().println(jsonObj);
		}
		else{
			Connection c = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				if(Boolean.valueOf(request.getServletContext().getAttribute("onServer").toString()))
				{
					c = ((DataSource)request.getServletContext().getAttribute("dbSource")).getConnection();
				}
				else{
					String url = "jdbc:mysql://cs3.calstatela.edu/cs4961stu01";
					String db_user = "cs4961stu01";
					String db_pass = ".XCGG1Bc";

					c = DriverManager.getConnection(url, db_user, db_pass);
				}
				String search_user = "select * from users where username = ?";
	            pstmt = c.prepareStatement( search_user );
	            pstmt.setString( 1, request.getParameter("username").toString() );
	            
	            rs = pstmt.executeQuery();
	            
	            if(rs.next())
	            {
	            	String update_user = "update users set firstname = ?, lastname = ?, email = ?, phone = ?, department = ? where username = ?";
	            	PreparedStatement pstmt2 = c.prepareStatement(update_user);
	            	pstmt2.setString(1, firstName);
	            	pstmt2.setString(2, lastName);
	            	pstmt2.setString(3, email);
	            	pstmt2.setString(4, phoneNumber);
	            	pstmt2.setString(5, department);
	            	pstmt2.setString(6, request.getParameter("username"));
		            pstmt2.executeUpdate();

	            }
	            else
	            {
	            	String insert_user = "insert into users (firstname, lastname, pass, username, phone, department, email, position) values(?, ?, ?, ?, ?, ?, ?, ?)";
	            	PreparedStatement pstmt2 = c.prepareStatement(insert_user);
	            	pstmt2.setString(1, firstName);
	            	pstmt2.setString(2, lastName);
	            	pstmt2.setString(3, "");
	            	pstmt2.setString(4, request.getParameter("username"));
	            	pstmt2.setString(5, phoneNumber);
	            	pstmt2.setString(6, department);
	            	pstmt2.setString(7, email);
	            	pstmt2.setInt(8, 3);
	            	pstmt2.execute();

	            }
	            pstmt.close();
	            rs.close();
	            c.close();
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				DbUtils.closeQuietly(pstmt);
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(c);
			}
			if(request.getSession().getAttribute("errorMessage")!= null){
				request.removeAttribute("errorMessage");
			}
			jsonObj.addProperty("firstname", firstName);
    		jsonObj.addProperty("lastname", lastName);
    		jsonObj.addProperty("phoneNumber", phoneNumber);
    		jsonObj.addProperty("email", email);
    		jsonObj.addProperty("department", department);
    		jsonObj.addProperty("Valid", true);

    		response.getWriter().println(jsonObj);
		}
	}

}
