package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query4 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query IV \t");
		System.out.println();
		System.out.println("\t\t Facilities with no negative experience for cardiac symptoms");
		System.out.println("\t\t----------------------------------");
		String query = "select hospital.admit, fac.name from (select distinct res1.admit from (select distinct rep.report_id, "
				+ "pat.admit, cout.p_id, cout.neg_code, cout.neg_desc from checkout cout join reports rep on rep.p_id = cout.p_id "
				+ "join patients pat on pat.p_id = rep.p_id where rep.b_code = 'HRT000' and neg_code is null)res1 where res1.admit "
				+ " not in (select distinct res2.admit from (select distinct pat.admit, cout.p_id, cout.neg_code, cout.neg_desc from "
				+ "checkout cout join reports rep on rep.p_id = cout.p_id join patients pat on pat.p_id = rep.p_id where "
				+ "rep.b_code = 'HRT000' and neg_code is not null)res2))hospital join medical_facility fac on fac.fac_id = hospital.admit";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					int facility = rs.getInt(i);
					String fac_name = rs.getString(i + 1);
					System.out.println("\t\t" + facility + "\t" + fac_name);
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