package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Symptom {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public void addSymptom(Credential emp, int empId) {
		StaffMenu staff = new StaffMenu();
		System.out.println("\t\t\tEnter Symptom name");
		Scanner input = new Scanner(System.in);
		String symName = input.nextLine();
		System.out.println("\t\t\tEnter Symptom Body part");// can be blank
		String byPart = bodyPartIssue();
		System.out.println("\t\t\tEnter Symptom severity");// we have to display the type of severity to choose from
		String sevScale = severityMetric();
		System.out.println("\n\t\t\t[1].Record\n\t\t\t[2].Go back\n");
		if (input.nextInt() == 1) {
			symptomProcess(symName, byPart, sevScale);
			staff.Menu(emp, empId);
		} else {
			staff.Menu(emp, empId);
		}
	}

	private String bodyCodeExtract(String bodyPart) {
		String code = "";
		String bodyQuery = "Select body_code from body_part where name = '" + bodyPart + "'";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(bodyQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					code = rs.getString(i);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	private void symptomProcess(String symptomName, String bodyCode, String severityScale) {
		String severityQuery = "Insert into Symptoms values ('SYM'||to_char(SymptomSequence.nextval,'FM000'),'"
				+ symptomName + "','" + severityScale + "','" + bodyCode + "')";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(severityQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String bodyPartIssue() {
		int chosen_bodyId = 0;
		int length = 0;
		Map<Integer, String> body_ids = new HashMap<Integer, String>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Body Part Menu \t");
		System.out.println("\t\t----------------------------------");
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String query = "select * from body_part";
		Scanner input = new Scanner(System.in);
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 3) {
					String bodycode = rs.getString(i);
					String bname = rs.getString(i + 1);
					String dept_code = rs.getString(i + 2);
					System.out.println(length + 1 + "  | " + bodycode + "  | " + bname);
					body_ids.put(length + 1, bodycode);
					length++;
				}
			}
			System.out.println(length + 1 + "  | " + "General");
			body_ids.put(length, "General");
			chosen_bodyId = input.nextInt();
			if(chosen_bodyId == 7) {
				return "General";
			}
			while (chosen_bodyId > 0 && chosen_bodyId <= length) {
				return body_ids.get(chosen_bodyId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String severityMetric() {
		int chosen_severity = 0;
		int length = 0;
		Map<Integer, String> severityScale = new HashMap<Integer, String>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t  Severity Metric \t");
		System.out.println("\t\t----------------------------------");
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String query = "select * from severity_scale";
		Scanner input = new Scanner(System.in);
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 4) {
					String sev_code = rs.getString(i);
					String sev_type = rs.getString(i + 1);
					String sev_value = rs.getString(i + 2);
					System.out.println(length + 1 + "  | " + sev_code + "  | " + sev_type + "  | " + sev_value);
					severityScale.put(length + 1, sev_type);
					length++;
				}
			}
			chosen_severity = input.nextInt();
			while (chosen_severity > 0 && chosen_severity <= length) {
				return severityScale.get(chosen_severity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
