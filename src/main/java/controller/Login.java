package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import external.ActiveDirectory;
import model.User;
import function.LoginFunction;
import function.RetrieveTicket;
import function.RetrieveUpdates;


@WebServlet(urlPatterns="/Login", loadOnStartup = 1)
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public Login() {
        super();
        
        // TODO Auto-generated constructor stub
    }
    


 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 	System.out.println("hello");
		request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Byebye");
		String user = request.getParameter("username");
		String password = request.getParameter("password");
		
		LoginFunction lf = new LoginFunction();
		RetrieveTicket rttk = new RetrieveTicket();
		RetrieveUpdates rtup = new RetrieveUpdates();
		
		int check = 4;
		
		try {
			check = lf.checkSystemAccount(user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (check == 0){
			// Checks AD
			String domain = "ad.calstatela.edu";
	        String choice = "username";

            ActiveDirectory activeDirectory = new ActiveDirectory();
            
	        try{
                activeDirectory.connect( domain, user, password );
                NamingEnumeration<SearchResult> result = activeDirectory
                        .searchUser( user, choice, null );
                
                if(result.hasMore())
                {
                	SearchResult rs = (SearchResult) result.next();
                    Attributes attrs = rs.getAttributes();
                    String temp = attrs.get( "givenName" ).toString();
                    //-------------------------------------------------------
//                    NamingEnumeration<String> test = attrs.getIDs();
//                    
//                    List<String> testList = new ArrayList<String>();
//                    while(test.hasMore()){
//                    	testList.add(test.next());
//                    }
                    //-------------------------------------------------------
                    String firstName = temp
                        .substring( temp.indexOf( ":" ) + 1 );
                    String lastName = temp.substring( temp.indexOf( ":" ) + 2 );
                    temp = attrs.get( "mail" ).toString();
                    String emailId = temp
                        .substring( temp.indexOf( ":" ) + 1 );
                    temp = attrs.get( "distinguishedName" ).toString();
                    String distinguishedName = temp
                        .substring( temp.indexOf( ":" ) + 1 );
        			//-------------------------------------------------------
                    
                	request.getSession().setAttribute("user", user);
        			request.getSession().setAttribute("firstname", firstName);
        			request.getSession().setAttribute("emailId", emailId);
        			request.getSession().setAttribute("position", 3);

        			//-------------------------------------------------------
//        			request.getSession().setAttribute("testList", testList);
//
//                    String CIN = attrs.get("cn").toString();
//                    String userID = attrs.get("uid").toString();
//                    String accountName = attrs.get("sAMAccountName").toString();
//                    String principalName = attrs.get("userPrincipalName").toString();
//        			request.getSession().setAttribute("CIN", CIN);
//        			request.getSession().setAttribute("uid", userID);
//        			request.getSession().setAttribute("accountName", accountName);
//        			request.getSession().setAttribute("userPrincipalName", principalName);
        			
        			
                	request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
                
                }
                else{
        			request.setAttribute("errorMessage", "Invalid username or password, please try again!");
        			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
                }
                

            	activeDirectory.closeLdapConnection();
                
	        
			} catch(NamingException e){

            	activeDirectory.closeLdapConnection();
				request.setAttribute("errorMessage", "Invalid username or password, please try again!");
				request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (check == 1){
			// Incorrect pass
			request.setAttribute("errorMessage", "Invalid username or password, please try again!");
			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
		}
		else if (check == 2){
			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("firstname", lf.getSystemAccount().getFirstname());
			request.getSession().setAttribute("lastname", lf.getSystemAccount().getLastname());
			request.getSession().setAttribute("unit_id", lf.getSystemAccount().getUnit_id());
			request.getSession().setAttribute("position", lf.getSystemAccount().getStatus());
			try {
				request.getSession().setAttribute("tickets", rttk.getUserTicket(user, lf.getSystemAccount().getStatus()));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);	
		}


//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		Connection c = null;
//		try
//		{
//			String url = "jdbc:mysql://cs3.calstatela.edu/cs4961stu05";
//			String db_user = "cs4961stu05";
//			String db_pass = ".Im0nx.W";
//			
//			c = DriverManager.getConnection(url, db_user, db_pass);
//			
//			String search_user = "select * from users where username = ?";
//            PreparedStatement pstmt = c.prepareStatement( search_user );
//            pstmt.setString( 1, user );
//            ResultSet rs = pstmt.executeQuery();
//            
//            if(rs.next())
//            {
//    			if(rs.getString("pass").equals(org.apache.commons.codec.digest.DigestUtils.sha256Hex(password)))
//    			{
///*    				User userSession = new User(rs.getInt("id"),
//    						rs.getString("firstname"),
//    						rs.getString("lastname"),
//    						user,
//    						org.apache.commons.codec.digest.DigestUtils.sha256Hex(password),
//    						rs.getInt("CIN"),
//    						rs.getInt("position"),
//    						rs.getInt("unit_id"),
//    						rs.getInt("superviser_id"),
//    						rs.getBoolean("is_supervisor")
//    						);*/
//    				
//    				/* Not sure if I should use a user object as attribute in the session or
//    				 * to set multiple attributes one like below
//    				 */
//    				
//    				request.getSession().setAttribute("user", user);
//    				request.getSession().setAttribute("firstname", rs.getString("firstname"));
//    				request.getSession().setAttribute("lastname", rs.getString("lastname"));
//    				request.getSession().setAttribute("CIN", rs.getInt("CIN"));
//    				request.getSession().setAttribute("unit_id", rs.getInt("unit_id"));
//    				request.getSession().setAttribute("supervisor_id", rs.getInt("supervisor_id"));
//    				request.getSession().setAttribute("is_supervisor", rs.getBoolean("is_supervisor"));
//    			}
//    			else
//    			{
//        			c.close();
//        			request.setAttribute("errorMessage", "Invalid username or password, please try again!");
//        			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
//    			}
//            }
//            else
//            {
//            	/* If the person is not in our database, we need to check 
//            	 * the active directory's database for them.
//            	 * However, I haven't understand completely what the AD does so
//            	 * right now this is just returns an error. 
//            	 */
//            	c.close();
//    			request.setAttribute("errorMessage", "Invalid username or password, please try again!");
//    			request.getRequestDispatcher("/WEB-INF/Login.jsp").forward(request, response);
//            }
//            
//		}
//        catch( SQLException e )
//        {
//            throw new ServletException( e );
//        }
//        finally
//        {
//            try
//            {
//            	System.out.println("test");	// Just a test to see if this goes through before redirect
//                if( c != null ) c.close();
//            }
//            catch( SQLException e )
//            {
//                throw new ServletException( e );
//            }
//        }


	}

}
