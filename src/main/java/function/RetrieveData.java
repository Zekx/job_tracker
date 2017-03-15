package function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import model.Ticket;
import model.Unit;
import model.Update;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class's main purpose is to search the database for information, hence its name.
 * All of the Servlets in this application use this class for its function to 
 * get the necessary data such as users, tickets, units, technician that was assigned to a ticket, 
 * etc.
 */
public class RetrieveData {
	
	private DataSource datasource;
	private String dbUrl = "";
	private String dbUser = "";
	private String dbPass = ""; 
	private Logger rdLog = null;
	
	
	/**
	 * Old deprecated constructor; No longer in use unless necessary in the future.
	 * 
	 * @param dbURL The database's URL (ex: jdbc:mysql://localhost/example).
	 * @param dbUser The database account's username.
	 * @param dbPass The database account's password.
	 */
	public RetrieveData(String dbURL, String dbUser, String dbPass){ // Constructor meant for localhosts 
		this.datasource = null;
		this.dbUrl= dbURL;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
		rdLog = LoggerFactory.getLogger(RetrieveData.class);
	}
	
	/**
	 *  Main constructor use for this web application.
	 *  
	 *  @param DataSource The DataSource object for connecting to the database.
	 */
	public RetrieveData(DataSource datasource){

		this.datasource = datasource;
		rdLog = LoggerFactory.getLogger(RetrieveData.class);
	}

	
	/**
	 * This method returns a ticket from the database using its id.
	 * @param id The ticket's ID number.
	 * @return Ticket Returns Ticket object.
	 */
	public Ticket getTicket(int id){
		Ticket ticket = null;
		
		try (Connection c = this.datasource.getConnection()){
			String search_user = "select * from tickets where id = ?";
			
			// Not sure if try-resource field for PreparedStatement is necessary but
			// I put it in just for precaution measure for memory leaks
			try(PreparedStatement pstmt = c.prepareStatement(search_user);){
				pstmt.setInt(1, id);
				
				try(ResultSet rs = pstmt.executeQuery();){
					if (rs.next()) {
						ticket = new Ticket(rs.getInt("id"), 
								rs.getString("username"), 
								rs.getString("userFirstName"), 
								rs.getString("userLastName"), 
								getTechnicians(id),
								rs.getString("phone"), 
								rs.getString("email"), 
								rs.getString("department"),
								rs.getInt("Progress"), 
								rs.getInt("priority"), 
								rs.getInt("unitId"), 
								rs.getString("subject"),
								rs.getString("details"), 
								rs.getDate("startDate"), 
								rs.getTime("startDate").toString(),
								rs.getDate("endDate"),
								rs.getDate("lastUpdated"), 
								rs.getTime("lastUpdated").toString(), 
								rs.getString("ticketLocation"), 
								getTicketUpdates(id), 
								""); // Completion data field, not used.
					}	
				}
			}
			
		} catch (SQLException e) {
			rdLog.error("SQL Error @ RetrieveData's getTicket method.", e);
		} catch (Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getTicket method.", e);
		}

		return ticket;
	}
	
	/**
	 * This method returns a list of tickets that is associated to their username, position, and 
	 * unit id.
	 * 
	 * @param username The username of the user.
	 * @param position The position of the user ( 0 = sys. admin., 1 = unit supervisor, 2 = technician, 3 = regular user ).
	 * @param unit_id The unit id of the value. 
	 * @return List<Ticket> A list of tickets associated with the user.
	 * 
	 */
	public List<Ticket> getUserTicket(String username, int position, int unit_id) throws SQLException {

		List<Ticket> tickets = new ArrayList<Ticket>();
		String search_user = "";
		
		try (Connection c = this.datasource.getConnection()){
			
			// Setting the SQL string depending on the position of the user.
			if (position == 0) { // System Administrator
				search_user = "select * from tickets order by lastUpdated desc";
			}
			else if (position == 1 || position == 2) { // Supervising Technician or technician
				search_user = "select * from tickets where unitId = ? order by lastUpdated desc";
			} 
			else { // Regular users
				search_user = "select * from tickets where username = ? order by lastUpdated desc";
				//pstmt = c.prepareStatement(search_user);
				//pstmt.setString(1, username);
			}
			try(PreparedStatement pstmt = c.prepareStatement(search_user);){
				
				// Adding variables to the SQL statement.
				if(position == 1 || position == 2){
					pstmt.setInt(1, unit_id);
				}
				else if(position == 3){
					pstmt.setString(1, username);
				}
				
				try(ResultSet rs = pstmt.executeQuery();){
					// Searching and adding the tickets.
					while (rs.next()) {
						int id = rs.getInt("id");
						String usernameRequestor = rs.getString("username");
						String userFirstName = rs.getString("userFirstName");
						String userLastName = rs.getString("userLastName");
						String phone = rs.getString("phone");
						String email = rs.getString("email");
						//String department = rs.getString("department");
						String department = "";
						int currentProgress = rs.getInt("Progress");
						int priority = rs.getInt("priority");
						int unitId = rs.getInt("unitId");
						String subject = rs.getString("subject");
						String details = rs.getString("details");
						Date startDate = rs.getDate("startDate");
						String startDateTime = rs.getTime("startDate").toString();
						Date endDate = rs.getDate("endDate");
						Date lastUpdated = rs.getDate("lastUpdated");
						String lastUpdatedTime = rs.getTime("lastUpdated").toString();
						String ticketLocation = rs.getString("ticketLocation");
						String completionDetails = "";
		
						Ticket newTicket = new Ticket(id,
								usernameRequestor,
								userFirstName,
								userLastName,
								getTechnicians(id),
								phone,
								email,
								department,								
								currentProgress,
								priority,
								unitId,
								subject,
								details,
								startDate,
								startDateTime,
								endDate,
								lastUpdated,
								lastUpdatedTime,
								ticketLocation,
								getTicketUpdates(id),
								completionDetails);
		
						tickets.add(newTicket);
					}
				}
			}
			
		} catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getUserTicket method.", e);
		} catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getUserTicket method.", e);
		}

		return tickets;
	}

	/**
	 *  This method is call when a user want to look for ticket with specific
	 *  string or text in the fields of the ticket.
	 *  
	 * @param term The string that the user is searching.
	 * @param username The user's username.
	 * @param position The position of the user.
	 * @param unit_id The ID of the unit that the user belongs to ( 0 if he/she belongs to none of the units ).
	 * @return List<Ticket> A list of all the tickets that matches the term that the user is searching for.
	 * @throws SQLException SQL Error.
	 * @throws Exception Non-SQL Error.
	 */
	public List<Ticket> searchTicket(String term, String username, int position, int unit_id) throws SQLException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		String search_user = "";
		try(Connection c = this.datasource.getConnection()) {
			if (position == 0) { // System Administrator
				search_user = "select * from tickets where CONCAT_WS('', username, userFirstName, userLastName, phone, email, Progress, department, unitId, "
						+ "subject, details, startDate, endDate, lastUpdated, ticketLocation) like ? order by lastUpdated desc";
				// pstmt = c.prepareStatement(search_user);
				// pstmt.setString(1, '%' + term + '%');

			}
			else if (position == 1 || position == 2) { // Supervising Technician or Technician
				search_user = "select * from tickets where unitId = ? and CONCAT_WS('', username, userFirstName, userLastName, phone, email,"
						+ " Progress, department, unitId, subject, details, startDate, endDate, lastUpdated, ticketLocation) like ? order by lastUpdated desc";
				// pstmt = c.prepareStatement(search_user);
				// pstmt.setInt(1, unit_id);
				// pstmt.setString(2, '%' + term + '%');
				
			} 
			else { // Regular users
				search_user = "select * from tickets where username = ? and CONCAT_WS('', username, userFirstName, userLastName, phone, email, "
						+ "Progress, unitId, subject, details, startDate, endDate, lastUpdated, ticketLocation) like ? order by lastUpdated desc";
				// pstmt = c.prepareStatement(search_user);
				// pstmt.setString(1, username);
				// pstmt.setString(2, '%' + term + '%');
			}
			try(PreparedStatement pstmt = c.prepareStatement(search_user)){
				// Setting variables for search.
				if (position == 0) { // System Administrator
					pstmt.setString(1, '%' + term + '%');
				}
				else if (position == 1 || position == 2) { // Supervising Technician or Technician
					pstmt.setInt(1, unit_id);
					pstmt.setString(2, '%' + term + '%');
				} 
				else { // Regular users
					pstmt.setString(1, username);
					pstmt.setString(2, '%' + term + '%');
				}
				
				try(ResultSet rs = pstmt.executeQuery()){
					// Adding result to list.
					while (rs.next()) {
						int id = rs.getInt("id");
						String usernameRequestor = rs.getString("username");
						String userFirstName = rs.getString("userFirstName");
						String userLastName = rs.getString("userLastName");
						String phone = rs.getString("phone");
						String email = rs.getString("email");
						String department = rs.getString("department");
						int currentProgress = rs.getInt("Progress");
						int priority = rs.getInt("priority");
						int unitId = rs.getInt("unitId");
						String subject = rs.getString("subject");
						String details = rs.getString("details");
						Date startDate = rs.getDate("startDate");
						String startDateTime = rs.getTime("startDate").toString();
						Date endDate = rs.getDate("endDate");
						Date lastUpdated = rs.getDate("lastUpdated");
						String lastUpdatedTime = rs.getTime("lastUpdated").toString();
						String ticketLocation = rs.getString("ticketLocation");
						String completionDetails = "";
		
						Ticket newTicket = new Ticket(id,
								usernameRequestor,
								userFirstName,
								userLastName,
								getTechnicians(id),
								phone,
								email,
								department,
								currentProgress,
								priority,
								unitId,
								subject,
								details,
								startDate,
								startDateTime,
								endDate,
								lastUpdated,
								lastUpdatedTime,
								ticketLocation,
								getTicketUpdates(id),
								completionDetails);
		
						tickets.add(newTicket);
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's searchTicket method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's searchTicket method.", e);
		}
		
		return tickets;
	}
	
	/**
	 * This method searches and returns the User object base on ID input.
	 * 
	 * @param userId The ID of the user.
	 * @return User Returns the User object that contains the inputed ID.
	 */
	public User getUser(int userId){
		User user = null;

		try(Connection c = this.datasource.getConnection()){
			String getUser = "select * from users where id = ?";
			
			try(PreparedStatement pstmt = c.prepareStatement(getUser)){
				pstmt.setInt(1, userId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						user = new User(rs.getInt("id"),
								rs.getString("firstname"),
								rs.getString("lastname"),
								rs.getString("username"),
								rs.getString("phone"),
								rs.getString("email"),
								rs.getString("department"),
								rs.getInt("position"),
								rs.getInt("unit_id"));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getUser method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getUser method.", e);
		}
		
		return user;
	}
	
	/**
	 * This method search a user in the database using their username and return the 
	 * User object when found.
	 * 
	 * @param username Username of the user.
	 * @return User Returns user object that have the inputted username.
	 */
	public User getUserByUsername(String username){
		User user = null;
		
		try(Connection c = this.datasource.getConnection()){
			String getUser = "select * from units where username = ?";
			
			try(PreparedStatement pstmt = c.prepareStatement(getUser)){
				pstmt.setString(1, username);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						user = new User(rs.getInt("id"),
								rs.getString("firstname"),
								rs.getString("lastname"),
								rs.getString("username"),
								rs.getString("phone"),
								rs.getString("email"),
								rs.getString("department"),
								rs.getInt("position"),
								rs.getInt("unit_id"));
					}
				}
			}
			
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getUserByUsername method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getUserByUsername method.", e);
		}
		
		return user;
	}
	
	public Unit getUnit(int unitId){
		Unit unit = null;
		
		try (Connection c = this.datasource.getConnection()){
			String getUser = "select * from units where id = ?";
			
			try(PreparedStatement pstmt = c.prepareStatement(getUser)){
				pstmt.setInt(1, unitId);
				try(ResultSet rs = pstmt.executeQuery()){
				
					if(rs.next()){
						unit = new Unit(rs.getInt("id"),
								rs.getString("unitName"),
								rs.getString("phone"),
								rs.getString("location"),
								rs.getString("email"),
								rs.getString("description"));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getUnit method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getUnit method.", e);
		}
		
		return unit;
	}
	
	
	/**
	 *  This method returns a list of ALL users inside the database.
	 *  
	 * @return List<User> A list of ALL users in the database.
	 */
	public List<User> getAllUsers(){
		List<User> users = new ArrayList<User>();
		
		String getUser = "";
		
		try (Connection c = this.datasource.getConnection()){
			getUser = "select * from users";
			
			try(PreparedStatement pstmt = c.prepareStatement(getUser)){
				
				try(ResultSet rs = pstmt.executeQuery()){
					while(rs.next()){
						users.add(new User(rs.getInt("id"),
								rs.getString("firstname"),
								rs.getString("lastname"),
								rs.getString("username"),
								rs.getString("phone"),
								rs.getString("email"),
								rs.getString("department"),
								rs.getInt("position"),
								rs.getInt("unit_id")));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getAllUsers method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getAllUsers method.", e);
		}
		
		return users;
	}
	
	/**
	 *  This method returns a list of updates that is under the ticket's ID.
	 *  
	 * @param ticketId ID of the ticket.
	 * @return List<Update> Returns a list of Update objects.
	 * 
	 */
	public List<Update> getTicketUpdates(int ticketId) throws SQLException {
		List<Update> tickets = new ArrayList<Update>();
		
		try(Connection c = this.datasource.getConnection()) {
			String search_update = "select * from updates where ticketId = ? order by modifiedDate desc";
			
			try( PreparedStatement pstmt = c.prepareStatement(search_update)){
				pstmt.setInt(1, ticketId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					while (rs.next()) {
		
						int id = rs.getInt("id");
						int tId = rs.getInt("ticketId");
						String modifier = rs.getString("modifier");
						String modifierDetails = rs.getString("updateDetails");
						String modifiedDate = rs.getTimestamp("modifiedDate").toString();
		
						tickets.add(new Update(id, tId, modifier, modifierDetails, modifiedDate));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getTicketUpdates method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getTicketUpdates method.", e);
		}

		return tickets;
	}
	
	/**
	 * This method returns a list of all user assigned under a ticket.
	 * 
	 * @param ticketId The ID of the ticket.
	 * @return List<User> A list of all technician assigned to the ticket.
	 *
	 */
	public List<User> getTechnicians(int ticketId) throws SQLException {
		List<User> userList = new ArrayList<User>();

		try(Connection c = this.datasource.getConnection()) {
			String search_update = "select technicianUser from assignments where ticketId = ?";
			
			try(PreparedStatement pstmt = c.prepareStatement(search_update)){
				pstmt.setInt(1, ticketId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					while (rs.next()) {
		
						String getUser = "select * from users where username = ?";
						try(PreparedStatement pstmt2 = c.prepareStatement(getUser)){
							pstmt2.setString(1, rs.getString("technicianUser").toString());
							
							try(ResultSet rs2 = pstmt2.executeQuery()){
									
								/* 
								 * This is pretty ugly, sorry :(
								 * Maybe I could've use join for the SQL statement. Oh whales.
								 */
								if (rs2.next()) {
									userList.add(new User(rs2.getInt("id"),
											rs2.getString("firstname"),
											rs2.getString("lastname"),
											rs2.getString("username"),
											rs2.getString("phone"),
											rs2.getString("email"),
											rs2.getString("department"),
											rs2.getInt("position"),
											rs2.getInt("unit_id")));
								}
							}
						}
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getTechnicians method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getTechnicians method.", e);
		}

		return userList;
	}
	
	public boolean checkAssignment(String user, int ticketId){
		boolean assigned = false;
		String searchTechnician = "select * from assignments where ticketId = ? and technicianUser = ?";
		try(Connection c = this.datasource.getConnection()){
			try(PreparedStatement pstmt = c.prepareStatement(searchTechnician)){
				pstmt.setInt(1, ticketId);
				pstmt.setString(2, user);
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						assigned = true;
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's checkAssignment method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's checkAssignment method.", e);
		}
		
		return assigned;
	}
	
	/**
	 *  This method returns a HashMap of users assign to a ticket.
	 *  The format is Map< TechnicianUsername, Boolean > where the boolean is True if 
	 *  the technician is assigned to the ticket.
	 *  
	 * @param ticketId The ID of the ticket.
	 * @return Map<String, Boolean> 
	 */
	public Map<String, Boolean> getTechId(int ticketId){
		Map<String, Boolean> techIds = new HashMap<String, Boolean>();
		
		String search_technician = "select technicianUser from assignments where ticketId = ? ";
		
		try(Connection c = this.datasource.getConnection()){

			try(PreparedStatement pstmt = c.prepareStatement(search_technician)){
				pstmt.setInt(1, ticketId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					while(rs.next()){
						techIds.put(rs.getString("technicianUser"), true);
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getTechId method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getTechId method.", e);
		}
		
		return techIds;
	}
	
	/**
	 *  This method returns a list of all technicians under a unit.
	 *  
	 * @param unitId The ID of the unit.
	 * @return List<User> List of the technicians under this unit.
	 */
	public List<User> getUnitTechnicians(int unitId){
		List<User> tchl = new ArrayList<User>();
		String search_user = "";
		
		try(Connection c = this.datasource.getConnection()){
			search_user = "select * from users where position = ? or position = ? and unit_id = ?";
			
			try(PreparedStatement pstmt = c.prepareStatement(search_user)){
				// Supervisor can also assign themselves if they wish.
				pstmt.setInt(1, 1);
				pstmt.setInt(2, 2); 
				pstmt.setInt(3, unitId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					while(rs.next()){
						tchl.add(new User(
								rs.getInt("id"),
								rs.getString("firstname"),
								rs.getString("lastname"),
								rs.getString("username"),
								rs.getString("phone"),
								rs.getString("email"),
								rs.getString("department"),
								rs.getInt("position"),
								rs.getInt("unit_id")
								));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getUnitTechnicians method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getUnitTechnicians method.", e);
		}
		return tchl;
	}

	/**
	 * This method returns a list of all the units in the database.
	 * 
	 * @return List<Unit> Returns a list of Unit objects.
	 */
	public List<Unit> getAllUnits(){
		Connection c = null;
		PreparedStatement ptsmt = null;
		ResultSet rs = null; 
		
		List<Unit> unitList = new ArrayList<Unit>();
		
		String getUnit = "select * from units";
		
		try{
			if(this.datasource == null){
				c = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
			}
			else{
				c = this.datasource.getConnection();
			}
			
			ptsmt = c.prepareStatement(getUnit);
			rs = ptsmt.executeQuery();
			
			while(rs.next()){
				unitList.add(new Unit(rs.getInt("id"),
						rs.getString("unitName"),
						rs.getString("phone"),
						rs.getString("location"),
						rs.getString("email"),
						rs.getString("description")
						));
			}
			
			ptsmt.close();
			rs.close();
			c.close();
			
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getAllUnit method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getAllUnit method.", e);
		}
		
		return unitList;
		
	}
	
	/**
	 * This method returns a list of all supervisors' email under a unit. 
	 *
	 * @param unitId The ID of the unit.
	 * @return List<String> List of supervisors' email.
	 */
	public List<String> getSupervisorEmails(int unitId){
		List<String> emails = new ArrayList<String>();
		String getEmail = "select email from users where unit_id = ? and position = ? ";

		try(Connection c = this.datasource.getConnection()){
			
			try(PreparedStatement pstmt = c.prepareStatement(getEmail)){
				pstmt.setInt(1, unitId);
				pstmt.setInt(2, 1);
				
				try(ResultSet rs = pstmt.executeQuery()){
					while(rs.next()){
						emails.add(rs.getString("email"));
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getSupervisorEmails method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getSupervisorEmails method.", e);
		}
		
		return emails;
	}
	
	/**
	 *  This method returns the email of the ticket's requester.
	 *  
	 * @param ticketId The ID of the ticket.
	 * @return String Returns the email of the requester.
	 */
	public String getRequestorEmailFromTicket(int ticketId){
		String email = "";
		String emailQuery = "select email from users where username = (select username from tickets where id = ?)";
		
		
		try(Connection c = this.datasource.getConnection()){
			
			try(PreparedStatement pstmt = c.prepareStatement(emailQuery)){
				pstmt.setInt(1, ticketId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						email = rs.getString("email");
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getRequestorEmailFromTicket method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getRequestorEmailFromTicket method.", e);
		}
		
		
		return email;
	}
	
	/**
	 *  This method returns the email of a user by using their username to search.
	 *  
	 * @param username The username of the user.
	 * @return String Returns the user's email.
	 */
	public String getEmailFromUsername(String username){
		String email = "";
		String emailQuery = "select email from users where username = ?";
		
		try(Connection c = this.datasource.getConnection()){

			try(PreparedStatement pstmt = c.prepareStatement(emailQuery)){
				pstmt.setString(1, username);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						email = rs.getString("email");
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getEmailFromUsername method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getEmailFromUsername method.", e);
		}
		return email;
	}
	
	/**
	 *  This method returns the id of the last ticket that the user created.
	 *  
	 * @param username The user's username.
	 * @return int The ID of the last created ticket of the user.
	 */
	public int getTicketIdFromUsernameUsingTime(String username){
		int id = 0;
		String query = "select id from tickets where username = ? and Progress = 0 order by lastUpdated desc";
		
		try (Connection c = this.datasource.getConnection()){
			
			try(PreparedStatement pstmt = c.prepareStatement(query)){
				pstmt.setString(1, username);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()){
						id = rs.getInt("id");
					}
				}
			}
		}catch(SQLException e){
			rdLog.error("SQL Error @ RetrieveData's getTicketIdFromUsernameUsingTime method.", e);
		}catch(Exception e){
			rdLog.error("Non-SQL Error @ RetrieveData's getTicketIdFromUsernameUsingTime method.", e);
		}
		
		return id;
	}

}
