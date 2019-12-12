package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RecordSymptoms {
	public int recordSymptom(Credential patient, Reports patientReport, String symptom) {
		int reportid = 0;
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String symQuery = "Insert into Reports values (ReportIDSequence.nextval," + patient.pt_id + ",'" + symptom
				+ "','" + patientReport.firstOccur + "'," + patientReport.duration + ",'" + patientReport.reason + "','"
				+ patientReport.severity + "','" + patientReport.bodyPart + "',null)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(symQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String patQuery = "select res.report_id from (select report_id from reports where p_id = " + patient.pt_id
				+ " order by report_id desc)res where rownum < 2";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(patQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					reportid = rs.getInt(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reportid;
	}
}
