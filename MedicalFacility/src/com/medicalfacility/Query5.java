package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query5 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query V \t");
		System.out.println();
		System.out.println("\t\tFacilities with most number of negative experiences");
		System.out.println("\t\t----------------------------------");
		String query = "select hospital.facility, fac.name from (select res.facility, res.counter from (select res.admit as facility, "+
				"count(res.neg_code) as counter from (select pat.admit, cout.emp_id, cout.p_id, cout.neg_code, cout.neg_desc from checkout "+
				"cout join patients pat on pat.p_id = cout.p_id where neg_code is not null and neg_desc is not null)res group by res.admit "+
				"order by counter desc)res where rownum < 2)hospital join medical_facility fac on fac.fac_id = hospital.facility";
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
