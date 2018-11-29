package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

class Phy_Simulator extends CoapResource {
	BikeMonitor owner;
	public Phy_Simulator(BikeMonitor bm) {
		super("PhySim");
		owner = bm;
		getAttributes().setTitle("PhySim");
		setObservable(true);
	}

	public void handleGET(CoapExchange exchange) {
		String status = exchange.getQueryParameter("value");
		owner.handler(Boolean.parseBoolean(status));
		exchange.respond("PhySim stopped: "+status);
	}

}