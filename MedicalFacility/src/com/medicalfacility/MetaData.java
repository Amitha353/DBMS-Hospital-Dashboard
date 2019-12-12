package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MetaData {
	public Reports SymData(String symcode) {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMetaData\t");
		System.out.println("\t\t----------------------------------");
		System.out.println();
		Reports patientReport = new Reports();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		reportSymptoms(patientReport, symcode);
		return patientReport;
	}

	public void reportSymptoms(Reports patientReport, String symcode) {
		Scanner input = new Scanner(System.in);

		patientReport.bodyPart = bodyPartIssue(symcode);
		System.out.println();
		
		System.out.println("\t\t\tSince when (in number of day(s)) have you been facing the problem?");
		patientReport.duration = input.nextFloat();
		System.out.println();
		
		System.out.println("\t\t\tIs this the first time you are experiencing this issue - (T) or (F)?");
		patientReport.firstOccur = input.next();
		System.out.println();
		
		if(symcode.equals("") ) {
			System.out.println("\t\t\tPlease enter the severity of your symptom.");
			patientReport.severity = input.next();
		} else {
			patientReport.severity = severityScaleMetric(symcode);
		}
		System.out.println();
		
		System.out.println("\t\t\tAny incidents you have encountered recently, that could have triggered the symptom?");
		patientReport.reason = input.next();
	}

	public String bodyPartIssue(String symcode) {
		int chosen_bodyId = 0;
		int length = 0;
		Map<Integer, String> body_ids = new HashMap<Integer, String>();;
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String query = "select * from body_part";
		Scanner input = new Scanner(System.in);
		System.out.println("\t\t\tPlease select the body part you are experiencing the symptom.");
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
					System.out.println("\t\t\t["+ index + "]. " + bodycode + " - " + bname);
					body_ids.put(length + 1, bodycode);
					length++;
				}
			}
			int lst = length+1;
			System.out.println("\t\t\t["+ lst + "]. " + "General");
			body_ids.put(length, "General");
			chosen_bodyId = input.nextInt();
			if (chosen_bodyId == 8) {
				return "";
			}
			while (chosen_bodyId > 0 && chosen_bodyId <= length) {
				return body_ids.get(chosen_bodyId);
			}
			System.out.println("\t\t\tThe entered option is in-valid");
			System.out.println("\t\t\t[1].Re-choose the body part you are experiencing problem.\n\t\t\t[2].Go back");
			int option = input.nextInt();
			if (option == 1) {
				bodyPartIssue(symcode);
			} else if (option == 2) {
				SymData(symcode);
			} else {
				System.out.println("\t\t\tOperation un-recognized");
				System.out.println("\t\t\tLogging out user...");
				StartPage.startup();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String severityScaleMetric(String symCode) {
		int length = 0;
		String sev_type = "";
		String sev_value = "";
		Scanner input = new Scanner(System.in);
		String[] metrics = {};
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String scaleQuery = "select sym.sym_code, sev.sev_type, sev.sev_value from severity_scale sev "
				+ "join symptoms sym on sym.sev_type = sev.sev_type where sym.sym_code = '" + symCode + "'";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(scaleQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 4) {
					String sym_code = rs.getString(i);
					sev_type = rs.getString(i + 1);
					sev_value = rs.getString(i + 2);
					int index = length+1;
//					System.out.println("\t\t\t[" + index + "]. " + sym_code + " - " + sev_type + " - " + sev_value);
					length++;
				}
			}

			if (!sev_type.equals("Range")) {
				System.out.println(
						"\t\t\tPlease enter the severity of your symptom according to the below scale, for assistance.");
				metrics = sev_value.split("\\|");
				for (int i = 0; i < metrics.length; i++) {
					int idx = i+1;
					System.out.println("\t\t\t[" + idx + "]. " + metrics[i]);
				}
			} else {
				System.out.println("\t\t\tEnter the severity of your symptom in the scale 1 - 10, 10 extremely severe.");
				int scale = input.nextInt();
				if (scale > 0 && scale <= 10) {
					return String.valueOf(scale);
				} else {
					System.out.println("\t\t\tPlease select a valid symptom code.");
					severityScaleMetric(symCode);
				}
			}
			int chosenSymptom = input.nextInt();
			if (chosenSymptom > 0 && chosenSymptom <= metrics.length) {
				return metrics[chosenSymptom - 1];
			} else {
				System.out.println("\t\t\tPlease select a valid symptom code.");
				severityScaleMetric(symCode);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
