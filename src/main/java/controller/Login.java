package controller;

import java.io.IOException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import external.ActiveDirectory;
import function.LoginFunction;
import function.RetrieveData;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("username");
		String password = request.getParameter("password");
		Logger loginLog = LoggerFactory.getLogger(Login.class);

		LoginFunction lf = new LoginFunction();

		DataSource ds = (DataSource)request.getServletContext().getAttribute("dbSource");
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
    	int check = 4;
    	try{
    		check = lf.checkSystemAccountDBSource(ds, user, password);
    	} catch (SQLException sqle) {
			loginLog.error("SQL Error @ Login.", sqle);
			request.setAttribute("errorMessage", "Something went wrong, please try again later!");
			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
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
    		request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("firstname", lf.getSystemAccount().getFirstName());
			request.getSession().setAttribute("lastname", lf.getSystemAccount().getLastName());
			request.getSession().setAttribute("phoneNumber", lf.getSystemAccount().getPhoneNumber());
			request.getSession().setAttribute("email", lf.getSystemAccount().getEmail());
			request.getSession().setAttribute("department", lf.getSystemAccount().getDepartment());
			request.getSession().setAttribute("unit_id", lf.getSystemAccount().getUnitId());
			request.getSession().setAttribute("position", lf.getSystemAccount().getStatus());
			
			try {
				request.getSession().setAttribute("tickets", rd.getUserTicket(user, lf.getSystemAccount().getStatus(), lf.getSystemAccount().getUnitId()));
			} catch (SQLException e) {
				loginLog.error("SQL Error @ Login (setting tickets).", e);
			}
			
			if( lf.getSystemAccount().getLastName().isEmpty() || lf.getSystemAccount().getPhoneNumber().isEmpty() || 
					lf.getSystemAccount().getEmail().isEmpty() ){
				
					response.sendRedirect("FirstLoginUpdate");
				
			}
			else{
				if(request.getSession().getAttribute("dReferred") != null){
						int id = Integer.parseInt(request.getSession().getAttribute("dReferred").toString());
						request.getSession().removeAttribute("dReferred");
						response.sendRedirect("Details?id="+id);
				}
				else{
					response.sendRedirect("Home");
				}
			
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
	
	                    request.getSession().setAttribute("user", user);
	        			request.getSession().setAttribute("firstname", firstName);
	        			request.getSession().setAttribute("lastname", "");
	        			request.getSession().setAttribute("email", emailAD);
	        			request.getSession().setAttribute("phoneNumber", "");
	        			request.getSession().setAttribute("department", "");
	        			request.getSession().setAttribute("unit_id", 0);
	        			request.getSession().setAttribute("position", 3);
	        			
	        			result.close();
		            	activeDirectory.closeLdapConnection();
		            	
		            	//request.getRequestDispatcher("/WEB-INF/FirstLoginUpdate.jsp").forward(request, response);
		            	response.sendRedirect("FirstLoginUpdate");
	            	}
	            	else{
	            		// This should be when check == 1 and NOT 2 or 3.
	            		// If it's 2 or 3, then something is VERY WRONG.
	            		
	            		request.getSession().setAttribute("user", user);
	        			request.getSession().setAttribute("firstname", lf.getSystemAccount().getFirstName());
	        			request.getSession().setAttribute("lastname", lf.getSystemAccount().getLastName());
	        			request.getSession().setAttribute("phoneNumber", lf.getSystemAccount().getPhoneNumber());
	        			request.getSession().setAttribute("email", lf.getSystemAccount().getEmail());
	        			request.getSession().setAttribute("department", lf.getSystemAccount().getDepartment());
	        			request.getSession().setAttribute("unit_id", lf.getSystemAccount().getUnitId());
	        			request.getSession().setAttribute("position", lf.getSystemAccount().getStatus());
	        			
	        			result.close();
		            	activeDirectory.closeLdapConnection();
	        			
	        			try {
	        				request.getSession().setAttribute("tickets", rd.getUserTicket(user, lf.getSystemAccount().getStatus(), lf.getSystemAccount().getUnitId()));
	        			} catch (SQLException e) {
	        				loginLog.error("SQL Error @ Login (setting tickets).", e);
	        			}
	        			
	        			if( lf.getSystemAccount().getLastName().isEmpty() || lf.getSystemAccount().getPhoneNumber().isEmpty() || 
	        					lf.getSystemAccount().getEmail().isEmpty() ){

	        					response.sendRedirect("FirstLoginUpdate");
	        				
	        			}
	        			else{
	        				if(request.getSession().getAttribute("dReferred") != null){
	    						int id = Integer.parseInt(request.getSession().getAttribute("dReferred").toString());
	    						request.getSession().removeAttribute("dReferred");
	    						response.sendRedirect("Details?id="+id);
		    				}
		    				else{
		    					response.sendRedirect("Home");
		    				}
	        			}
	            	}
	            	}else{
	            		// This is when check = 0, 1, or 2
	        			result.close();
		            	activeDirectory.closeLdapConnection();
	            		
		    			request.setAttribute("errorMessage", "Invalid username or password, please try again!");
		    			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
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
		    				
							request.setAttribute("errorMessage", "Invalid username or password, please try again!");
							request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
		    			}
		    			else{
		    				request.setAttribute("errorMessage", "CSULA account authentication seems to be down, please try again later!");
							request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
		    			}
		    		}
		    		else
		    		{
						request.setAttribute("errorMessage", "Invalid username or password, please try again!");
						request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
		    		}

    		}
			
    	}
	}
}
	
