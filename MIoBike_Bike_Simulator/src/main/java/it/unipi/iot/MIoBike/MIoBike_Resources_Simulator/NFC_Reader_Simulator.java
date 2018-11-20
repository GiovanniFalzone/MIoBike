package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

class NFC_Reader_Simulator extends Thread{
	String UserId;
	String NFC_NM_uri;
	CoapClient my_coap_client;
	public NFC_Reader_Simulator(String my_NFC_NM_uri, String User_Id) {
		NFC_NM_uri = my_NFC_NM_uri;	
		UserId = User_Id;
	}
	 
	public void Send_Request(String UserId) {
		String uri = NFC_NM_uri+"/?format=String&value="+UserId;
		System.out.println(uri);
		my_coap_client = new CoapClient(uri);
		CoapResponse response = my_coap_client.get();
		System.out.println(response.getResponseText());
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double rand = Math.random();
			System.out.println(rand);
			if(rand>0.5) {
				Send_Request(UserId);
			}
		}
	}

}