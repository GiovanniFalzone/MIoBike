/*
 * DbManager is used to manage SQL database, to open and close connection and perform query on database
 */
package it.unipi.iot.MIoBike.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;

import static it.unipi.iot.MIoBike.web.Constants_web.*;
 
public class DbManager {
	private String driver;
	private String db_name;
	private String username = "root";
	private String password = "";
	
	Connection con;
	Statement st;  
	
	public DbManager() {
		driver = DRIVER;
		db_name = DB_NAME;
	}
	
	public DbManager(String d, String n, String usr, String pwd) {
		if(DEV_MOD)
			printStatus("Access to DB");
		driver = d;
		db_name = n;
		username = usr;
		password = pwd;
	}
	
	private void openConnection() throws Exception {
		if(DEV_MOD)
			printStatus("Open Connection");
		Class.forName(driver);
		con = DriverManager.getConnection(db_name, username, password);  
		st = con.createStatement();  
	}
	
	private void closeConnection() throws Exception {
		if(DEV_MOD)
			printStatus("Close Connection");
		con.close(); 
	}
	
	private ResultSet executeQuery(String query) throws Exception { 
		if(DEV_MOD)
			printStatus("Executing query: "+query);
		return st.executeQuery(query);    
	}
	
	private int executeUpdate(String query, String table) throws Exception {
		if(DEV_MOD) {
			printStatus("Updating table: "+table);
			printStatus("Executing query: "+query);
		}
		return st.executeUpdate(query);  
	}
	
	/*
	 * SQL Query to check if user exists
	 */
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
	
	/*
	 * SQL Query Login for user, retrieves its data from users and subscription tables
	 */
	public JSONObject login(String usr, String pwd) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to login of user: "+usr);
		String query = "SELECT * FROM `users` WHERE username = '" + usr + "' AND password= '" + pwd + "'";
		
		openConnection();
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}

		printStatus("SUCCESS --> query: "+query);
		
		JSONObject user = new JSONObject();	//associative array
		
		user.put("ID_user", rs.getInt("ID_user"));
		user.put("username", rs.getString("username"));
		user.put("password", rs.getString("password"));
		user.put("admin", rs.getBoolean("admin"));
		user.put("email", rs.getString("email"));
		user.put("weight", rs.getDouble("weight"));
		user.put("ID_subscription", rs.getInt("ID_subscription"));
		user.put("avatar", rs.getString("avatar"));
		user.put("balance", rs.getDouble("balance"));

		
		if(DEV_MOD)
			System.out.println("Logged user data "+user.toString());
		
		closeConnection();
	
		return user;
	}
	
	/*
	 * Retrieves all username
	 */
	public JSONArray getUsers() throws Exception{
		if(DEV_MOD)
			printStatus("Attempt to get all users");
		String query = "SELECT * FROM `users`";

		openConnection();

		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		printStatus("SUCCESS --> query: "+query);
		JSONArray json_array = new JSONArray();

		if(rs.getInt("admin") != 1){
			JSONObject json = new JSONObject();
			json.put("ID", rs.getInt("ID_user"));
			json.put("name", rs.getString("username"));
			json.put("email", rs.getString("email"));
			json.put("subscription_ID", rs.getInt("ID_subscription"));
			json.put("avatar", rs.getString("avatar"));
			json.put("balance", rs.getDouble("balance"));
			json_array.put(json);
		}
		while(rs.next()) {
			if(rs.getInt("admin") != 1){
				JSONObject json = new JSONObject();
				json.put("ID", rs.getInt("ID_user"));
				json.put("name", rs.getString("username"));
				json.put("email", rs.getString("email"));
				json.put("subscription_ID", rs.getInt("ID_subscription"));
				json.put("avatar", rs.getString("avatar"));
				json.put("balance", rs.getDouble("balance"));
				json_array.put(json);
			}
		}
		
		if(DEV_MOD)
			System.out.println("Users: "+json_array.toString());
		closeConnection();
	
		return json_array;
	}

	/*
	 * Retrieve subscription data for a specific subscription ID from "subscriptions" table
	 */
	public JSONObject getSubscription(int id) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get subscription for user: "+id);
		
		//find the last subscription for the given user
		String query = "SELECT * FROM `subscriptions` WHERE ID_user = '" + id + "' AND expiration_date =(SELECT MAX(expiration_date) FROM subscriptions where ID_user = '" + id + "')";
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		JSONObject data = new JSONObject();
		data.put("activation_date", rs.getString("activation_date"));
		data.put("expiration_date", rs.getString("expiration_date"));
		
		closeConnection();

		if(DEV_MOD)
			System.out.println("Subscription data: "+data);
	
		return data;
	}
	
	public double getSpeed(String userName) throws Exception {
		String query = "SELECT COUNT(*) as tot, SUM(value) as total FROM sensors WHERE name = 'Speed' AND user = '"+userName+"'";
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return 0;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		int count = rs.getInt("tot");
		double tot = rs.getInt("total");
		double avg = tot/count;
		closeConnection();
		
		return avg;
		
	}

	/* Update subscription creating a new subscription for this user */
	public JSONObject updateSubscription(String userId, int balance) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to update user subscription");

		// Find activation date(today) and calculate new expiring date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today_date = new Date();
		Date exp_date = today_date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(exp_date);
		
		String type;
		switch(balance) {
		case 5:
			cal.add(Calendar.DATE, 7);
			type = "w";
			break;
		case 100:
			cal.add(Calendar.YEAR, 1);
			type = "y";
			break;
		case 10:
		default:
			type = "m";
			cal.add(Calendar.MONTH, 1);	
		}

		exp_date = cal.getTime();

		String today = sdf.format(today_date);
		String expiring = sdf.format(exp_date);
				
		SimpleDateFormat day_sdf = new SimpleDateFormat("dd");
		SimpleDateFormat month_sdf = new SimpleDateFormat("MM");
		String day = day_sdf.format(new Date());
		String month = month_sdf.format(new Date());
		String subs_id = userId+day+month;
		
		//find the last subscription for the given user
		String query = "INSERT INTO subscriptions (ID_subscription, ID_user, activation_date, expiration_date, type) VALUES ('"+subs_id+"', '"+userId+"', '"+today+"', '"+expiring+"', '"+type+"')";
		
		openConnection();
		
		String table = "subscriptions";
		int rs = executeUpdate(query, table);

		JSONObject json = new JSONObject();
		
		if (rs==0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return json;
		}
		
		printStatus("SUCCESS --> query: "+query);

		json.put("res", rs);
		json.put("subs_id", subs_id);
		
		closeConnection();
	
		if(DEV_MOD)
			System.out.println("Updated subscription: "+json.toString());
		return json;
	}
	
	// Update the balance when the user has renewed its subscription
	public boolean updateUserBalance(String user_id, int subs_id, int balance) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to update user table with new balance and subs id");
		String query = "UPDATE users SET ID_subscription = '"+subs_id+"', balance='"+balance+"' WHERE ID_user= '"+user_id+"'";
		openConnection();
		
		String table = "subscriptions";
		int rs = executeUpdate(query, table);
		
		if (rs==0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
	}
	
	public boolean decreaseBalance(String userId, double value) throws Exception {
		String query = "UPDATE users SET balance = balance - '"+value+"' WHERE username = '"+userId+"'";
		openConnection();
		
		String table = "users";
		int rs = executeUpdate(query, table);
		
		if (rs==0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
	}
	
	public boolean updateUserInfo(String user_id, double weight, String email) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to update user table with new weight and email");
		
		String query = "";
		if(weight != 0 && email != null)
			query = "UPDATE users SET weight = '"+weight+"', email = '"+email+"' WHERE ID_user= '"+user_id+"'";
		else if(weight != 0)
			query = "UPDATE users SET weight = '"+weight+"' WHERE ID_user= '"+user_id+"'";
		else if(email != null)
			query = "UPDATE users SET email = '"+email+"' WHERE ID_user= '"+user_id+"'";
		
		
		openConnection();
		
		String table = "users";
		int rs = executeUpdate(query, table);
		
		if (rs==0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
	}
	
	public boolean updateSensors(String resName, String bikeName, String value, String date, String user) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to update Sensors table");
		
		String query;
		resName = resName.replaceAll(" ", "");
		if(!resName.equals("Odometer"))
			query = "INSERT INTO sensors (`ID_bike`, `name`, `value`, `read_date`, `user`) VALUES ('"+bikeName+"', '"+resName+"', '"+value+"', '"+date+"', '"+user+"')";
		else 
			query = "UPDATE sensors SET value ='"+value+"' WHERE name='"+resName+"' AND ID_bike = '"+bikeName+"'";

		openConnection();
		
		int rs = executeUpdate(query, "sensors");
		
		if (rs == 0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
		
	}
	
	public boolean insertTrip(String resName, String bikeName, String value, String date, String user) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to inser a new trip "+resName+" for user "+user);
		
		String query;
		resName = resName.replaceAll(" ", "");
		query = "INSERT INTO trips (`ID_bike`, `name`, `value`, `time`, `user`) VALUES ('"+bikeName+"', '"+resName+"', '"+value+"', '"+date+"', '"+user+"')";

		openConnection();
		
		int rs = executeUpdate(query, "trips");
		
		if (rs == 0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
		
	}
	
	public boolean updateTrip(String resName, String bikeName, String value, String date, String user, int id_trip) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to update trips entry "+id_trip);
		
		String query;
		resName = resName.replaceAll(" ", "");
		query = "UPDATE trips SET name='"+resName+"', value= '"+value+"', time= '"+date+"' WHERE ID_trip = '"+id_trip+"'" ;

		openConnection();
		
		int rs = executeUpdate(query, "trips");
		
		if (rs == 0) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return false;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		closeConnection();
		return true;
		
	}
	
	public JSONObject getTrips(String user) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get all the trips for user "+user);
		
		String query = "SELECT COUNT(*) as tot, SUM(value) as KM, SUM(time) as milli  FROM trips WHERE user = '"+user+"' AND name ='End_Trip'";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		JSONObject json = new JSONObject();
		json.put("trips", rs.getInt("tot"));
		json.put("kilometers", rs.getInt("km"));
		json.put("time", rs.getInt("milli"));
		
		closeConnection();

		return json;
		
	}
	
	public JSONObject getLastTrip(String resName, String user) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get data from last "+user+" trip");
		
		String query = "SELECT * FROM trips WHERE name = '"+resName+"' AND user = '"+user+"' AND time IN (SELECT MAX(time) FROM trips WHERE name = '"+resName+"' AND user = '"+user+"')";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		JSONObject json;
		json = new JSONObject();
		json.put("id_entry", rs.getInt("ID_trip"));
		json.put("value", rs.getString("value"));
		json.put("BikeName", rs.getString("ID_bike"));
		json.put("date", rs.getString("time"));
			
		closeConnection();

		return json;
		
	}
	
	public JSONArray getLastSensorData(String resName) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get data from "+resName+" sensor");
		
		String query = "SELECT * FROM sensors WHERE name = '"+resName+"' AND read_date IN (SELECT MAX(read_date) FROM sensors WHERE name = '"+resName+"' GROUP BY ID_Bike)";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		//printStatus("SUCCESS --> query: "+query);
		
		JSONArray arr = new JSONArray();
		JSONObject json;
		do {
			json = new JSONObject();
			json.put("value", rs.getString("value"));
			json.put("BikeName", rs.getString("ID_bike"));
			
			arr.put(json);
		} while(rs.next());
		
		closeConnection();

		return arr;
		
	}

	public JSONArray getSensorData(String resName) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get data from "+resName+" sensor");
		
		String query = "SELECT * FROM sensors WHERE name = '"+resName+"' ORDER BY read_date LIMIT 100";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		//printStatus("SUCCESS --> query: "+query);
		
		JSONArray arr = new JSONArray();
		JSONObject json;
		do {
			json = new JSONObject();
			json.put("bike", rs.getString("ID_bike"));
			json.put("resource", rs.getString("name"));
			json.put("value", rs.getString("value"));
			json.put("date", rs.getString("read_date"));
			arr.put(json);
		} while(rs.next());
		
		closeConnection();

		return arr;
		
	}
	
	public JSONArray getSensorByBike(String resName, String bikeName) throws Exception{
		if(DEV_MOD)
			printStatus("Attempt to get data from "+resName+" sensor of bike "+bikeName);
		
		String query = "SELECT * FROM sensors WHERE name = '"+resName+"' AND ID_bike ='"+bikeName+"' ORDER BY read_date DESC LIMIT 100";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		JSONArray arr = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("bike", rs.getString("ID_bike"));
		json.put("resource", rs.getString("name"));
		json.put("value", rs.getString("value"));
		json.put("date", rs.getString("read_date"));
		arr.put(json);
		while(rs.next()) {
			JSONObject js = new JSONObject();
			js.put("bike", rs.getString("ID_bike"));
			js.put("resource", rs.getString("name"));
			js.put("value", rs.getString("value"));
			js.put("date", rs.getString("read_date"));
			arr.put(js);
		}
		
		closeConnection();

		return arr;
	}
	
	public JSONObject getLastRead(String resName, String bikeName) throws Exception {
		if(DEV_MOD)
			printStatus("Attempt to get last value for "+resName+" sensor");
		
		String query = "SELECT * FROM sensors WHERE name = '"+resName+"' AND ID_bike ='"+bikeName+"'";
		
		openConnection();
		
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			printStatus("FAIL --> query: "+query);
			return null;
		}
		
		printStatus("SUCCESS --> query: "+query);
		
		JSONObject json = new JSONObject();
		json.put("bike", rs.getString("ID_bike"));
		json.put("resource", rs.getString("name"));
		json.put("value", rs.getString("value"));
		json.put("date", rs.getString("read_date"));
		
		closeConnection();

		return json;
	}

}
