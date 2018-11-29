package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import java.net.SocketException;
import java.util.Random;
import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_Phy_Simulators.PosGen;

public class Bike_Simulator extends BikeMonitor{
	private static String BikeName;
	private boolean Stop_Motion = true;
	private long secondsFromMinTemp;
	private Phisical_Simulator phy_sim;

	public Bike_Simulator(String BikeName, long start_time) throws SocketException {
		super(BIKE_JAVA_SIMULATOR_PORT);
		super.addEndpoints();
    	super.start();
    	secondsFromMinTemp = start_time;
		phy_sim = new Phisical_Simulator();
		phy_sim.start();
	}
	
	public static void main( String[] args ) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 4);			// hour with minimum temperature
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long passed = now - c.getTimeInMillis();
        long secondsPassed = passed / 1000;

		if(args.length == 1) {
				BikeName = args[0];
				try {
					Bike_Simulator bs = new Bike_Simulator(BikeName, secondsPassed);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else {
			System.out.println("Please inert the Bike's Name");
		}
	}

	@Override
	public void handler(boolean value) {
		Stop_Motion = value;
		System.out.println("Motion Phy Simulation status: "+value);
	}

	public class Phisical_Simulator extends Thread{
		public PosGen Motion_Phy_Sim = new PosGen();
		public double rad_step = Math.toRadians((360/(60*60*24)));
        public double iteration_rad = secondsFromMinTemp * rad_step;
        public double iteration_rad_slow = iteration_rad;

		public void run() {
			while(true) {
				try {
					Thread.sleep(PHY_SIMULATORS_PERIOD);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Motion_Phy_Sim.update();
				iteration_rad = (iteration_rad + rad_step)%(Math.toRadians(360));
				iteration_rad_slow = ((iteration_rad_slow + rad_step)*0.1)%(Math.toRadians(360));
//				Simulate_NFC();
//				Simulate_Locker();
				Simulate_AirQuality();
				Simulate_Humidity();
				Simulate_Temperature();
				Simulate_TyrePressure();
				if(!Stop_Motion) {
					Simulate_Motion();
				}
			}
		}

		public void Send_value(String payload, String uri) {
			Request req = new Request(Code.POST);
			req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
			req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
			if(payload.length()>0) {
				req.setPayload(payload);
			}
			CoapClient client = new CoapClient(uri);
			client.setTimeout(REQUEST_TIMEOUT);
			CoapResponse response = client.advanced(req);
		}
		
		public void Send_value(String uri) {
			CoapClient my_coap_client = new CoapClient(uri);
			CoapResponse response = my_coap_client.get();
		}
		
		public void Simulate_Locker() {
			if(PHY_SIMULATOR_LOCKER) {
				Random rand = new Random();
				float prob = rand.nextFloat();
				if(prob >= 0.9) {
					String Task_NM_name = LOCKER_NAME + NM_NAME_EXTENSION;
					String query = "/"+Task_NM_name+"?format=String&value="+BikeName;
					String URI = LOCAL_URI + LOCKER_TASK_PORT + query;
					Send_value(URI);
					System.out.println("Trying to release: "+ BikeName+"   "+URI);
				}
			}
		}

		public void Simulate_NFC() {
			if(PHY_SIMULATOR_NFC) {
				Random rand = new Random();
				float prob = rand.nextFloat();
				if(prob >= 0.999) {
					int id = rand.nextInt(TESTERS.length);
					String UserId = TESTERS[id];
					String Task_NM_name = NFC_READER_NAME + NM_NAME_EXTENSION;
					String query = "/"+Task_NM_name+"?format=String&value="+UserId;
					String URI = LOCAL_URI + NFC_TASK_PORT + query;
					Send_value(URI);
					System.out.println("User: " + UserId+" trying to take "+ BikeName+"   "+URI);
				}
			}
		}
		
		public void Simulate_Motion() {
			if(PHY_SIMULATOR_MOTION) {
				if(COOJA) {
					String payload = Motion_Phy_Sim.get_post_payload();
					if(DEV_MODE) {
						System.out.println("Update Motion cooja: "+ payload);
					}
					Send_value(payload, GPS_URI);
				} else {
					JSONObject content = Motion_Phy_Sim.get_post_payload_JSON();
					JSONObject payload = new JSONObject();
					String latitude = ""+content.get("lat");
					String longitude = ""+ content.get("long");
					payload.put("latitude", latitude);
					payload.put("longitude", longitude);
					Send_value(payload.toString(), GPS_URI);

					payload = new JSONObject();
					String dinstance = ""+content.get("km");
					payload.put("distance", dinstance);
					Send_value(payload.toString(), ODOMETER_URI);
					
					payload = new JSONObject();
					String speed = ""+content.get("vel");
					payload.put("speed", speed);
					Send_value(payload.toString(), SPEEDOMETER_URI);
					if(DEV_MODE) {
						System.out.println("Update Motion Java sim: "+ payload);
					}
				}
			}
		}
		
		public void Simulate_AirQuality() {
			if(PHY_SIMULATOR_AIRQUALITY) {
				String label = "AirQuality";
				String uri = AIRQUALITY_URI;
				JSONObject payload = new JSONObject();
				Random rand = new Random();
				float prob = rand.nextFloat();
				double base_value = 15.00;
				double max_excursion = 2;
				double max_variability = 0.5;
				double value = base_value + max_excursion*Math.cos(iteration_rad - Math.toRadians(90)) + max_variability*(1-prob);
				String str_value = String.format("%.2f", value);
				payload.put(label, str_value);
				Send_value(payload.toString(), uri);
			}
		}

		public void Simulate_Humidity() {
			if(PHY_SIMULATOR_HUMIDITY) {
				String label = "humidity";
				String uri = HUMIDITY_URI;
				JSONObject payload = new JSONObject();
				Random rand = new Random();
				float prob = rand.nextFloat();
				double base_value = 70.00;
				double max_variability = 5;
				double max_excursion = 20 - max_variability;
				double value = base_value + max_excursion*Math.sin(iteration_rad - Math.toRadians(90)) + max_variability*(1-prob);
				String str_value = String.format("%.2f", value);
				payload.put(label, str_value);
				Send_value(payload.toString(), uri);
			}
		}

		public void Simulate_Temperature() {
			if(PHY_SIMULATOR_TEMPERATURE) {
				String label = "temperature";
				String uri = TEMPERATURE_URI;
				JSONObject payload = new JSONObject();
				Random rand = new Random();
				float prob = rand.nextFloat();
				double base_value = 20.00;
				double max_variability = 1;
				double max_excursion = 5 - max_variability;
				double value = base_value + max_excursion*Math.sin(iteration_rad - Math.toRadians(90)) + max_variability*(1-prob);
				String str_value = String.format("%.2f", value);
				payload.put(label, str_value);
				Send_value(payload.toString(), uri);
			}
		}

		public void Simulate_TyrePressure() {
			if(PHY_SIMULATOR_TYRE_PRESSURE) {
				String uri = TYRE_PRESSURE_URI;
				JSONObject payload = new JSONObject();
				Random rand = new Random();
				float prob = rand.nextFloat();
				double base_value = 2.5;
				double max_variability = 0.4;
				double max_excursion = 1.5 - max_variability;
				double value = base_value + max_excursion*Math.sin(iteration_rad_slow) + max_variability*(1-prob);
				String str_value = String.format("%.2f", value);
				payload.put("front", str_value);
				payload.put("rear", str_value);
				Send_value(payload.toString(), uri);
			}
		}	
	}	
		
}
