package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class StaffProcessPatient {
	Process processPatient = new Process();

	public void Menu(Credential emp, int empId) {
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Staff Process Patient \t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Enter Vitals\n\t\t\t[2].Treat patient\n\t\t\t[3].Go Back\n");
		Scanner input = new Scanner(System.in);
		int choice = input.nextInt();
		switch (choice) {
		case 1:
			Vitals vital = new Vitals();
			int selectedPatient = checkinInPatients(emp, empId);
			vital.Vitals_record(emp, selectedPatient, empId);
			break;
		case 2:
			int selectPatient = checkInCompletePatient(emp, empId);
			if (selectPatient != 0) {
				TreatPatient treat = new TreatPatient();
				treat.treatmentEligibility(emp, selectPatient, empId);
			}
			break;
		case 3:
			StaffMenu staff = new StaffMenu();
			staff.Menu(emp, empId);
			break;
		}
	}

	public int checkinInPatients(Credential emp, int empId) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Scanner scan = new Scanner(System.in);
		int chosenPatientId = 0;
		int processId = 0;
		System.out.println("\t\t\tFrom the list of patients, select a patient and perform vitals-check");
		System.out.println();
		String checkInPatientQuery = "select res.process_id, res.p_id, res.f_name, res.l_name, res.admit from (select process.process_id, pt.p_id, pt.f_name, pt.l_name, pt.admit from process join patients pt "
				+ "on process.p_id = pt.p_id where check_in  = 'T' and emp_id is null and substr(checkin_start,0,8) = to_date(sysdate,'dd-mm-yy') and (pri_status = 'High' "
				+ "or pri_status = 'Quarantine') union select process.process_id, pt.p_id, pt.f_name, pt.l_name,pt.admit from process join patients pt on process.p_id = pt.p_id "
				+ "where check_in  = 'T' and emp_id is null and pri_status = 'Normal' union select process.process_id, pt.p_id, pt.f_name, pt.l_name, pt.admit from process join "
				+ "patients pt on process.p_id = pt.p_id where check_in  = 'T' and emp_id is null and pri_status is null)res where res.admit = "
				+ emp.fac_id;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(checkInPatientQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 5) {
					int process_id = rs.getInt(i);
					int patient_id = rs.getInt(i + 1);
					String p_fname = rs.getString(i + 2);
					String p_lname = rs.getString(i + 3);
					System.out.println("\t\t\t"+patient_id + " " + p_fname + " " + p_lname);
					map.put(process_id, patient_id);
				}
			}
			if (map.size() == 0) {
				System.out.println("\t\t\tNo patients in check-in phase currently");
				Menu(emp, empId);
			}
			chosenPatientId = scan.nextInt();
			for (Entry<Integer, Integer> entry : map.entrySet()) {
				if (entry.getValue().equals(chosenPatientId)) {
					processId = entry.getKey();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return processId;
	}

	public int checkInCompletePatient(Credential emp, int empId) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		Scanner scan = new Scanner(System.in);
		int count = 0;
		System.out.println("\t\t\tFrom the list of patients, enter a patient into treatment phase");
		System.out.println();
		String selectTreatPatient = "select distinct pat.p_id, pat.f_name, pat.l_name from patients pat join process proc "
				+ "on proc.p_id = pat.p_id where proc.checkin_end is not null and treatment is null and pat.admit = "
				+ emp.fac_id;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(selectTreatPatient);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 3) {
					int pat_id = rs.getInt(i);
					String f_name = rs.getString(i + 1);
					String l_name = rs.getString(i + 2);
					count++;
					System.out.println("\t\t\t"+pat_id + " " + f_name + " " + l_name);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (count == 0) {
			System.out.println("\t\t\tCurrently no patients in treatment-phase");
			Menu(emp, empId);
		}
		int selectTreatmentPatient = scan.nextInt();
		return selectTreatmentPatient;
	}
}
