package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import it.unipi.iot.MIoBike.web.DbManager;

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
    
    @Override
   	public void init() throws ServletException {
   		System.out.println("----------- initializing Subs Servlet ------------");
   	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DbManager db = new DbManager();
		
		int subs_ID = Integer.parseInt(request.getParameter("subscription_ID"));
		
		JSONObject res = new JSONObject();
		
		try {
			res = db.getSubscription(subs_ID);
			if(res==null) {
				response.getWriter().write("There is no subscription with id "+subs_ID);
				return;
			}
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve subscription info");
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
		DbManager db = new DbManager();

		String userId = request.getParameter("userId");
		int balance = Integer.parseInt(request.getParameter("balance"));
		int new_balance;
		int subs_id;
		if(request.getParameter("subscription-type") == null) {
			new_balance = balance+Integer.parseInt(request.getParameter("recharge"));
			subs_id = Integer.parseInt(request.getParameter("subscriptionId"));
			try {
				db.updateUserBalance(userId, subs_id, new_balance);
			} catch (Exception e) {
				response.getWriter().write("Error while performing query to update user balance");
				return;
			}
		} else { 
			int type = Integer.parseInt(request.getParameter("subscription-type"));

			JSONObject json = new JSONObject();
			try {
				json = db.updateSubscription(userId, type);
			} catch (Exception e) {
				response.getWriter().write("Error while performing query to update subscription info");
				return;
			}
			
			//String res = (rs)?"true":"false";
			new_balance = balance - type;
			subs_id = json.getInt("subs_id");
			System.out.println(json);
			if(json.getInt("res")!=0) {
				try {
					db.updateUserBalance(userId, subs_id, new_balance);
				} catch (Exception e) {
					response.getWriter().write("Error while performing query to update user balance");
					return;
				}
			}
		}
			
		HttpSession session = request.getSession(true);
		session.setAttribute("balance", new_balance);
		session.setAttribute("subscriptionId", subs_id);
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.sendRedirect("web-UI/dashboard.jsp");
	    //response.getWriter().write("ciao");
	}

}
