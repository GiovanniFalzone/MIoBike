package it.unipi.iot.MIoBike.web.servlets;

import static it.unipi.iot.MIoBike.web.Constants_web.CONNECTED;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import it.unipi.iot.MIoBike.MIoBike_BikeManager.BikeManager;

/**
 * Servlet implementation class PeriodicMovingBikeServlet
 * 	polling for unlocked bikes, to retrieve their position change
 */
@WebServlet("/PeriodicMovingBikesServlet")
public class PeriodicMovingBikesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PeriodicMovingBikesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		BikeManager bm = new BikeManager();

		int period = 1000;
		//Runtime runtime = Runtime.getRuntime();
		while (true) { // this is only for demonstration purpose, don't do this! Use asynchronous APIs instead
			try {
				String bikeArray = null;
				JSONArray json_array = null;
				json_array = bm.get_unlocked_Bikes();
				
				if(json_array.length() == 0)
					period = 10000;
				else
					period = 1000;
				
				printWriter.write("data: " + json_array.toString() + "\n\n");
				printWriter.flush();
				response.flushBuffer();
				//Do not close the writer!
				//Gives the locked bike every 10 seconds
				Thread.sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
