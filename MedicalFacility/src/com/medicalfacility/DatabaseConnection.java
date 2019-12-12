package com.medicalfacility;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
	private static final String jdbcURL = "jdbc:oracle:thin:@localhost:1521:xe";
	private Connection connection = null;

	public DatabaseConnection() {
		createConnection();
	}

	public Connection getConnection() {
		return connection;
	}

	private void createConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String user = "homeuser";
			String passwd = "password2";
			connection = DriverManager.getConnection(jdbcURL, user, passwd);
		} catch (Throwable oops) {
			oops.printStackTrace();
		}
	}

	public void close() {
		if (connection != null) {
			try {
				System.out.println("Closing connection....");
				connection.close();
			} catch (Throwable whatever) {
			}
		}
	}
}
