package it.unipi.iot.MIoBike.web.servlets;

public class UserSession {
	private int user_id;
	
	public UserSession(int id) {
		user_id = id;
	}
	
	public int getUserId() {
		return user_id;
	}
}
