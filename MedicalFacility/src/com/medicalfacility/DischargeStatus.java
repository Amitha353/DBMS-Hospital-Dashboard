package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class DischargeStatus {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public int status() {
		int i = 1;
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Discharge Status \t");
		System.out.println("\t\t----------------------------------");
		String query = "select * from discharge";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();

			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (i = 1; i <= cols; i = i+2) {
					int dis_code = rs.getInt(i);
					String columnValue = rs.getString(i+1);
					System.out.print("\t\t["+dis_code + "]." +columnValue);
				}
				System.out.println("\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		i++;
		System.out.println("\t\t["+i+"]"+ ".Go Back\n");
		Scanner input = new Scanner(System.in);
		int casevar = input.nextInt();
		if (casevar != i) {
			return casevar;
		}
		return i;
		// send this back to the staff process patient page
	}
}
