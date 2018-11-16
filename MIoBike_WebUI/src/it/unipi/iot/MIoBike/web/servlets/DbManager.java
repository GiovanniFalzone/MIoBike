package it.unipi.iot.MIoBike.web.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
 
public class DbManager {
	private String driver = "com.mysql.jdbc.Driver";
	private String db_name = "jdbc:mysql://localhost:3306/MIoBike_db";
	private String username = "root";
	private String password = "";
	
	Connection con;
	Statement st;  
	
	public DbManager() {}
	
	public DbManager(String d, String n, String usr, String pwd) {
		System.out.println("------------------ Access to DB ------------------");
		driver = d;
		db_name = n;
		username = usr;
		password = pwd;
	}
	
	private void openConnection() throws Exception {
		System.out.println("---------------- Open Connection -----------------");
		Class.forName(driver);
		con = DriverManager.getConnection(db_name, username, password);  
		st = con.createStatement();  
	}
	
	private void closeConnection() throws Exception {
		System.out.println("---------------- Close Connection ----------------");
		con.close(); 
	}
	
	private ResultSet executeQuery(String query) throws Exception { 
		System.out.println("---------------- Query Execution -----------------");
		return st.executeQuery(query);    
	}
	
	private int executeUpdate(String query, String table) throws Exception {
		System.out.println("-------------- Updating "+table+" table --------------");
		return st.executeUpdate(query);  
	}
	
	/*private String createColumns(Collection<String> data) {
		String keys = "";
		for (String k : data) {
			if(k != null) {
				keys = keys + " `" + k + "`,";
			}
		}
		
		keys = keys.substring(0, keys.length() - 1);
		
		return keys;
	}
	
	private String createValues(Collection<String[]> data) {
		String values = "";

		for (String[] v : data) {
			if(v[0] != null) {
				if(v[0].equals("on"))
					values = values + " '" + 1 + "',";
				else
					values = values + " '" + v[0] + "',";
			}
		}
		
		values = values.substring(0, values.length() - 1);
		
		return values;
	}
	
	public String encrypt_password(String pwd) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		md5.update(pwd.getBytes());
		byte[] bytes_password = md5.digest();
		StringBuffer encrypted_password = new StringBuffer();
		for (byte b : bytes_password)
			encrypted_password.append(String.format("%02x", b & 0xff));
		
		return encrypted_password.toString();
	}*/
	
	/* Check if a user exists with its password */
	public boolean authenticate(String usr, String pwd) throws Exception {
		String query = "SELECT * FROM `users` WHERE username = '" + usr + "' AND password= '" + pwd + "'";
		
		openConnection();
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			return false;
		}
		return true;
	}
	
	/* Retrieving user data from "users" table */
	public HashMap<String, Object> login(String usr, String pwd) throws Exception {
		String query = "SELECT * FROM `users` WHERE username = '" + usr + "' AND password= '" + pwd + "'";
		
		if(!con.isValid(500));
			openConnection();
		System.out.println("------------------- Logging in -------------------");
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return null;
		}

		System.out.println("-------------------- SUCCESS ---------------------");
		HashMap<String, Object> user = new HashMap<String, Object>();	//associative array
		
		user.put("ID_user", rs.getInt("ID_user"));
		user.put("username", rs.getString("username"));
		user.put("password", rs.getString("password"));
		user.put("admin", rs.getBoolean("admin"));
		user.put("email", rs.getString("email"));
		user.put("weight", rs.getDouble("weight"));
		user.put("ID_key", rs.getInt("ID_key"));
		user.put("ID_subscription", rs.getInt("ID_subscription"));
		user.put("avatar", rs.getString("avatar"));
		
		closeConnection();
	
		return user;
	}
	
	/* Retrieving subscription data for a subscription ID from "subscriptions" table */
	public HashMap<String, Object> getUserInfo(int id) throws Exception {
		System.out.println("-------- Getting user's subscription info --------");
		String query = "SELECT * FROM `user_stats` WHERE ID_user = '" + id + "'";

		openConnection();

		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return null;
		}
		
		System.out.println("-------------------- SUCCESS ---------------------");
		HashMap<String, Object> data = new HashMap<String, Object>();	//associative array

		data.put("ID_stat", rs.getInt("ID_stat"));
		data.put("trips", rs.getInt("total_trips"));
		data.put("tot_km", rs.getDouble("total_km"));
		data.put("tot_cal", rs.getDouble("total_cal"));
		data.put("fav_bike", rs.getInt("ID_favourite_bicycle"));
		data.put("avg_km", rs.getDouble("average_km"));
		data.put("avg_cal", rs.getDouble("average_cal"));
		data.put("avg_speed", rs.getDouble("average_speed"));
		data.put("max_km", rs.getDouble("max_km"));
		data.put("max_speed", rs.getDouble("max_speed"));
		
		System.out.println("User statistics: "+data.toString());
		
		closeConnection();
	
		return data;
	}

	/* Retrieving subscription data for a subscription ID from "subscriptions" table */
	public HashMap<String, Object> getSubscription(int id) throws Exception {
		System.out.println("-------- Getting user's subscription info --------");
		
		//find the last subscription for the given user
		String query = "SELECT * FROM `subscriptions` WHERE ID_user = '" + id + "' AND expiration_date =(SELECT MAX(expiration_date) FROM subscriptions where ID_user = '" + id + "')";
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return null;
		}
		
		System.out.println("-------------------- SUCCESS ---------------------");
		HashMap<String, Object> data = new HashMap<String, Object>();	//associative array

		data.put("activation_date", rs.getString("activation_date"));
		data.put("expiration_date", rs.getString("expiration_date"));
		data.put("balance", rs.getDouble("balance"));
		
		closeConnection();
	
		return data;
	}
	
	/* Count total trips, kilometers and calories for a certain user */
	public HashMap<String, Object> getTrips(int ID_user) throws Exception {
		System.out.println("----------- Getting user's trips' info -----------");
		
		String query = "SELECT COUNT(*) as count, SUM(km) as km, SUM(cal) as cal FROM `trips` WHERE ID_user = '" + ID_user +"'";

		openConnection();
		
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return null;
		}
		
		if (rs.getInt("count") == 0) {
			System.out.println("------------ No trips for this user --------------");
			return null;
		}
		
		System.out.println("-------------------- SUCCESS ---------------------");
		HashMap<String, Object> data = new HashMap<String, Object>();	//associative array
		
		data.put("count", rs.getString("count"));
		data.put("tot_km", rs.getString("km"));
		data.put("tot_cal", rs.getDouble("cal"));
		
		closeConnection();
	
		return data;
	}

	/* Count daily trips, kilometers and calories for a certain user */
	public HashMap<String, Object> getDailyTrips(int ID_user) throws Exception {
		System.out.println("-------- Getting user's daily trips' info --------");
		
		SimpleDateFormat today_sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = today_sdf.format(new Date());
		
		String query = "SELECT COUNT(*) as count, SUM(km) as km, SUM(cal) as cal FROM `trips` WHERE ID_user = '" + ID_user + "' AND date(end_time)='"+today.toString()+"'";

		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return null;
		}
		
		if (rs.getInt("count") == 0) {
			System.out.println("-------------- No trips for today ---------------");
			return null;
		}
		
		System.out.println("-------------------- SUCCESS ---------------------");
		HashMap<String, Object> data = new HashMap<String, Object>();	//associative array

		data.put("count", rs.getString("count"));
		data.put("tot_km", rs.getString("km"));
		data.put("tot_cal", rs.getDouble("cal"));
		
		closeConnection();
	
		return data;
	}
	
	/* Add a trip to "trips" table */
	public boolean addTrip(int ID_user, int ID_bicycle, String start_time, String end_time, double start_lat, 
		double start_lon, double end_lat, double end_lon, double km) throws Exception {

		System.out.println("------------- Adding trip for user " + ID_user + " -------------");

		String end_coord = ""+end_lat+","+end_lon;
		String start_coord = ""+start_lat+","+start_lon;

		//calculate speed
		Date date1=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(start_time);  
		Date date2=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(end_time);
		long millis1 = date1.getTime();
		long millis2 = date2.getTime();
		long diff = millis2-millis1;
		double sec_diff = diff/1000;
		double min_diff = sec_diff/60;
		double h_diff = min_diff/60;

		double avg_speed = km/h_diff;

		// calculate calories
		double cal = 5000;

		String query = "INSERT INTO trips (ID_user, ID_bicycle, start_time, end_time, initial_GPS, final_GPS, km, cal, avg_speed) "
				+ "VALUES ('"+ID_user+"', '"+ID_bicycle+"', '"+start_time+"', '"+end_time+"', '"+start_coord+"', '"+end_coord+"', '"+km+"', '"+cal+"', '"+avg_speed+"')";

		System.out.println(query);

		openConnection();
		
		String table = "trips";
		
		int rs = executeUpdate(query, table);
		
		if (rs==0) {
			closeConnection();
			System.out.println("---------------------- FAIL ----------------------");
			return false;
		}
		
		System.out.println("-------------------- SUCCESS ---------------------");		
		closeConnection();
		return true;
		//return data;
	}
	
	
	//TODO
	/*
	*/
	
}
