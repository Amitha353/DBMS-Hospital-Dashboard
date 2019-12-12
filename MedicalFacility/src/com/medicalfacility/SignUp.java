package com.medicalfacility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SignUp {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();
	Scanner input = new Scanner(System.in);
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	String fname, lname, city, country, street, state;
	int zip, admit, number;
	Long phone_number;
	Date dob;

	public void register() {
		try {
			System.out.println("\t\t----------------------------------");
			System.out.println("\t\t\t  Sign Up\t");
			System.out.println("\t\t----------------------------------");
			System.out.println();
			SignIn si = new SignIn();
			admit = si.MedicalFacility();
			System.out.println("\t\t\tEnter First Name");
			fname = reader.readLine();
			System.out.println("\t\t\tEnter Last Name");
			lname = reader.readLine();
			System.out.println("\t\t\tDate Of Birth");
			dob = Date.valueOf(input.next());
			System.out.println("\t\t\tStreet Number");
			number = input.nextInt();
			System.out.println("\t\t\tStreet Name");
			street = reader.readLine();
			System.out.println("\t\t\tCity");
			city = reader.readLine();
			System.out.println("\t\t\tState");
			state = reader.readLine();
			System.out.println("\t\t\tZip Code");
			zip = input.nextInt();
			System.out.println("\t\t\tCountry");
			country = reader.readLine();
			System.out.println("\t\t\tPhone Number");
			phone_number = input.nextLong();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Sign Up\n\t\t\t[2].Go back\n");
		int casevar = input.nextInt();
		if (casevar == 1) {

			String query = "select PATIENTSIDSEQUENCE.nextval from dual";
			try {
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				ResultSet rs = preparedStmt.executeQuery();
				if (rs.next()) {
					long pid = rs.getLong(1);
					query = " insert into patients values (" + pid + ",?, ?, ?, ?, ?, ?,?,?,?,?,?)";
					preparedStmt = connection.prepareStatement(query);
					preparedStmt.setString(1, fname);
					preparedStmt.setString(2, lname);
					preparedStmt.setDate(3, dob);
					preparedStmt.setString(4, street);
					preparedStmt.setString(5, city);
					preparedStmt.setString(6, state);
					preparedStmt.setInt(7, zip);
					preparedStmt.setLong(8, phone_number);
					preparedStmt.setInt(9, admit);
					preparedStmt.setInt(10, number);
					preparedStmt.setString(11, country);
					preparedStmt.executeQuery();
					connection.close();
					System.out.println("\t\t\tYou have successfully registered!!");
					SignIn redirectP = new SignIn();
					redirectP.login();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else {
			StartPage.startup();
		}
	}
}
