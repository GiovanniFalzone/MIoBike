package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Lock_Simulator extends CoapResource {
	private boolean lock_status; 
	public Lock_Simulator() {
		super("Lock");	
		lock_status = true;
		getAttributes().setTitle("Lock");
		setObservable(true);
	}

	public void handleGET(CoapExchange exchange) {
		String status = exchange.getQueryParameter("value");
		lock_status = Boolean.valueOf(status);
		System.out.println("received: "+ status);
		JSONObject response = new JSONObject();
		response.put("format", "boolean");
		response.put("value",  String.valueOf(lock_status));
		exchange.respond(response.toString());
	}

	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		String value = request.getString("value");
		System.out.println("Received new status: "+ value);
		lock_status = value.equals("true");
	}

}