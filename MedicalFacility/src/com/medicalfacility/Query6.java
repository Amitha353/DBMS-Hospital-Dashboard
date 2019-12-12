package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query6 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query VI \t");
		System.out.println();
		System.out.println("\t\t Waiting phase duration of patients in medical facility");
		System.out.println("\t\t----------------------------------");
		String query = "select * from (select res.ptname as patient, res.cdate, res.admit as facility, res.duration, res.reportids,\r\n"
				+ "RANK() OVER (PARTITION BY res.admit ORDER BY duration desc) as reportRank from\r\n"
				+ "(SELECT prc.process_id, pt.admit, pt.f_name || ' ' || pt.l_name as ptname, pt.dob, to_timestamp(prc.checkin_start) as cdate,\r\n"
				+ "prc.p_id, prc.reportids, to_timestamp(prc.tmt_start) - to_timestamp(prc.checkin_start) AS duration\r\n"
				+ "FROM process prc JOIN patients pt ON prc.p_id = pt.p_id)res) where reportRank <= 5";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 6) {
					String pat = rs.getString(i);
					String cdate = rs.getString(i + 1);
					int facility = rs.getInt(i + 2);
					String duration = rs.getString(i + 3);
					String reportids = rs.getString(i + 4);
					int reportRank = rs.getInt(i + 5);
					System.out.println(
							"\t\t" + pat + "\t" + facility + "\t" + duration + "\t" + reportRank + "\t" + cdate);
					if (reportids != null) {
						reportData(reportids);
					}
				}
			}
			connection.close();
			DemoQueries queries = new DemoQueries();
			queries.dataVerify();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reportData(String reportIds) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String reportQuery = "select res.sym_code, sym.name from (select sym_code from reports where report_id in ( "
				+ "select distinct regexp_substr('" + reportIds + "','[^, ]+', 1, level) from dual connect by "
				+ "regexp_substr('" + reportIds
				+ "', '[^, ]+', 1, level) is not null)) res join symptoms sym on sym.sym_code = res.sym_code";
		System.out.println();
		System.out.println("\t\t-------------------------------------");
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(reportQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					String symCode = rs.getString(i);
					String symName = rs.getString(i + 1);
					System.out.println("\t\t" + symCode + "\t\t" + symName);
				}
			}
			System.out.println("\t\t-------------------------------------");
			System.out.println();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
