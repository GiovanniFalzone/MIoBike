package it.unipi.iot.MIoBike.MIoBike_common;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import org.eclipse.californium.core.CoapClient;
import org.json.JSONObject;

public class Resource_Manager extends Thread{
	protected om2m_Node_Manager om2m_MN_manager;
	protected CoapClient my_coap_client;
	protected String Resource_uri;
	protected String ResourceName;
	protected String BikeName;
	protected String type;
	protected int period;
	protected JSONObject json_data;
	private boolean Sleeping_State;
	
	public Resource_Manager(String resource_uri, om2m_Node_Manager my_om2m_MN_manager, int my_period, String my_Bike_Name, String my_Resource_Name, String my_type) {
		if(DEV_MODE) {
			System.out.println("---------- Creating "+my_Bike_Name+"/"+my_Resource_Name+" -----------");
			System.out.println("ResUri: "+ resource_uri + " Type: "+my_type+" Period:" + my_period);
		}
		Resource_uri = resource_uri;
		BikeName = my_Bike_Name;
		period = my_period;
		ResourceName = my_Resource_Name;
		type = my_type;
		om2m_MN_manager = my_om2m_MN_manager;
		String []labels = {  };
		om2m_MN_manager.create_Container(BikeName, ResourceName, type,labels);
		json_data = new JSONObject();
		json_data.put(JSON_KEY_BIKENAME, BikeName);
		json_data.put(JSON_KEY_RESOURCENAME, ResourceName);
		json_data.put(JSON_KEY_DATE, "");
		json_data.put(JSON_KEY_RESOURCEDATA, new JSONObject());
		if(DEV_MODE) {
			System.out.println("---------------------------------------");
		}
	}

	public void set_period(int new_period) {
		if(DEV_MODE) {
			System.out.println("---------- Set "+new_period+" as Period for: "+BikeName+"/"+ResourceName+" -----------");
		}
		this.period = new_period;
		if(Sleeping_State && this.getState() == State.TIMED_WAITING) {
			this.interrupt();
		}
	}
	
	public void set_status(JSONObject json_con) {
	}	
	
	public void execute_Job() {
		if(DEV_MODE) {
			System.out.println("empty job");			
		}
	}
	
	public void run(){
		int iteration = 0;
		while(true) {
			try {
				Sleeping_State = true;
				if(period != DISABLED_FREQ_PERIOD) {
					Thread.sleep(period);
				} else {
					Thread.sleep(DEFAULT_POLLING_PERIOD);					
				} 
			} catch (InterruptedException e) { }
			Sleeping_State = false;
			if(period != DISABLED_FREQ_PERIOD) {
				if(DEV_MODE) {
					System.out.println("--------------"+BikeName+"/"+ResourceName+" Iteration " + iteration++ + "-------------------------------");
				}
				execute_Job();
				if(DEV_MODE) {
					System.out.println("---------------------------------------------------------------------------");
				}				
			}
		}
	}
}
