package it.unipi.iot.MIoBike.MIoBike_BikeManager_Simulator;
import it.unipi.iot.MIoBike.MIoBike_Utils.Bike_Manager;
import org.json.JSONArray;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

public class BikeManager_Simulator {
	public static void main( String[] args ) {
		Bike_Manager bm = new Bike_Manager();
		String BikeName = "Bike1";

//		bm.set_bike_period(BikeName, very_high_period, high_period, low_period, low_period, low_period, high_period, low_period, high_period);
//		bm.set_Bike_Lock(BikeName, true);
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		bm.set_Bike_Lock(BikeName, false);
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		bm.get_Last_Read(BikeName, GPS_NAME);
//		bm.get_Bike_Data(BikeName);
//		System.out.println(bm.get_Bikes_Data());
		
//		bm.get_all_Sensor_Data(BikeName, NFC_READER_NAME);
//		bm.get_all_Sensor_Data(BikeName, TEMPERATURE_NAME);
//		bm.get_all_Sensor_Data(BikeName, HUMIDITY_NAME);	
//		bm.get_all_Sensor_Data(BikeName, ODOMETER_NAME);
//		bm.get_all_Sensor_Data(BikeName, SPEEDOMETER_NAME);	
//		bm.get_all_Sensor_Data(BikeName, LOCK_NAME);
//		bm.get_all_Sensor_Data(BikeName, GPS_NAME);	
	
//		JSONArray jarray_locked = bm.get_locked_Bikes();
//		JSONArray jarray_unlocked = bm.get_unlocked_Bikes();
//		System.out.println(jarray_locked);
//		System.out.println(jarray_unlocked);
//		for(int i =0; i<jarray_locked.length(); i++) {
//			JSONObject obj = jarray_locked.getJSONObject(i);
//			String my_BikeName = obj.getString("BikeName");
//			bm.UnLock_Bike(my_BikeName);
//		}
//		for(int i =0; i<jarray_unlocked.length(); i++) {
//			JSONObject obj = jarray_unlocked.getJSONObject(i);
//			String my_BikeName = obj.getString("BikeName");
//			bm.Lock_Bike(my_BikeName);
//		}

//		JSONArray jarr;
//		jarr = bm.get_all_Sensor_Data(BikeName, NFC_READER_NAME);
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, TEMPERATURE_NAME);
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, HUMIDITY_NAME);	
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, ODOMETER_NAME);
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, SPEEDOMETER_NAME);	
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, LOCK_NAME);
//		System.out.println(jarr);
//		jarr = bm.get_all_Sensor_Data(BikeName, GPS_NAME);	
//		System.out.println(jarr);		
		
//		jarr = bm.get_Bikes_Data();
//		System.out.println(jarr);

	}
}
