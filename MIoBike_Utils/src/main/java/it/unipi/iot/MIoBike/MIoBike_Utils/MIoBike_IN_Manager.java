package it.unipi.iot.MIoBike.MIoBike_Utils;

/*questo deve tenere tutte le MN ovvero le bici come classi nodo e gestire tutto il IN come riprendi il dato, ridondanza
 * recupero del dato, sync, registrazione, notifica etc*/
public class MIoBike_IN_Manager extends om2m_Node_Manager{

	public MIoBike_IN_Manager(String uri, String Id, String Name) {
		super(uri, Id, Name);
	}
}
