package android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import external.ActiveDirectory;
import function.LoginFunction;
import function.RetrieveData;

/**
 * Servlet implementation class AndroidLogin
 */
@WebServlet("/AndroidLogin")
public class AndroidLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String HELLO_STRING = "Hello: ";
	private static final String JSON_KEY = "output";

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
		System.out.println("Performing Android Login Authenication...");
		
		/*String text = "";
        StringBuffer sb = new StringBuffer();
        try {
             BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
             String line = reader.readLine();
             while (line != null) {
                  sb.append(line);
                  line = reader.readLine();
             }
             if (sb.length() != 0) {
                  text = sb.toString();
             }
        } catch (Exception e) {
             System.out.println(e);
        } */

        response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        System.out.println("Attaching values to the response.");
        
        JsonObject jsonObj = new JsonObject();
        
        String user = request.getParameter("username");
        String password = request.getParameter("password");
        
		LoginFunction lf = new LoginFunction();
		RetrieveData rd = null;
    	int check = 4;
    	try{
			if (Boolean.valueOf(request.getServletContext().getAttribute("onServer").toString())){
				DataSource ds = (DataSource)request.getServletContext().getAttribute("dbSource");
				rd = new RetrieveData(ds);
				check = lf.checkSystemAccountDBSource(ds, user, password);
			}
			else{
				String dbURL = request.getServletContext().getAttribute("dbURL").toString();
				String dbUser = request.getServletContext().getAttribute("dbUser").toString();
				String dbPass = request.getServletContext().getAttribute("dbPass").toString();
				rd = new RetrieveData(dbURL, dbUser, dbPass);
				
				check = lf.checkSystemAccount(dbURL, dbUser, dbPass, user, password);
				System.out.println("Login location");
			}
    	} catch (SQLException sqle) {
    		jsonObj.addProperty("error", "Something went wrong, please try again later!");
			System.out.print(sqle.toString());
			return;
		}
    	/*
    	 * 		0 -- User doesn't exist in the database
		 * 		1 -- User exist but since password is empty, the account is not
		 * 				 a special account
		 * 		2 -- User exists but incorrect password
		 * 		3 -- User exists, password is correct, user information is saved
		 * 				into the LoginFunction class variable
    	 */
    	if(check == 3){
    		jsonObj.addProperty("user", user);
    		jsonObj.addProperty("firstname", lf.getSystemAccount().getFirstName());
    		jsonObj.addProperty("lastname", lf.getSystemAccount().getLastName());
    		jsonObj.addProperty("phoneNumber", lf.getSystemAccount().getPhoneNumber());
    		jsonObj.addProperty("email", lf.getSystemAccount().getEmail());
    		jsonObj.addProperty("unit_id", lf.getSystemAccount().getUnitId());
    		jsonObj.addProperty("position", lf.getSystemAccount().getStatus());
			
			try {
				String ticketInfoInJSON = new Gson().toJson(rd.getUserTicket(user, lf.getSystemAccount().getStatus(), lf.getSystemAccount().getUnitId()));
				jsonObj.addProperty("tickets", ticketInfoInJSON);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//Work on this part for the login! Create new layout for first time logins.
			if( lf.getSystemAccount().getLastName().isEmpty() || lf.getSystemAccount().getPhoneNumber().isEmpty() || 
					lf.getSystemAccount().getEmail().isEmpty() ){
				jsonObj.addProperty("firstLogin?", true);
			}
			else{
				jsonObj.addProperty("firstLogin?", false);
			}
    	}
    	else{
    		String domain = "ad.calstatela.edu";
	        String choice = "username";
	
	        ActiveDirectory activeDirectory = new ActiveDirectory();
	        
	        try{
	            activeDirectory.connect( domain, user, password );
	            NamingEnumeration<SearchResult> result = activeDirectory
	                    .searchUser( user, choice, null );
	            if(result.hasMore()){
	            	if(check == 0)
	            	{
	            		SearchResult rs = (SearchResult) result.next();
	                    Attributes attrs = rs.getAttributes();
	                    
	                    String temp = attrs.get( "givenName" ).toString();
	                    String firstName = temp
	                        .substring( temp.indexOf( ":" ) + 1 );
	                    
	                    temp = attrs.get( "mail" ).toString();
	                    String emailAD = temp
	                        .substring( temp.indexOf( ":" ) + 1 );   
	                    
	                    jsonObj.addProperty("user", user);
	            		jsonObj.addProperty("firstname", firstName);
	            		jsonObj.addProperty("lastname", "");
	            		jsonObj.addProperty("phoneNumber", "");
	            		jsonObj.addProperty("email", emailAD);
	            		jsonObj.addProperty("unit_id", 0);
	            		jsonObj.addProperty("position", 3);
	        			
	        			result.close();
		            	activeDirectory.closeLdapConnection();
		            	
		            	//request.getRequestDispatcher("/WEB-INF/FirstLoginUpdate.jsp").forward(request, response);
		            	jsonObj.addProperty("firstLogin?", true);
	            	}
	            	else{
	            		// This should be when check == 1 and NOT 2 or 3.
	            		// If it's 2 or 3, then something is VERY WRONG.
	            		
	            		jsonObj.addProperty("user", user);
	            		jsonObj.addProperty("firstname", lf.getSystemAccount().getFirstName());
	            		jsonObj.addProperty("lastname", lf.getSystemAccount().getLastName());
	            		jsonObj.addProperty("phoneNumber", lf.getSystemAccount().getPhoneNumber());
	            		jsonObj.addProperty("email", lf.getSystemAccount().getEmail());
	            		jsonObj.addProperty("unit_id", lf.getSystemAccount().getUnitId());
	            		jsonObj.addProperty("position", lf.getSystemAccount().getStatus());
	            		jsonObj.addProperty("department", lf.getSystemAccount().getDepartment());
	        			
	        			result.close();
		            	activeDirectory.closeLdapConnection();
	        			
	        			try {
	        				String ticketInfoInJSON = new Gson().toJson(rd.getUserTicket(user, lf.getSystemAccount().getStatus(), lf.getSystemAccount().getUnitId()));
	        				jsonObj.addProperty("tickets", ticketInfoInJSON);
	        			} catch (SQLException e) {
	        				e.printStackTrace();
	        			}
	        			
	        			if( lf.getSystemAccount().getLastName().isEmpty() || lf.getSystemAccount().getPhoneNumber().isEmpty() || 
	        					lf.getSystemAccount().getEmail().isEmpty() ){
	        				jsonObj.addProperty("firstLogin?", true);
	        			}
	        			else{
	        				jsonObj.addProperty("firstLogin?", false);
	        			}
	            	}
	            	}else{
	            		// This is when check = 0, 1, or 2
	        			result.close();
		            	activeDirectory.closeLdapConnection();
	            		
		    			jsonObj.addProperty("error", "Username or password was invalid!");
	            	}
	            }
	            catch(Exception e){
	        	/*
	        	 * 	This is for when AD logs in fails.
	        	 * 	This section include both situation when AD is down or login is incorrect.
	        	 * 
	        	 */
	        	activeDirectory.closeLdapConnection();
	            
	    		if(check == 0 || check == 1)	
	    		{
	    			if(e instanceof NamingException)
	    			{	
	    				// The if clause applies when AD return an error of invalid password or username.
		    			// Check = 0 means incorrect AD login and user does not belong in this database.
		    			// Check = 1 means the user belongs to the database but incorrect password.
	    				
						jsonObj.addProperty("error", "Invalid username or password, please try again!");
	    			}
	    			else{
	    				jsonObj.addProperty("error", "Invalid username or password, please try again!");
	    			}
	    		}
	    		else
	    		{
	    			jsonObj.addProperty("error", "Invalid username or password, please try again!");
	    		}

    		}
			
    	}
    	
    	response.getWriter().println(jsonObj);
	}

}
