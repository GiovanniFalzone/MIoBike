package it.unipi.iot.MIoBike.MIoBike_Utils;

public class Constants {
	public static final boolean IN_DEV_MODE = true;
	public static final boolean MN_DEV_MODE = true;
	public static final boolean LIBS_DEV_MODE = true;

	public static final int OPTION_ACCESS = 256;
	public static final int OPTION_TYPE = 267;
	public static final int TYPE_MN =	16;
	public static final int TYPE_AE =	2;
	public static final int TYPE_CONTAINER =	3;
	public static final int TYPE_CONTENT =	4;
	public static final int TYPE_SUBSCRIPTION =	23;
	
	
	public static final String URI_M2M_base_path = "/~";
	public static final String M2M_admin_credentials = "admin:admin";

	public static final int IN_port = 5683;
	public static final String Service_Name = "MIo-Bike";
	public static final String IN_uri = "coap://127.0.0.1:"+ IN_port;
	public static final String IN_Id = "/" + Service_Name + "-in-cse";
	public static final String IN_Name = Service_Name + "-in-name";
}
