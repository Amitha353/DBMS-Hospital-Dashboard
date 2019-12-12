package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class StaffMenu {
	public void Menu(Credential emp, int empId) {
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\t Staff Menu \t");
		System.out.println("\t\t----------------------------------");
		System.out.println(
				"\t\t\t[1].Checked-In Patient List\n\t\t\t[2].Treated Patient List\n\t\t\t[3].Add Symptoms\n\t\t\t[4].Add severity scale\n\t\t\t[5].Add Assessment rule\n\t\t\t[6].Go Back\n");
		Scanner input = new Scanner(System.in);
		int choice = input.nextInt();
		String medicalStaff = checkStaffClearance(empId);
		if (medicalStaff == "B" && (choice == 3 || choice == 4 || choice == 5)) {
			System.out.println("\t\tMedical Staff not authorized to perform this operation.");
			System.out.println();
			choice = 6;
		}
		if (medicalStaff == "C") {
			System.out.println("\t\tNon-Medical Staff not authorized to perform this operation.");
			System.out.println();
			choice = 6;
		}
		switch (choice) {
		case 1:
			StaffProcessPatient staffProcess = new StaffProcessPatient();
			staffProcess.Menu(emp, empId);
			break;
		case 2:
			TreatedPatient tPat = new TreatedPatient();
			tPat.treatedPatientList(emp, empId);
			break;
		case 3:
			Symptom sym = new Symptom();
			sym.addSymptom(emp, empId);
			break;
		case 4:
			SeverityScale sevScale = new SeverityScale();
			sevScale.Menu(emp, empId);
			break;
		case 5:
			AssessmentRule assess = new AssessmentRule();
			assess.addAssessment(emp, empId);
			break;
		case 6:
			SignIn sin = new SignIn();
			sin.login();
			break;
		}
	}

	public String checkStaffClearance(int empId) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String ismedical = "N";
		String treatEligible = "N";
		String staffCategory = "C";
		String valQuery = "select ismedical, treatment_eligibility from staff where empid = " + empId;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(valQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 2) {
					ismedical = rs.getString(i);
					treatEligible = rs.getString(i + 1);
				}
			}
			if (ismedical.equals("Y")) {
				staffCategory = "B";
				if (treatEligible.equals("Y")) {
					staffCategory = "A";
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return staffCategory;
	}
}
