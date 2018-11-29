/*
 * Some constants utilities for servlets and DbManager
 */
package it.unipi.iot.MIoBike.web;

public class Constants_web {
	public static final boolean DEV_MOD = true;
	public static final boolean CONNECTED = true;
	
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "jdbc:mysql://localhost:3306/MIoBike_db";


	public static void printStatus(String toprint) {
		System.out.println(toprint);
		System.out.println("------------------------------------------------------------------------------");
	}
}
