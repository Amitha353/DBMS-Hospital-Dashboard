package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class NegExperience {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public int nexperience() {
		String neg_desc = "";
		Scanner input = new Scanner(System.in);
		System.out.println("\n\t\t\t[1].Negative Experience Code \n\t\t\t[2].Go back\n");
		if (input.nextInt() == 1) {
			try {
				String query = "select * from neg_experience";
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

			System.out.println("\n\t\t\tEnter the code");
			// validate here
			int code = input.nextInt();
			// validation here
			return code;

		} else {

			return 0;
			// go back to staff patient report page page
		}

	}
}
