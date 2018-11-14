package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class SubscriptionDataServlet
 */
@WebServlet("/SubscriptionDataServlet")
public class SubscriptionDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubscriptionDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DbManager db = new DbManager();
		
		int subs_ID = Integer.parseInt(request.getParameter("subscription_ID"));
		
		HashMap<String, Object> res = new HashMap<String, Object>();
		
		try {
			res = db.get_subscription(subs_ID);
			if(res==null) {
				response.getWriter().write("There is no subscription with id "+subs_ID);
				return;
			}
		} catch (Exception e) {
			response.getWriter().write("Error while performing query.");
			return;
		}
		
		JSONObject json_obj = new JSONObject();
		json_obj.put("activation_date", res.get("activation_date").toString());
		json_obj.put("expiration_date", res.get("expiration_date").toString());
		json_obj.put("balance", res.get("balance").toString());
		
		String json = json_obj.toString();
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
