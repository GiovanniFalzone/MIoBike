package it.unipi.iot.MIoBike.MIoBike_Utils;

public class main_test {
	public static void main(String args[]) {
		Bike_Manager bm = new Bike_Manager();
		bm.get_Last_Read("Bike1", "GPS");
		bm.get_Bike_Data("Bike1");
		bm.get_Bikes_Data();
		bm.get_all_Sensor_Data("Bike1", "Lock");
		bm.get_all_Sensor_Data("Bike1", "GPS");
	}
}
