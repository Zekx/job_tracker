package function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import function.StringFilter;
/**
 * This class is meant to check if the user exists in our database. The class has a private 
 * user variable. If the account exists under that username, the information 
 * will be saved into the user variable and it will check against the inputted password to see 
 * if it is correct. 
 * 
 */

public class LoginFunction {
	private User user;
	
	public LoginFunction(){
		this.user = null;
	}
	
	// Don't use this constructor; Deprecated.
	
	//NOTE: This version is the only one working for the Android Application. Further improvments of the method will be necessary for the mobile
	//application.
	public int checkSystemAccount(String dbURL, String dbUser, String dbPass, String user, String password) throws SQLException 
	{
		StringFilter sf = new StringFilter();
		 /*This method checks the input information with the one in the database.
		 * The method returns one of the following:
		 * 		0 -- User doesn't exist in the database
		 * 		1 -- User exist but since password is empty, the account is not
		 * 				 a special account
		 * 		2 -- User exists but incorrect password
		 * 		3 -- User exists, password is correct, user information is saved
		 * 				into the LoginFunction class variable*/

		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		int returnInt = 4;
		Connection c = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			c = DriverManager.getConnection(dbURL, dbUser, dbPass);
			
			String search_user = "select * from users where username = ?";
            pstmt = c.prepareStatement( search_user );
            pstmt.setString( 1, user );
            rs = pstmt.executeQuery();
            if(rs.next())
            {
            	if(rs.getString("pass").isEmpty())
            	{
            		int id = rs.getInt("id");
            		String firstName = sf.filterNull(rs.getString("firstname"));
            		String lastName = sf.filterNull(rs.getString("lastname"));
            		String phoneNumber = sf.filterNull(rs.getString("phone"));
            		String email = sf.filterNull(rs.getString("email"));
            		String department = sf.filterNull(rs.getString("department"));
            		int position = rs.getInt("position");
            		int unitId = rs.getInt("unit_id");
            		
            		
            		this.user = new User(id,
            				firstName,
            				lastName,
            				user,
            				phoneNumber,
            				email,
            				department,
            				position,
            				unitId);
            		
            		returnInt = 1;
            	}
            	else{
	            	if(rs.getString("pass").equals(org.apache.commons.codec.digest.DigestUtils.sha256Hex(password)))
	            	{
	            		int id = rs.getInt("id");
	            		String firstName = sf.filterNull(rs.getString("firstname"));
	            		String lastName = sf.filterNull(rs.getString("lastname"));
	            		String phoneNumber = sf.filterNull(rs.getString("phone"));
	            		String email = sf.filterNull(rs.getString("email"));
	            		String department = sf.filterNull(rs.getString("department"));
	            		int position = rs.getInt("position");
	            		int unitId = rs.getInt("unit_id");
	            		
	            		
	            		this.user = new User(id,
	            				firstName,
	            				lastName,
	            				user,
	            				phoneNumber,
	            				email,
	            				department,
	            				position,
	            				unitId);
	            		
	            		returnInt =  3;
	            	}
	            	else
	            	{
	            		returnInt =  2;	// Incorrect password
	            		
	            	}
            	}
            }
            else{
            	returnInt =  0;	 // User does not exist in the system
            }
            pstmt.close();
            rs.close();
            c.close();
		}
		finally{
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(c);
		}
		
		return returnInt;
	}
	
	/**
	 *  This method uses the DataSource param object to create a connection to the database.
	 *  Afterwards, it will verify the user and password to see if an account with those 
	 *  credentials exists within the database. It returns the following value and what they mean:
	 *   
	 *  
     *  0 -- User doesn't exist in the database.
	 *  1 -- User exist but since password is empty, the account is not
	 * 	    	 a special account.
	 *  2 -- User exists but incorrect password.
	 *  3 -- User exists, password is correct, user information is saved
	 * 				into the LoginFunction class variable.
	 *  
	 *  
	 *  
	 * @param datasource DataSource object to create a connection.
	 * @param user	The username of the account.
	 * @param password	The password of the account.
	 * @return int Return one of four int values ( 0 - 4 ) and their representations are listed above.
	 * 
	 */
	public int checkSystemAccountDBSource(DataSource datasource, String user, String password) throws SQLException 
	{
		/* This method checks the input information with the one in the database.
		 * The method returns one of the following:
		 * 		0 -- User doesn't exist in the database
		 * 		1 -- User exist but since password is empty, the account is not
		 * 				 a special account
		 * 		2 -- User exists but incorrect password
		 * 		3 -- User exists, password is correct, user information is saved
		 * 				into the LoginFunction class variable
		 */
		StringFilter sf = new StringFilter();
		Logger loginFunctionLog = LoggerFactory.getLogger(LoginFunction.class);
		int returnInt = 4;
		
		try (Connection c = datasource.getConnection()){	
			String search_user = "select * from users where username = ?";
            try(PreparedStatement pstmt = c.prepareStatement( search_user )){
	            pstmt.setString( 1, user );
	            try(ResultSet rs = pstmt.executeQuery()){
		            if(rs.next())
		            {
		            	if(rs.getString("pass").isEmpty())
		            	{
		            		int id = rs.getInt("id");
		            		String firstName = sf.filterNull(rs.getString("firstname"));
		            		String lastName = sf.filterNull(rs.getString("lastname"));
		            		String phoneNumber = sf.filterNull(rs.getString("phone"));
		            		String email = sf.filterNull(rs.getString("email"));
		            		String department = sf.filterNull(rs.getString("department"));
		            		int position = rs.getInt("position");
		            		int unitId = rs.getInt("unit_id");
		            		
		            		
		            		this.user = new User(id,
		            				firstName,
		            				lastName,
		            				user,
		            				phoneNumber,
		            				email,
		            				department,
		            				position,
		            				unitId);
		            		
		            		returnInt = 1;
		            	}
		            	else{
			            	if(rs.getString("pass").equals(org.apache.commons.codec.digest.DigestUtils.sha256Hex(password)))
			            	{
			            		int id = rs.getInt("id");
			            		String firstName = sf.filterNull(rs.getString("firstname"));
			            		String lastName = sf.filterNull(rs.getString("lastname"));
			            		String phoneNumber = sf.filterNull(rs.getString("phone"));
			            		String email = sf.filterNull(rs.getString("email"));
			            		String department = sf.filterNull(rs.getString("department"));
			            		int position = rs.getInt("position");
			            		int unitId = rs.getInt("unit_id");
			            		
			            		
			            		this.user = new User(id,
			            				firstName,
			            				lastName,
			            				user,
			            				phoneNumber,
			            				email,
			            				department,
			            				position,
			            				unitId);
			            		
			            		returnInt =  3;
			            	}
			            	else
			            	{
			            		returnInt =  2;	// Incorrect password
			            		
			            	}
		            	}
		            }
		            else{
		            	returnInt =  0;	 // User does not exist in the system
		            }
	            }
            }
		}catch(SQLException e){
			loginFunctionLog.error("SQL Error @ LoginFunction.", e);
		}catch(Exception e){
			loginFunctionLog.error("Non-SQL Error @ LoginFunction.", e);
		}
		return returnInt;
	}
	
	/**
	 *  This method returns the User object. If the checkSystemAccountDBSource method
	 *  above ran before this method, it would contain the User object of that account 
	 *  if it is valid; otherwise, the object would be null.
	 * @return User Returns the User object.
	 */
	public User getSystemAccount(){
		return user;
	}
}
