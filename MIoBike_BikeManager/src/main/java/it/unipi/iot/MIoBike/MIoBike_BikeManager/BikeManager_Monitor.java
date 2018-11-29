package it.unipi.iot.MIoBike.MIoBike_BikeManager;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import java.io.IOException;

import it.unipi.iot.MIoBike.MIoBike_common.MonitorThread;

public class BikeManager_Monitor extends BikeManager{
	private MonitorThread NotificationManager;

	public class WebService_Update_Request extends Thread {
		String [] my_Queries = {};
		String ResourceName;
		
		public WebService_Update_Request(JSONObject json_con, String UserId) {
			if(json_con.length() > 0) {
				UserId = (UserId.length()==0)?"null":UserId;

				ResourceName = json_con.getString(JSON_KEY_RESOURCENAME);
				JSONObject ResData = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
				String query_1 = JSON_KEY_RESOURCENAME	+"="+ ResourceName;
				String query_2 = JSON_KEY_BIKENAME		+"="+ json_con.getString(JSON_KEY_BIKENAME);
				String query_3 = JSON_KEY_DATE			+"="+ json_con.getString(JSON_KEY_DATE);
				String query_4 = JSON_KEY_VALUE			+"="+ ResData.getString(JSON_KEY_VALUE);
				String query_5 = LABEL_PREFIX_USERID.replaceAll(":", "")	+"="+ UserId;
				String []Queries = new String [] { query_1, query_2, query_3, query_4, query_5 };
				my_Queries = Queries;
			}
		}

		public WebService_Update_Request(String What, String BikeName, String Date, String value, String UserId) {
			What = (What.length()==0)?"null":What;
			BikeName = (BikeName.length()==0)?"null":BikeName;
			Date = (Date.length()==0)?"null":Date;
			value = (value.length()==0)?"null":value;
			UserId = (UserId.length()==0)?"null":UserId;

			ResourceName = What;
			String query_1 = JSON_KEY_RESOURCENAME	+"="+ What;
			String query_2 = JSON_KEY_BIKENAME	+"="+ BikeName;
			String query_3 = JSON_KEY_DATE	+"="+ Date;
			String query_4 = JSON_KEY_VALUE		+"="+ value;
			String query_5 = LABEL_PREFIX_USERID.replaceAll(":", "")+"="+ UserId;
			String [] Queries = new String [] { query_1, query_2, query_3, query_4, query_5};
			my_Queries = Queries;
		}
		
		public void run() {
			if(	(ResourceName.equals(TEMPERATURE_NAME)) ||
				(ResourceName.equals(HUMIDITY_NAME)) 	||
				(ResourceName.equals(AIRQUALITY_NAME))  ||
				(ResourceName.equals(SPEEDOMETER_NAME)) ||
				(ResourceName.equals(ODOMETER_NAME)) 	||
				(ResourceName.equals(START_TRIP_LABEL)) ||
				(ResourceName.equals(END_TRIP_LABEL)))	{
				if(DEV_MODE) {
					String query = "";
					for(String s : my_Queries) {
						query += s + " ";
					}
					System.out.println("WebService Updater Thread working on: " + query);
				}
				try {
					get_request_http(WEBSERVICE_URI, my_Queries);
//					get_request_http("http://httpbin.org/get", my_Queries);
				} catch (ClientProtocolException e) {
					System.out.println("MIoBike WebService unreachable");
				} catch (IOException e) {
					System.out.println("MIoBike WebService unreachable");
				}
			}
		}
	}
	
	public BikeManager_Monitor() {
		super();
		if(DEV_MODE_BM) {
			System.out.println("--------- Bike Manager Initialization ----------------");
		}
		Create_NotificationManager();
		Subscribe_for_Bike_registration();
		Subscribe_to_each_Bike();
		Subscribe_to_Bikes_Sensors();
		if(DEV_MODE_BM) {
			System.out.println("--------------------------------------------");
		}
	}

	public void Subscribe_to_each_Bike() {
		Subscribe_to_each_AE(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[] { "Bike" });
	}
	
	public void Subscribe_to_Bikes_Sensors() {
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+NFC_READER_NAME });		
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+LOCKER_NAME });
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+TEMPERATURE_NAME });
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+HUMIDITY_NAME });
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+AIRQUALITY_NAME });
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+ODOMETER_NAME });
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ LABEL_PREFIX_RESOURCENAME+SPEEDOMETER_NAME });
	}
	
	public void Subscribe_for_Bike_registration() {
		Subscribe_to_Node_Root(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);
	}
	
	private void Create_NotificationManager() {
		String NotificationManager_name = BIKEMANAGER_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, BIKEMANAGER_NM_PORT, this);
		NotificationManager.start();
	}
	
	private void User_start_Trip(String BikeName, String UserId, String Date) {
		JSONObject con = get_Last_Read(BikeName, ODOMETER_NAME);
		JSONObject ResData = con.getJSONObject(JSON_KEY_RESOURCEDATA);
		String odometer_value = ResData.getString(JSON_KEY_VALUE);
		WebService_Update_Request ws_req = new WebService_Update_Request(START_TRIP_LABEL, BikeName, Date, odometer_value, UserId);
		ws_req.start();		
	}

	private void User_end_Trip(String BikeName, String UserId, String Date) {
		JSONObject con = get_Last_Read(BikeName, ODOMETER_NAME);
		JSONObject ResData = con.getJSONObject(JSON_KEY_RESOURCEDATA);
		String odometer_value = ResData.getString(JSON_KEY_VALUE);
		WebService_Update_Request ws_req = new WebService_Update_Request(END_TRIP_LABEL, BikeName, Date, odometer_value, UserId);
		ws_req.start();
	}
	
	
//-------------------------------------------------------------------
	public void Handle_new_MN_Notification(JSONObject csr) {
		if(DEV_MODE_BM) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN registration");
			System.out.println("---------------------------------------------------------------");
		}

		if(DEV_MODE_BM) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN registered");
			System.out.println("---------------------------------------------------------------");
		}
	}
//-------------------------------------------------------------------
	
	public void Handle_new_Bike_Notification(JSONObject ae) {
		if(DEV_MODE_BM) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Bike registration");
			System.out.println("---------------------------------------------------------------");
		}
		JSONArray jarray = ae.getJSONArray(TAG_LBL);
		String BikeName = retrieve_UserId_BikeName_from_lables(jarray)[1];
		Subscribe_to_AE(BikeName, BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);
	}

	public void Handle_new_Resource_Notification(JSONObject cnt) {
		if(DEV_MODE_BM) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Resource registration");
			System.out.println("---------------------------------------------------------------");
		}
		JSONArray jarray = cnt.getJSONArray(TAG_LBL);
		String [] UI_BN_RN_TY = retrieve_UserId_BikeName_ResName_Type_from_lables(jarray);
		String BikeName = UI_BN_RN_TY[1];
		String ResourceName = UI_BN_RN_TY[2];
		if(	(ResourceName.equals(NFC_READER_NAME))	||
			(ResourceName.equals(LOCKER_NAME)) 		||
			(ResourceName.equals(TEMPERATURE_NAME)) ||
			(ResourceName.equals(HUMIDITY_NAME)) 	||
			(ResourceName.equals(AIRQUALITY_NAME))  ||
			(ResourceName.equals(SPEEDOMETER_NAME)) ||
			(ResourceName.equals(ODOMETER_NAME))) {
			Subscribe_to_Container(BikeName, ResourceName, BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);			
		}
	}
	
	public void Handle_Content_Notification(JSONObject cin) {
		JSONObject json_con = new JSONObject(cin.getString("con"));
		if(json_con.has(JSON_KEY_RESOURCEDATA)) {
			String [] labels = retrieve_UserId_BikeName_from_lables(cin.getJSONArray(TAG_LBL));
			String UserId = labels[0];
			WebService_Update_Request ws_req = new WebService_Update_Request(json_con, UserId);
			ws_req.start();
			JSONObject ResData = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
			String Originator_BikeName = json_con.getString(JSON_KEY_BIKENAME);

			if(json_con.getString(JSON_KEY_RESOURCENAME).equals(NFC_READER_NAME)) {
				UserId = ResData.getString(JSON_KEY_VALUE);
				boolean ok = User_Unlock_Request(UserId, Originator_BikeName);
				if(ok) {
					User_start_Trip(Originator_BikeName, UserId, json_con.getString(JSON_KEY_DATE));
				}

			}else if(json_con.getString(JSON_KEY_RESOURCENAME).equals(LOCKER_NAME)) {
				ResData = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
				String BikeName = ResData.getString(JSON_KEY_VALUE);
				if(BikeName.equals(Originator_BikeName)) {
					boolean ok = User_Release_Bike(BikeName);
					if(ok) {
						User_end_Trip(BikeName, UserId, json_con.getString(JSON_KEY_DATE));
					}
				} else {
					System.out.println("Can't release "+BikeName + " via "+ Originator_BikeName);
				}
			}
		}
	}
	
	public void Notification_Handler(String content) {
		if(DEV_MODE_BM) {
			System.out.println("-------------System Notification-----------");
			System.out.println(content);
			System.out.println("-----------------------------------------");
			
		}
		JSONObject json_content = new JSONObject(content);
		JSONObject json_sgn = json_content.getJSONObject("m2m:sgn");
		if(json_sgn.has("m2m:sur")) {
			if(json_sgn.has("m2m:nev")) {
				JSONObject json_nev = json_sgn.getJSONObject("m2m:nev");
				JSONObject json_rep = json_nev.getJSONObject("m2m:rep");
				JSONObject json_notification_content = new JSONObject();
				if(json_rep.has("m2m:csr")) {
					json_notification_content = json_rep.getJSONObject("m2m:csr");								
				} else if(json_rep.has("m2m:ae")) {
					json_notification_content = json_rep.getJSONObject("m2m:ae");
				} else if(json_rep.has("m2m:cnt")) {
					json_notification_content = json_rep.getJSONObject("m2m:cnt");
				} else if(json_rep.has("m2m:cin")) {
					json_notification_content = json_rep.getJSONObject("m2m:cin");
				}
				int ty = json_notification_content.getInt("ty");
	
				switch(ty) {
					case TYPE_MN:
						Handle_new_MN_Notification(json_notification_content);
						break;
					case TYPE_AE:
						Handle_new_Bike_Notification(json_notification_content);
						break;
					case TYPE_CONTAINER:
						Handle_new_Resource_Notification(json_notification_content);
						break;
					case TYPE_CONTENT:
						Handle_Content_Notification(json_notification_content);
						break;
					default:
						if(DEV_MODE_BM) {
							System.out.println("----------------Received bad Notification--------------");
							System.out.println(content);
							System.out.println("-------------------------------------------------------");
						}
						break;
				}
			}
		}
	}

}
