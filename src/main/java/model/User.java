 package model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id; 				// User's unique id
	private String firstName; 		// User's first name
	private String lastName; 		// User's last name
	private String userName; 		// Username and email are the same.

	// Types of users on the system.
	private enum Position {
		SYS_ADMIN(0), SUPERVISING_TECHNICIAN(1), TECHNICIAN(2), USER(3);

		private int positionValue;

		Position(int position_value) {
			this.positionValue = position_value;
		}

		public int getValue() {
			return positionValue;
		}
	};

	private String phoneNumber;
	
	private String department;
	
	private String email;
	
	private Position status;

	private int unitId; 		// Describes where the user belongs to in a unit (by

	// Simple constructor for regular users ( students )
	public User(int id, String firstname, String lastname, String username) {
		this.id = id;
		this.firstName = firstname;
		this.lastName = lastname;
		this.userName = username;
		this.status = Position.USER;
		this.unitId = 0; 				// User does not belongs to any unit\
	}

	// Full user paramenter constructor
	public User(int id, String firstname, String lastname, String username, String phone, String email,
			String department,int position, int unit_id)
	{
		this.id = id;
		this.firstName = firstname;
		this.lastName = lastname;
		this.userName = username;
		this.phoneNumber = phone;
		this.email = email;
		this.department = department;
		switch(position)
		{
			case 0:
				this.status = Position.SYS_ADMIN;
				break;
			case 1:
				this.status = Position.SUPERVISING_TECHNICIAN;
				break;
			case 2:
				this.status = Position.TECHNICIAN;
				break;
			case 3:									// These assume that you want to enter a new technician
			default: 								// and somehow enter a number thats not intended
				this.status = Position.USER;
				break;
		}
		
		this.unitId = unit_id;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public String getUsername() {
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	
	public String getEmailFromUsername() {
		return userName + "@calstatela.edu"; // Since username has the same
												// header as email with the
												// "@calstatela.edu" domain
	}

	public String getEmail(){
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unit_id) {
		this.unitId = unit_id;
	}

	public void setStatus(int position) {
		switch (position) {
		case 0:
			this.status = Position.SYS_ADMIN;
			break;
		case 1:
			this.status = Position.SUPERVISING_TECHNICIAN;
			break;
		case 2:
			this.status = Position.TECHNICIAN;
			break;
		case 3:
			this.status = Position.USER;
			break;
		}

	}

	public int getStatus() {
		return status.getValue();
	}
	
	public String getStatusString(){
		String status = "";
		switch(getStatus()){
		case 0:
			status = "SYSTEM ADMINISTRATOR";
			break;
		case 1:
			status = "SUPERVISING TECHNICIAN";
			break;
		case 2:
			status = "TECHNICIAN";
			break;
		case 3:
		default:
			status = "USER";
			break;
		}
		
		return status;
	}

	public String toString(){
		return "First name: " + this.getFirstName() + "\n"
				+ "Last name:" + this.getLastName() + "\n"
				+ "User name:" + this.getUsername() + "\n"
				+ "Phone number:" + this.getPhoneNumber() + "\n"
		 		+ "Email:" + this.getEmail() + "\n"
		 		+ "Status:" + this.getStatus() + "\n"
		 		+ "Unit:" + this.getUnitId() + "\n";
	}
	
}
