package edu.upenn.cis455.storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import static com.sleepycat.persist.model.Relationship.*;

import com.sleepycat.persist.model.SecondaryKey;
@Entity
public class User {
	@PrimaryKey
	private String username;
	@SecondaryKey(relate=ONE_TO_ONE)
	private String password;
	@SecondaryKey(relate=ONE_TO_ONE)
	private String firstName;
	@SecondaryKey(relate=ONE_TO_ONE)
	private String lastName;
	@SecondaryKey(relate=ONE_TO_MANY)
	private Set<String> channels = new HashSet<String>();
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Set<String> getChannels() {
		return channels;
	}
	public void setChannels(Set<String> channels) {
		this.channels = channels;
	}
	
}
