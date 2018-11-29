package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class AirQuality_Simulator extends CoapResource {
	private String AirQuality = "80.5";
	public AirQuality_Simulator() {
		super("AirQuality");	
		getAttributes().setTitle("AirQuality");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "mge-3/m^3");
		response.put("value", AirQuality);
		exchange.respond(response.toString());
		System.out.println("New AirQuality: "+AirQuality);
	}

	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		AirQuality = request.getString("AirQuality");
		exchange.accept();
	}

}