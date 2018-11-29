package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class TyrePressure_Simulator extends CoapResource {
	private String pressure_front = "1.3";
	private String pressure_rear = "1.3";
	
	public TyrePressure_Simulator() {
		super("TyrePressure");	
		getAttributes().setTitle("TyrePressure");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		JSONObject press = new JSONObject();
		press.put("front", pressure_front);
		press.put("rear", pressure_rear);
		response.put("format", "bar");
		response.put("value", press);
		exchange.respond(response.toString());
		System.out.println("New TyrePressure: "+press);
	}

	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		pressure_front = request.getString("front");
		pressure_front = request.getString("rear");
	}

}