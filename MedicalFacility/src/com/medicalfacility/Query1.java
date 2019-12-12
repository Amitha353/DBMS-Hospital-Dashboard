package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query1 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query I \t");
		System.out.println();
		System.out.println("\t\t Patients discharged but had negative experience");
		System.out.println("\t\t----------------------------------");
		String query = "select distinct pt.f_name || ' ' || pt.l_name as Patient_Name, pt.admit as Facility, "
				+ "prc.checkin_start as CheckIn_Date, cout.neg_desc as Negative_Desc from checkout cout join patients pt "
				+ "on pt.p_id = cout.p_id join process prc on prc.p_id = pt.p_id where cout.checkout_time is not null"
				+ " and (cout.neg_code is not null and cout.neg_desc is not null)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 4) {
					String pt_name = rs.getString(i);
					String facility = rs.getString(i + 1);
					String checkindate = rs.getString(i + 2);
					String negativeDesc = rs.getString(i + 3);
					System.out.println("\t\t"+pt_name + "\t" + facility + "\t" + checkindate + "\t" + negativeDesc);
				}
			}
			connection.close();
			DemoQueries queries = new DemoQueries();
			queries.dataVerify();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
