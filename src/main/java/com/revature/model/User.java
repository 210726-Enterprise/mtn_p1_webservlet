package com.revature.model;

import com.revature.p1.orm.annotations.Column;
import com.revature.p1.orm.annotations.Entity;
import com.revature.p1.orm.annotations.Id;

import java.io.Serializable;

/**
 * Model class for the 'users' table, which contains the primary key column 'id' and varchar/String columns 'firstname', 'lastname' and 'emailaddress'.
 */
@Entity(tableName = "users")
public class User implements Serializable {

	@Id(columnName = "id")
	private int id;
	
	@Column(columnName = "firstname")
	private String firstname;
	
	@Column(columnName = "lastname")
	private String lastname;
	
	@Column(columnName = "emailaddress")
	private String emailaddress;
	

	public User() {
		super();
	}

	public User(int id, String firstname, String lastname, String emailaddress) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.emailaddress = emailaddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", emailaddress=" + emailaddress
				+ "]";
	}
}
