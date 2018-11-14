package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;

import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

/**
 * Servlet implementation class CoapServlet
 */
@WebServlet("/CoapServlet")
public class CoapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //private om2m_Node_Manager in_man;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CoapServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        //String ret = in_man.get_all_AE().toString();
		om2m_Node_Manager in_man = new om2m_Node_Manager("coap://192.168.0.1:"+OM2M_port, IN_Id, IN_Name);
		String ret = in_man.get_all_AE().toString();
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(ret);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
