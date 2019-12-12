package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AssessmentRule {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public void addAssessment(Credential emp, int empId) {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\tMenu \t");
		System.out.println("\t\t----------------------------------");
		Scanner scan = new Scanner(System.in);
		String symptom = symptomChosen();
		String bodyPart = bodyPartSelected();
		String severityMetric = severityScale();
		int rule = 0;
		System.out.println("\t\t[1].Does the assessment belongs to a ruleset.\n\t\t[2].No Rule set");
		int option = scan.nextInt();
		if (option == 1) {
			System.out.println("\t\t\tEnter the numeric ruleset");
			rule = scan.nextInt();
		}
		System.out.println("\t\t\tEnter the assessment status:");
		System.out.println("\t\t\t[1].High\n\t\t\t[2].Normal\n\t\t\t[3].Quarantine");
		int status = scan.nextInt();
		String statusValue = (status == 1) ? "High" : (status == 2) ? "Normal" : "Quarantine";
		createAssessmentRule(empId, symptom, bodyPart, severityMetric, rule, statusValue);
	}

	private String symptomChosen() {
		int chosen_symid = 0;
		int length = 0;
		Map<Integer, String> sym_ids = new HashMap<Integer, String>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\tMenu \t");
		System.out.println("\t\t----------------------------------");
		String query = "select * from symptoms";
		Scanner input = new Scanner(System.in);
		System.out.println("\t\t\tPlease select the symptom");
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 4) {
					String symcode = rs.getString(i);
					String symname = rs.getString(i + 1);
					String sevtype = rs.getString(i + 2);
					String b_code = rs.getString(i + 3);
					int index = length + 1;
					System.out.println("\t\t\t[" +index + "]. " + symcode + "  | " + symname + "  | " + b_code);
					sym_ids.put(length + 1, symcode);
					length++;
				}
			}
			int total = length + 1;
			System.out.println("\t\t\t[" +total+ "]. " + "Other");
			sym_ids.put(length+1, "Other");

			chosen_symid = input.nextInt();
			while (chosen_symid > 0 && chosen_symid <= length) {
				return sym_ids.get(chosen_symid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String bodyPartSelected() {
		int chosen_bodyId = 0;
		int length = 0;
		Map<Integer, String> body_ids = new HashMap<Integer, String>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\tMenu \t");
		System.out.println("\t\t----------------------------------");
		String query = "select * from body_part";
		Scanner input = new Scanner(System.in);
		System.out.println("\t\t\tPlease select the body part");
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
					int index = length + 1;
					System.out.println("\t\t\t[" +index + "]. " + bodycode + "  | " + bname);
					body_ids.put(length + 1, bodycode);
					length++;
				}
			}
			int total = length + 1;
			System.out.println("\t\t\t[" +total+ "]. "+ "General");
			body_ids.put(length+1, "General");
			chosen_bodyId = input.nextInt();
			if (chosen_bodyId == 8) {
				return "null";
			}
			while (chosen_bodyId > 0 && chosen_bodyId <= length) {
				return body_ids.get(chosen_bodyId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String severityScale() {
		int length = 0;
		String sev_type = "";
		String sev_value = "";
		Scanner input = new Scanner(System.in);
		String[] metrices = {};
		int scale = 0;
		Map<String, String> metrics = new HashMap<>();
		Map<Integer, String> sevType = new HashMap<>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\tMenu \t");
		System.out.println("\t\t----------------------------------");
		String scaleQuery = "select sev.sev_type, sev.sev_value from severity_scale sev";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(scaleQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					sev_type = rs.getString(i);
					sev_value = rs.getString(i + 1);
					int index = length + 1;
					System.out.println("\t\t\t[" +index + "]. " + sev_type + "  | " + sev_value);
					sevType.put(length + 1, sev_type);
					metrics.put(sev_type, sev_value);
					length++;
				}
			}
			scale = input.nextInt();
			if (scale > 0 && scale <= length) {
				String sevtype = sevType.get(scale);
				String sevval = metrics.get(sevtype);
				if (sevtype == "Range") {
					System.out.println("\t\t\tEnter a value between 1 - 10 for severity");
					String val = input.next();
					return val.toString();
				} else {
					metrices = sevval.split("\\|");
					System.out.println(metrices[0] + " " + metrices[1]);
					System.out.println("\t\t\tPlease select the severity value");
					for (int i = 0; i < metrices.length; i++) {
						System.out.println(i + 1 + "  | " + metrices[i]);
					}
				}
			}

			int chosenSeverity = input.nextInt();
			if (chosenSeverity > 0 && chosenSeverity <= metrices.length) {
				return metrices[chosenSeverity - 1];
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void createAssessmentRule(int empId, String symptom, String bodyPart, String severityMetric, int rule, String statusValue) {
		bodyPart = (bodyPart == "") ? "null" : bodyPart;
		String assessmentQuery = "Insert into assessments_rules values (AssessmentRulesSequence.nextval," + empId + ",'"
				+ symptom + "'," + bodyPart + "," + rule + ",'" + severityMetric + "','" + statusValue + "')";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(assessmentQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
