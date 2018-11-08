package it.unipi.iot.MIoBike.MIoBike_IN_ADN;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_DEV_MODE;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;

/*questo deve tenere tutte le MN ovvero le bici come classi nodo e gestire tutto il IN come riprendi il dato, ridondanza
 * recupero del dato, sync, registrazione, notifica etc*/
public class MIoBike_IN_Manager extends om2m_Node_Manager{
	private List<om2m_Node_Manager> Bikes;
	
	public MIoBike_IN_Manager(String uri, String Id, String Name) {
		super(uri, Id, Name);
		Bikes = new ArrayList<om2m_Node_Manager>();
		JSONArray MNs_array = get_all_MN();
		for(int i=0; i< MNs_array.length(); i++) {
			JSONObject MN = MNs_array.getJSONObject(i);
			String uri_path = MN.getString("uri");
			String bike_uri = get_label_from_path(uri_path, "poa");
			bike_uri = Convert_HTTP_URI_To_CoAP_URI(bike_uri);
			String bike_id = get_label_from_path(uri_path, "csi");
			String bike_name = bike_id.replaceAll("cse", "name");
			om2m_Node_Manager Bike = new om2m_Node_Manager(bike_uri, bike_id, bike_name);
			if(IN_DEV_MODE) {
				System.out.println("New Bike Manager: " + bike_uri + " " + bike_id + " " + bike_name);
			}
			Bikes.add(Bike);
		}
	}
	
	public void print_all_containers_tree() {
		for(om2m_Node_Manager Bike : Bikes) {
			JSONArray containers = Bike.get_all_Container();
			System.out.println("--------------------------------------");
			System.out.println(containers.toString());
			System.out.println("--------------------------------------");
		}
	}
}
