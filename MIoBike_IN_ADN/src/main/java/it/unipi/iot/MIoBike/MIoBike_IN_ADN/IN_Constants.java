package it.unipi.iot.MIoBike.MIoBike_IN_ADN;

import it.unipi.iot.MIoBike.MIoBike_Utils.Constants;

public class IN_Constants extends Constants{
	public static final boolean DEV_MODE = true;
	
	public static final int		IN_om2m_port = 5683;
	public static final String	IN_address = "coap://127.0.0.1";
	public static final String	IN_uri = IN_address+ ":" + IN_om2m_port;

	public static final String	IN_Id = "/" + Service_Name + "-in-cse";
	public static final String	IN_Name = "/" + Service_Name + "-in-name";

	public static final String	IN_NM_url = IN_address + ":" + IN_NM_port + "/" + IN_NM_NAME;
}
