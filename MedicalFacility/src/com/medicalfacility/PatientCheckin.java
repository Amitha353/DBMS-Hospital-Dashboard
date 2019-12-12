package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PatientCheckin {
	Process processPatient = new Process();
	List<Integer> reportIdentifiers = new ArrayList<>();
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public void Checkin(Credential patient) {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tPatient Checkin\t");
		System.out.println("\t\t----------------------------------");
		Scanner scan = new Scanner(System.in);
		String choice = symptomCriteria(patient);

		processPatient.symcode = (choice == "Other") ? "" : choice;

		Reports patientReport = new Reports();
		MetaData metaData = new MetaData();
		patientReport = metaData.SymData(processPatient.symcode);

		RecordSymptoms recSyms = new RecordSymptoms();

		int repId = recSyms.recordSymptom(patient, patientReport, processPatient.symcode);
		updateRuleSet(patientReport, repId, processPatient.symcode);
		reportIdentifiers.add(repId);

		System.out.println("\t\t\t[1].Experiencing other symptoms\n\t\t\t[2].Done");
		int status = scan.nextInt();

		while (status == 2) {
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			processPatient.checkin = "T";
			processPatient.checkinStart = myDateObj.format(myFormatObj);

			validatePatient(patient, processPatient, reportIdentifiers);
			PatientPriority prioritize = new PatientPriority();
			prioritize.prioritizePatient(reportIdentifiers, patient);

			StartPage.startup();
		}
		Checkin(patient);
	}

	public void updateRuleSet(Reports patientReport, int repId, String symCode) {

		String ruleSetQuery = "update reports set rule_set = (select assess.rule_set from assessments_rules assess where assess.sym_code = '"
				+ symCode + "'";
		if (patientReport.bodyPart != "") {
			ruleSetQuery += " and assess.body_code = '" + patientReport.bodyPart + "'";
		}
		if (!symCode.equals("SYM001")) {
			ruleSetQuery += " and assess.severity = '" + patientReport.severity + "') where report_id = " + repId;
		} else {
			ruleSetQuery += " and assess.severity < " + patientReport.severity + ") where report_id = " + repId;
		}

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(ruleSetQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String symptomCriteria(Credential patient) {
		int chosen_symid = 0;
		int length = 0;
		Map<Integer, String> sym_ids = new HashMap<Integer, String>();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		String query = "select * from symptoms order by sym_code";
		Scanner input = new Scanner(System.in);
		System.out.println("\t\t\tPlease select the symptom you are experiencing.");
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
					System.out.println("\t\t\t[" + index + "]." + symcode + " - " + symname + " - " + b_code);
					sym_ids.put(length + 1, symcode);
					length++;
				}
			}
			int lst = length + 1;
			System.out.println("\t\t\t[" + lst + "]." + "Other");
			sym_ids.put(length + 1, "Other");
			chosen_symid = input.nextInt();
			System.out.println();
			while (chosen_symid > 0 && chosen_symid <= length+1) {
				return sym_ids.get(chosen_symid);
			}
			System.out.println();
			System.out.println("\t\t\tThe entered option is in-valid");
			System.out.println("\t\t\t[1]. Re-choose the symptom youre experiencing\n\t\t\t[2]. Go back");
			int option = input.nextInt();
			if (option == 1) {
				symptomCriteria(patient);
			} else if (option == 2) {
				Checkin(patient);
			} else {
				System.out.println("\t\t\tOperation un-recognized");
				System.out.println("\t\t\tLogging out user...");
				StartPage.startup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void validatePatient(Credential patient, Process processPatient, List<Integer> reportIdentifiers) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String rIdentifiers = Arrays.toString(reportIdentifiers.toArray());

		String reportIdList = Arrays.toString(reportIdentifiers.toArray()).replace("[", "").replace("]", "");
		String valQuery = "Insert into Process values (ProcessIDSequence.nextval," + patient.pt_id + ",'"
				+ processPatient.symcode + "', null, '" + processPatient.checkin + "'," + "to_timestamp('"
				+ processPatient.checkinStart + "','dd-mm-yyyy hh24:mi:ss'),"
				+ "null,null,null,null,null,null,null,null,null,'" + reportIdList + "')";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(valQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
