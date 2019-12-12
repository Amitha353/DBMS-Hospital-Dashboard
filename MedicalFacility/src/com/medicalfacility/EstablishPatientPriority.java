package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

public class EstablishPatientPriority {
	public void setPriority(int processID, Map<Integer, Integer> assessmentRules, Map<Integer, Integer> reportRules) {
		String status = updateStatus(processID, assessmentRules, reportRules);
		updateFinal(processID, status);
	}

	public String updateStatus(int processId, Map<Integer, Integer> assessments, Map<Integer, Integer> reportRules) {
		String status = "Normal";
		for (Map.Entry<Integer, Integer> val : reportRules.entrySet()) {
			int key = val.getKey();
			if (key != 0) {
				int val1 = assessments.get(key);
				int val2 = reportRules.get(key);
				if (val1 == val2) {
					status = "High";
				}
			}
		}
		return status;
	}

	public void updateFinal(int processId, String status) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String loadStatus = "update process set pri_status = '" + status + "' where process_id  = " + processId;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(loadStatus);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
