package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PatientPriority {
	public void prioritizePatient(List<Integer> reportIdentifiers, Credential patient) {
		Map<Integer, Integer> reportRules = reportRules(reportIdentifiers);
		Map<Integer, Integer> assessmentRules = assessmentRules();
		int processID = assessPatient(patient);
		EstablishPatientPriority establish = new EstablishPatientPriority();
		establish.setPriority(processID, assessmentRules, reportRules);
	}

	public Map<Integer, Integer> reportRules(List<Integer> reportIdentifiers) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String rIdentifiers = Arrays.toString(reportIdentifiers.toArray());

		String reportIdList = Arrays.toString(reportIdentifiers.toArray()).replace("[", "").replace("]", "");
		List<Integer> ruleSetIds = new ArrayList<>();
		String ruleIdquery = "select distinct rule_set from reports where report_id in (" + reportIdList
				+ ") order by rule_set";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(ruleIdquery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					ruleSetIds.add(rs.getInt(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String ruleCountquery = "select count(sym_code) from (select distinct sym_code, b_code, rule_set  from reports where"
				+ " report_id in (" + reportIdList + ") order by rule_set) group by rule_set order by rule_set";
		List<Integer> ruleValueIds = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(ruleCountquery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					ruleValueIds.add(rs.getInt(i));
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<Integer, Integer> ruleMap = new HashMap<>();
		Iterator<Integer> i1 = ruleSetIds.iterator();
		Iterator<Integer> i2 = ruleValueIds.iterator();
		while (i1.hasNext() || i2.hasNext())
			ruleMap.put(i1.next(), i2.next());

		return ruleMap;
	}

	public Map<Integer, Integer> assessmentRules() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String assessmentInfo = "select rule_set, count(rule_set) from assessments_rules group by rule_set order by rule_set";
		Map<Integer, Integer> assessment = new HashMap<>();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(assessmentInfo);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					assessment.put(rs.getInt(i), rs.getInt(i + 1));
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return assessment;
	}

	public int assessPatient(Credential patient) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String procesQuery = "select res.process_id from (select process_id from process where p_id = " + patient.pt_id
				+ " and  checkin_end is null order by process_id desc)res where rownum < 2";
		int process_id = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(procesQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					process_id = rs.getInt(i);
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return process_id;
	}
}
