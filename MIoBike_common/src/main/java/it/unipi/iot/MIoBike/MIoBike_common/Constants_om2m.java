package it.unipi.iot.MIoBike.MIoBike_common;

public class Constants_om2m {
	public static final String	PROTOCOL = "coap://";

	public static final String IN_ADDRESS =				"192.168.0.1";
	public static final String BIKEMANAGER_ADDRESS =	"192.168.0.1";
	
	public static final int PROXY_COAP_PORT	= 5680;
	public static final int PROXY_HTTP_PORT	= 8484;
	
	public static final int	IN_OM2M_PORT =	5683;
	public static final int	MN_OM2M_PORT =	5684;

	public static final int	IN_NM_PORT = 	5685;
	public static final int MN_NM_PORT = 	5686;
	
	public static final String M2M_CREDENTIALS = "admin:admin";
	
	public static final String	SERVICE_NAME =		"MIo-Bike";
	public static final String	BIKEMANAGER_NAME =	"BikeManager";

	public static final String	URI_M2M_BASE_PATH = "/~";
	public static final String	NM_NAME_EXTENSION = "_NM"; 
	public static final String	IN_NM_NAME = 		SERVICE_NAME + NM_NAME_EXTENSION;
	
	public static final String	BIKEMANAGER_NM_NAME = 	BIKEMANAGER_NAME + NM_NAME_EXTENSION;
	public static final int		BIKEMANAGER_NM_PORT = 	5700;
	public static final String	BIKEMANAGER_NM_URL = 	PROTOCOL + BIKEMANAGER_ADDRESS + ":" + BIKEMANAGER_NM_PORT + "/" + BIKEMANAGER_NM_NAME;	


	public static final String IN_URI = 	PROTOCOL + IN_ADDRESS+ ":" + IN_OM2M_PORT;
	public static final String IN_NODE_ID_EXTENSION = "-in-cse";
	public static final String IN_NODE_NAME_EXTENSION = "-in-name";
	
	public static final String MN_NODE_ID_EXTENSION = "-mn-cse";
	public static final String MN_NODE_NAME_EXTENSION = "-mn-name";
	
	public static final String	IN_ID = 	"/" + SERVICE_NAME + IN_NODE_ID_EXTENSION;
	public static final String	IN_NAME = 	"/" + SERVICE_NAME + IN_NODE_NAME_EXTENSION;

	public static final String	IN_NM_URL = PROTOCOL + IN_ADDRESS + ":" + IN_NM_PORT + "/" + IN_NM_NAME;

	public static final int REQUEST_TIMEOUT =	 0;	
	public static final int OPTION_ACCESS = 	256;
	public static final int OPTION_TYPE =		 267;
	public static final int TYPE_MN =			16;
	public static final int TYPE_AE =			2;
	public static final int TYPE_CONTAINER =	3;
	public static final int TYPE_CONTENT =		4;
	public static final int TYPE_SUBSCRIPTION =	23;

	
	public static final String TAG_PREFIX_M2M =	"m2m:";
	public static final String TAG_AE = 		"ae";	//	ae: Application Entity
	public static final String TAG_CNT = 		"cnt";	//	cnt: Container
	public static final String TAG_CIN = 		"cin";	//	cin: Content Instance
	public static final String TAG_SUB = 		"sub";	//	sub: Subscription
	public static final String TAG_SGN = 		"sgn";	//	sgn: Agregated Notification
	public static final String TAG_RN = 		"rn";	//	rn: Resource Name
	public static final String TAG_TY = 		"ty";	//	ty: Type
	public static final String TAG_RI = 		"ri";	//	ri: Resource ID
	public static final String TAG_PI = 		"pi";	//	pi: Parent Id
	public static final String TAG_ACPI =	 	"Acpi";	//	Acpi: Access Control Policies IDs
	public static final String TAG_URIL =	 	"uril";	//	uril: URI List
	public static final String TAG_CT = 		"ct";	//	ct: Creation Time
	public static final String TAG_ET = 		"et";	//	et: Expiration Time
	public static final String TAG_LT = 		"lt";	//	lt: Last Modified Time
	public static final String TAG_LBL =	 	"lbl";	//	lbl: Label
	public static final String TAG_CNF =	 	"cnf";	//	cnf: Content Format
	public static final String TAG_CON =	 	"con";	//	con: Content
	public static final String TAG_MNI =	 	"mni";	//	mni: Maximum Number of Instance
	public static final String TAG_ST = 		"st";	//	st: State Tag
	public static final String TAG_CS = 		"cs";	//	cs: Content Size
	public static final String TAG_AEI =	 	"aei";	//	aei: Application Entity Id
	public static final String TAG_API =	 	"api";	//	api: Application Id
	public static final String TAG_POA =	 	"poa";	//	poa: Point of Access
	public static final String TAG_RR = 		"rr";	//	rr: Request Reachability
	public static final String TAG_NEV =	 	"nev";	//	nev: Notification Event
	public static final String TAG_NEP =	 	"nep";	//	nep: Representation
	public static final String TAG_SUR =	 	"sur";	//	sur: Subscription URI
	public static final String TAG_ENC =	 	"Enc";	//	Enc: Event Notification Criteria


}
