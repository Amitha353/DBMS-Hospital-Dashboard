package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Reason {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public int Choose_Reason() {
		int rcode;
		try {
			String query = "select * from reason";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();

			int cols = rsmd.getColumnCount();
			for (int i = 1; i <= cols; i++) {
				System.out.print(rsmd.getColumnName(i) + " 	");
			}
			System.out.println("\n");
			System.out.println("--------------------------------------------------------\n");
			while (rs.next()) {

				for (int i = 1; i <= cols; i++) {
					String columnValue = rs.getString(i);
					System.out.print(columnValue + "	");
				}
				System.out.println("\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\t\t\tEnter the reason code:");
		// validate the code here
		Scanner input = new Scanner(System.in);
		rcode = input.nextInt();
		System.out.println("\n\t\t\t[1].Record \n\t\t\t[2].Go back\n");
		if (input.nextInt() == 1) {
			return rcode;
		}
		return 0;
	}
}
