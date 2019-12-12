package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query2 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query II \t");
		System.out.println();
		System.out.println("\t\t Facilities with no negative experience reported - 250 days");
		System.out.println("\t\t----------------------------------");
		String query = "select hospital.admit as facility, med.name from (select distinct res1.admit from (select pt.admit, cout.* from checkout cout " + 
				"join patients pt on pt.p_id = cout.p_id where cout.checkout_time > sysdate - 250 and cout.neg_code is null and " +
				"cout.neg_desc is null) res1 where res1.admit not in (select distinct res2.admit from (select pt.admit, cout.* from " +
				"checkout cout join patients pt on pt.p_id = cout.p_id where cout.checkout_time > sysdate - 250 and cout.neg_code is " +
				"not null and cout.neg_desc is not null)res2)) hospital join medical_facility med on med.fac_id = hospital.admit";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					String facility = rs.getString(i);
					String name = rs.getString(i + 1);
					System.out.println("\t\t"+facility + "\t" + name);
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
