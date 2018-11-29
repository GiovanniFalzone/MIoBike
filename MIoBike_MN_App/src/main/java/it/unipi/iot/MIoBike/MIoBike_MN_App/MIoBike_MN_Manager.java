package it.unipi.iot.MIoBike.MIoBike_MN_App;

import it.unipi.iot.MIoBike.MIoBike_common.Resource_Manager;
import it.unipi.iot.MIoBike.MIoBike_common.Actuator_Manager_periodic;
import it.unipi.iot.MIoBike.MIoBike_common.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_common.Sensor_Manager_sporadic;
import it.unipi.iot.MIoBike.MIoBike_common.Sensor_Manager_periodic;
import it.unipi.iot.MIoBike.MIoBike_common.om2m_Node_Manager;

import static it.unipi.iot.MIoBike.MIoBike_MN_App.MN_Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class MIoBike_MN_Manager extends om2m_Node_Manager {
	private om2m_Node_Manager IN_Manager;
	private MonitorThread NotificationManager;
	private Map<String, Resource_Manager> Sensors;
	private Map<String, Resource_Manager> Actuators;
	
	public String BikeName;
	public String MN_NM_NAME;
	public String MN_NM_url;

	
	public MIoBike_MN_Manager(String uri, String id, String name, String my_BikeName) {
		super(uri, id, name);
		BikeName = my_BikeName;
		MN_NM_NAME = BikeName + NM_NAME_EXTENSION;
		MN_NM_url = uri + "/" + MN_NM_NAME;
		if(DEV_MODE_MN) {
			System.out.println("-------------- Inizializing " + BikeName + "------------------");			
		}
		Create_NotificationManager();
		Create_om2m_IN_Manager();
		CreateBike();
		synch_init();
		if(DEV_MODE_MN) {
			System.out.println("-------------- End of Inizialization --------------------------");
		}
	}

	private void synch_init() {
		String labels [] = IN_Manager.get_labels_from(BikeName, "", "");
		String BikeStatus = "";
		String UserId = "";
		for(String s : labels) {
			if(s.contains(LABEL_PREFIX_BIKESTATUS)) {
				if(s.split(LABEL_PREFIX_BIKESTATUS).length > 1) {
					BikeStatus = s.split(LABEL_PREFIX_BIKESTATUS)[1];				
				}
			} else if(s.contains(LABEL_PREFIX_USERID)) {
				if(s.split(LABEL_PREFIX_USERID).length > 1) {
					UserId = s.split(LABEL_PREFIX_USERID)[1];
				}
			}

		}
		update_labels_in_AE(BikeName, BikeStatus, UserId, new String[0]);

		JSONObject content = IN_Manager.get_Last_Read(BikeName, SAMPLING_PERIOD);
		if(content.length() > 0) {
			Dispatch_Request(SAMPLING_PERIOD, content);
		}

		content = IN_Manager.get_Last_Read(BikeName, LOCK_NAME);
		if(content.length() > 0) {
			Dispatch_Request(LOCK_NAME, content);
		}
	}
	
	private void CreateBike() {
		Create_AE_Bike();
		Create_Virtual_Resources();
		Create_Sensors();
		Create_Actuators();
	}

	private void Create_Virtual_Resources() {
		add_virtual_resource(SAMPLING_PERIOD);
		add_virtual_resource(USER_RESOURCE_NAME);
	}
	
	private void Create_Sensors() {
		if(DEV_MODE_MN) {
			System.out.println("Creating Sensors for " + BikeName);			
		}
		Sensors = new HashMap<String, Resource_Manager>();
		add_sensor_event_task(NFC_URI, NFC_READER_NAME, NFC_TASK_PORT);
		add_sensor_event_task(LOCKER_URI, LOCKER_NAME, LOCKER_TASK_PORT);
		add_sensor_periodic_task(AIRQUALITY_URI, AIRQUALITY_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(GPS_URI, GPS_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(TEMPERATURE_URI, TEMPERATURE_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(HUMIDITY_URI, HUMIDITY_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(TYRE_PRESSURE_URI, TYRE_PRESSURE_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(ODOMETER_URI, ODOMETER_NAME, DEFAULT_POLLING_PERIOD);
		add_sensor_periodic_task(SPEEDOMETER_URI, SPEEDOMETER_NAME, DEFAULT_POLLING_PERIOD);
	}
	
	private void Create_Actuators() {
		Actuators = new HashMap<String, Resource_Manager>();
		add_actuator_periodic_task(LOCK_URI, LOCK_NAME, DEFAULT_POLLING_PERIOD);
	}
	
	private void add_virtual_resource(String ResName) {
		String [] labels = new String[] {};
		create_Container(BikeName, ResName, LABEL_VIRTUAL, labels);
	}
	
	private void add_sensor_periodic_task(String res_uri, String SensorName, int period) {
		Sensor_Manager_periodic res = new Sensor_Manager_periodic(res_uri, this, period, BikeName, SensorName, LABEL_SENSOR);
		Sensors.put(SensorName, res);
		active_wait_for_subscription("/"+BikeName+"/"+SensorName, IN_NM_NAME);
		res.start();
	}

	private void add_sensor_event_task(String res_uri, String SensorName, int port) {
		Sensor_Manager_sporadic res = new Sensor_Manager_sporadic(res_uri, this, BikeName, SensorName, port, LABEL_SENSOR);
		Sensors.put(SensorName, res);
		active_wait_for_subscription("/"+BikeName+"/"+SensorName, IN_NM_NAME);
		res.start();
	}
	
	private void add_actuator_periodic_task(String res_uri, String ActuatorName, int period) {
		Resource_Manager res = new Actuator_Manager_periodic(res_uri, this, period, BikeName, ActuatorName, LABEL_ACTUATOR);
		Actuators.put(ActuatorName, res);
		res.start();
	}	
	
	public void active_wait_for_subscription(String ResPath, String NM_Name) {
		if(DEV_MODE_MN) {
			System.out.println("Checking Subscription for: "+NM_Name+" to: " + ResPath);
		}
		int count = 0;
		while(!check_Node_Subscription(ResPath, NM_Name)) {
			count++;
			try {
					Thread.sleep(1000);
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt();
			}
			if(count == 10) {
				count = 0;
				if(DEV_MODE_MN) {
					System.out.println("Sending a subscription request");
				}
				IN_Manager.request_subscription(Node_Id, Node_uri, Node_Name, ResPath);
			}
		}
	}

	private void Create_om2m_IN_Manager() {
		IN_Manager = new om2m_Node_Manager(Node_uri, IN_ID, IN_NAME);
		// controllo che IN abbia fatto la subscription sul mio nodo in root
		active_wait_for_subscription("", IN_NM_NAME);
	}
	
	private void Create_NotificationManager() {
		String NotificationManager_name = MN_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, MN_NM_PORT, this);
		NotificationManager.start();
	}

	private void Create_AE_Bike() {
		String api = BikeName + "_api";
		String ResName = BikeName;
		String ResReach = "true";
		String [] labels = new String[] { };
		create_AE(api, ResName, ResReach, labels);
		active_wait_for_subscription("/"+ResName, IN_NM_NAME);
	}

	public void Dispatch_Request(String ResourceName, JSONObject json_con) {
		if(ResourceName.equals(SAMPLING_PERIOD)) {
			if(!Sensors.isEmpty()) {
				if(json_con.has(JSON_KEY_RESOURCEDATA)) {
					JSONArray data = json_con.getJSONArray(JSON_KEY_RESOURCEDATA);
					for(int i = 0; i<data.length(); i++) {
						JSONObject my_obj = data.getJSONObject(i);
						String Name = my_obj.getString(JSON_KEY_RESOURCENAME);
						String str_value = my_obj.getString(JSON_KEY_PERIOD);
						int value = Integer.parseInt(str_value);
						if(Sensors.containsKey(Name)) {
							Sensors.get(Name).set_period(value);
						} else if (Actuators.containsKey(Name)){
							Actuators.get(Name).set_period(value);
						}
					}
				}
			}
		}

		if(ResourceName.equals(LOCK_NAME)) {
			Actuators.get(ResourceName).set_status(json_con);
		}
	}
	
	public void Handle_new_AE_Notification(JSONObject ae) {
		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registration");
			System.out.println("---------------------------------------------------------------");
		}				

		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registered");
			System.out.println("---------------------------------------------------------------");
		}
	}

	public void Handle_new_Container_Notification(JSONObject cnt) {
		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registration");
			System.out.println("---------------------------------------------------------------");
		}

		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registered");
			System.out.println("---------------------------------------------------------------");
		}				
	}

	public void Handle_Content_Notification(JSONObject cin) {
		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registration");
			System.out.println("---------------------------------------------------------------");
		}
		JSONArray jarray = cin.getJSONArray(TAG_LBL);
		String [] labels = convert_json_array_field_to_string_array(jarray);		
		String ret [] = retrieve_UserId_BikeName_ResName_Type_from_lables(jarray);
		String UserId = ret[0];
		String BikeName = ret[1];
		String ResourceName = ret[2];	
		String Type = ret[3];
		String con = cin.getString(TAG_CON);
		String cnf = cin.getString(TAG_CNF);
		create_Content_Instance(UserId, BikeName, ResourceName, Type, cnf, con, labels);
		Dispatch_Request(ResourceName, new JSONObject(con));		
		if(DEV_MODE_MN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registered");
			System.out.println("---------------------------------------------------------------");
		}
	}

	
	public void Notification_Handler(String content) {
		if(DEV_MODE_MN) {
			System.out.println("-------------Notification Handler------------------------");
			System.out.println(content);
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
				} else if(json_rep.has("m2m:sub")) {
					json_notification_content = json_rep.getJSONObject("m2m:sub");
				}
				int ty = json_notification_content.getInt(TAG_TY);
	
				switch(ty) {
					case TYPE_MN:
						break;
					case TYPE_AE:
						Handle_new_AE_Notification(json_notification_content);
						break;
					case TYPE_CONTAINER:
						Handle_new_Container_Notification(json_notification_content);
						break;
					case TYPE_CONTENT:
						Handle_Content_Notification(json_notification_content);
						break;
					default:
						System.out.println("----------------Received bad Notification--------------");
						System.out.println(content);
						System.out.println("-------------------------------------------------------");
						break;
				}			
			}
			if(DEV_MODE_MN) {
				System.out.println("--------------End of notification Handler-----------------");
			}
		}
	}
	
}
