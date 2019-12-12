package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Vitals {
	Process processPatient = new Process();

	public void Vitals_record(Credential emp, int selectedPatient, int empId) {
		System.out.println("\t\t\tEnter Temperature");
		Scanner input = new Scanner(System.in);
		int temp = input.nextInt();
		System.out.println("\t\t\tEnter BP-Systolic");
		int bpsys = input.nextInt();
		System.out.println("\t\t\tEnter BP-Diastolic");
		int bpdia = input.nextInt();
		System.out.println("\n\t\t\t[1].Record Vitals\n\t\t\t[2].Go back\n");
		if (input.nextInt() == 1) {
			recordVitals(selectedPatient, empId, temp, bpsys, bpdia);
			StaffProcessPatient staffProcess = new StaffProcessPatient();
			staffProcess.Menu(emp, empId);
		} else {
			StaffProcessPatient staffProcess = new StaffProcessPatient();
			staffProcess.Menu(emp, empId);
		}
	}

	public void recordVitals(int processId, int employeeId, int temp, int bpsys, int bpdia) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String checkin_end = myDateObj.format(myFormatObj);
		String recordVitalQuery = "update process set emp_id = " + employeeId + ",temp = " + temp + ", BP_SYSTOLIC = "
				+ bpsys + ", BP_DIASTOLIC = " + bpdia + ", checkin_end = to_timestamp('" + checkin_end
				+ "','dd-mm-yyyy hh24:mi:ss') where process_id = " + processId;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(recordVitalQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
