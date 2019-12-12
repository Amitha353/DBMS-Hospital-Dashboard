package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SeverityScale {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public void Menu(Credential emp, int empId) {
		Scanner scan = new Scanner(System.in);
		int level = 0;
		String scaleValue = "";
		System.out.println("\t\t\tPlease enter the severity scale type");
		String scaleType = scan.next();
		System.out.println("\t\t\tIf your scale has levels please type 1");
		level = scan.nextInt();
		List<String> result = new ArrayList<>();
		while (level == 1) {
			System.out.println("\t\t\tPlease enter your level value");
			String val = scan.next();
			result.add(val);
			System.out.println("\t\t\t[1].Do you have more metric levels?\n\t\t\t[2].Is you're metric complete?");
			level = scan.nextInt();
		}
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
			scaleValue += result.get(i) + "|";
		}
		ProcessSeverityScale(scaleType, scaleValue, empId);
		StaffMenu staff = new StaffMenu();
		staff.Menu(emp, empId);
	}

	private void ProcessSeverityScale(String scaleType, String scaleValue, int empId) {
		String severityQuery = "Insert into severity_scale values (SeverityScaleSequence.nextval,'" + scaleType + "','"
				+ scaleValue.substring(0, scaleValue.length() - 1) + "'," + empId + ")";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(severityQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
