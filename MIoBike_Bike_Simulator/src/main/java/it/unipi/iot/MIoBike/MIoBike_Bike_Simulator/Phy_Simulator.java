package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.JSON_KEY_VALUE;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

class Phy_Simulator extends CoapResource {
	BikeMonitor owner;
	public Phy_Simulator(BikeMonitor bm) {
		super("PhySim");
		owner = bm;
		getAttributes().setTitle("PhySim");
		setObservable(true);
	}

	public void handlePUT(CoapExchange exchange) {
		String payload = exchange.getRequestText();
		JSONObject response = null;
		try {
	    	response = new JSONObject(payload);
	    } catch (JSONException exception_0) {
	    	System.out.println("not a json");
	    	exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
	    	return;
	    }
		String status = response.getString(JSON_KEY_VALUE);
		System.out.println("PhySimulator: " + status);
		owner.handler(Boolean.parseBoolean(status));
		exchange.respond(CoAP.ResponseCode.CHANGED);
	}

}