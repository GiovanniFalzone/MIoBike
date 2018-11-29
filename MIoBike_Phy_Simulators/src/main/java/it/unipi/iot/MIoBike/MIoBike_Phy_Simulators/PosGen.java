package it.unipi.iot.MIoBike.MIoBike_Phy_Simulators;
// around Pisa the GPS coordinates are (43,...... ; 10,......); thus we are interested about the 6 decimal digits (we
// consider them as integers with 6 digits)

// we ignore earth sphericity (we are interested about small distances)

// for longitudine: about 12 units for one meter
// for latitude: about 9 units for one  meter
// approximating, we assume for both 10 units for one meter

import java.util.*;

import org.json.JSONObject;

import java.lang.*;

public class PosGen {
	// initial position
	public int x0;
	public int y0;

	// borders of the reachable area
	public int xlimit_inf;
	public int xlimit_sup;
	public int ylimit_inf;
	public int ylimit_sup;

	// actual position
	private int x;
	private int y;

	// last movement
	private int last_mov = -1;

	// number of consecutive movements (steps in the same direction)
	private int consec_mov = 0;

	private int dx;
	private int dy;

	// speed and total distance
	private double speed = 0.0; // expressed in km/h
	private double total_dist = 0.0; // expressed in km

	public PosGen() {
		// Pisa Centrale coordinates
		int x0 = 398309;
		int y0 = 708894;

		// constants chosen to remain always close to the city
		int xlimit_inf = 347348;
		int xlimit_sup = 461406;
		int ylimit_inf = 680262;
		int ylimit_sup = 747431;

		this.x0 = x0;
		this.y0 = y0;
		this.xlimit_inf = xlimit_inf;
		this.xlimit_sup = xlimit_sup;
		this.ylimit_inf = ylimit_inf;
		this.ylimit_sup = ylimit_sup;
		this.x = x0;
		this.y = y0;
	}

	// returning random int between 1 and 9
	private int rand_1_9() {
		Random random = new Random();
		int max_int = 9;
		int k = 1 + random.nextInt(max_int);
//		System.out.println("randomly chosen " + k + "\n");
		if (k == 10)
			k = 5;
		return k;
	}

	// piecewiese linear function
	// knowing the number of steps done in the same direction returns the probability of remaining in the same direction
	// or to choose randomly a direction
	private double plfunc() {
//		if (consec_mov == 0)
//			return 0.0;
		if (consec_mov < 100) // first 100 steps
			return 0.99;
		if (consec_mov < 150) // first 150 steps
			return 0.9;
		if (consec_mov < 200) // first 200 steps
			return 0.7;
		return 0.0;
	}

	// returning int between 1 and 9 considering the previous steps
	private int rand_mov() {
		Random random = new Random();
		double f = (double) random.nextFloat();
		if (f < plfunc()) {
			return last_mov;
		}
		else {
			return rand_1_9();
		}
	}

	// choosing direction considering also the past
	private int choose_dir() {
		if (last_mov == -1)
			return rand_1_9();

		return rand_mov();
	}

	private void update_last_mov_and_consec_mov(int n) {
		if (last_mov == n && last_mov != 5)
			consec_mov++;
		else
			consec_mov = 0;
		last_mov = n;
	}

	private void move() {
		if (Math.abs(x - xlimit_inf) > 100 && Math.abs(x - xlimit_sup) > 100 && Math.abs(y - ylimit_inf) > 100 && Math.abs(y - ylimit_sup) > 100) {
//			System.out.println("FAR FROM THE BORDERS!\n");
			dx = dx * (new Random().nextInt(30) + 10);
			dy = dy * (new Random().nextInt(30) + 10);
			x = x + dx;
			y = y + dy;
		}
		else { // it goes towards the center
//			System.out.println("CLOSE TO THE BORDERS!\n");
			long dir_x = x0 - x;
			long dir_y = y0 - y;
			dx = (int) (dir_x * 50 / Math.sqrt(dir_x*dir_x + dir_y*dir_y));
			dy = (int) (dir_y * 50 / Math.sqrt(dir_x*dir_x + dir_y*dir_y));

			int lm = 5; // understanding the direction of the controlled movement
			if (dx < 0) {
				if (dy < 0)
					lm = 7;
				if (dy == 0)
					lm = 4;
				if (dy > 0)
					lm = 1;
			}
			if (dx == 0) {
				if (dy < 0)
					lm = 8;
				if (dy == 0)
					lm = 5;
				if (dy > 0)
					lm = 2;
			}
			if (dx > 0) {
				if (dy < 0)
					lm = 9;
				if (dy == 0)
					lm = 6;
				if (dy > 0)
					lm = 3;
			}

			x = x + dx;
			y = y + dy;

			last_mov = lm;
			consec_mov = 1;
		}
	}

	private void update_speed_and_total_dist() {
		double sp = Math.sqrt(dx*dx + dy*dy); // expressed in gps units / second
		sp = sp * 0.1; // expressed in m/s
		speed = sp * 3.6; // expressed in km/h
//		System.out.println("velocita calcolata: " + speed + "\n");

		double step_len = Math.sqrt(dx*dx + dy*dy); // expressed in gps units
		step_len = step_len * 0.1; // expressed in m
		step_len = step_len * 0.001; // expressed in km
		total_dist += step_len;
//		System.out.println("distanza calcolata: " + total_dist + "\n");
	}

	public void update() {
		int direction = choose_dir();
//		System.out.println("direction chosen: " + direction + "\n");
		switch (direction) {
			case 1:
				dx = -1;
				dy = 1;
				update_last_mov_and_consec_mov(1);
				break;
			case 2:
				dx = 0;
				dy = 1;
				update_last_mov_and_consec_mov(2);
				break;
			case 3:
				dx = 1;
				dy = 1;
				update_last_mov_and_consec_mov(3);
				break;
			case 4:
				dx = -1;
				dy = 0;
				update_last_mov_and_consec_mov(4);
				break;
			case 5:
				// no movement
				dx = 0;
				dy = 0;
				update_last_mov_and_consec_mov(5);
				break;
			case 6:
				dx = 1;
				dy = 0;
				update_last_mov_and_consec_mov(6);
				break;
			case 7:
				dx = -1;
				dy = -1;
				update_last_mov_and_consec_mov(7);
				break;
			case 8:
				dx = 0;
				dy = -1;
				update_last_mov_and_consec_mov(8);
				break;
			case 9:
				dx = 1;
				dy = -1;
				update_last_mov_and_consec_mov(9);
				break;
		}
		move();
		update_speed_and_total_dist();
	}

	public int get_x() {
		return x;
	}

	public int get_y() {
		return y;
	}

	public double get_latitude() {
		int y = get_y();
		String str_latit = "43." + Integer.toString(y);
		double latit = Double.parseDouble(str_latit);
		return latit;
	}

	public double get_longitude() {
		int x = get_x();
		String str_longit = "10." + Integer.toString(x);
		double longit = Double.parseDouble(str_longit);
		return longit;
	}

	public String get_post_payload() {
		// the format of the POST (sent to GPS resource) payload must be of this type:
		// "lat=123456;long=123456;km=12345;vel=12"

		// the total_distance saturates at 99999, while the speed at 99

		// total_dist string
		int td = (int) total_dist;
		String std;
		if (td < 10)
			std = "0000" + Integer.toString(td);
		else {
			if (td < 100)
				std = "000" + Integer.toString(td);
			else {
				if (td < 1000)
					std = "00" + Integer.toString(td);
				else {
					if (td < 10000)
						std = "0" + Integer.toString(td);
					else {
						if (td < 100000)
							std = Integer.toString(td);
						else
							std = "99999";
					}
				}
			}
		}

		// speed string
		int v = (int) speed;
		String sv;
		if (v < 10)
			sv = "0" + Integer.toString(v);
		else {
			sv = Integer.toString(v % 100);
		}

		String pl = "lat=43." + Integer.toString(get_y()) + ";long=10." + Integer.toString(get_x()) +
			";km=" + std + ";vel=" + sv;
		return pl;
	}


	public JSONObject get_post_payload_JSON() {
		// JSON format: {"lat":123456,"long":123456,"km":12345,"vel":12}
		String s = get_post_payload();
		JSONObject payload = new JSONObject();
		String [] res = s.split(";");
		payload.put("lat", res[0].split("=")[1]);
		payload.put("long", res[1].split("=")[1]);
		payload.put("km", res[2].split("=")[1]);
		payload.put("vel", res[3].split("=")[1]);
		return payload;
	}
}

