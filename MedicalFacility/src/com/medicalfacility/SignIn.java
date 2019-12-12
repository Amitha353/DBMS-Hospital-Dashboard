package com.medicalfacility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SignIn {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	Credential person = new Credential();

	public void login() {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tSign In\t");
		System.out.println("\t\t----------------------------------");
		System.out.println();
		int option = enterCredentials();
		if (option == 1 || option == 2) {
			if (option == 1) {

				System.out.println("\t\t\tPlease enter your employee id");
				int emp_id = employeeValidation();
				StaffMenu staff = new StaffMenu();
				staff.Menu(person, emp_id);
			} else {
				patientSelfCheckin();
			}
		} else {
			StartPage.startup();
		}
	}

	private int enterCredentials() {
		Scanner input = new Scanner(System.in);

		System.out.println("\t\t[1].Enter Facility Id");
		person.fac_id = MedicalFacility();

		System.out.println("\t\t[2].Enter Last Name");
		person.lname = input.next();

		System.out.println("\t\t[3].Enter Date Of Birth");
		person.dob = input.next();

		System.out.println("\t\t[4].City");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			person.city = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("\t\t\tPlease select the user type");
		System.out.println("\t\t\t[1].Staff\n\t\t\t[2].Patient\n");
		int userType = input.nextInt();

		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Sign In\n\t\t\t[2].Go back\n");
		int choice = input.nextInt();
		if (choice == 1) {
			return userType;
		} else {
			StartPage.startup();
		}
		return input.nextInt();
	}

	public int employeeValidation() {
		Scanner emp = new Scanner(System.in);
		int empId = emp.nextInt();
		int resEmp = 0;
		String empQuery = "select * from ((select st.empid, fac.fac_id, st.last_name, st.dob, st.city from staff st "
				+ "join service_department ser on ser.dept_code = st.dept_code join FacilityDept fac on "
				+ "fac.dept_code = ser.dept_code join medical_facility med on med.fac_id = fac.fac_id) "
				+ "union (select st.empid, fac.fac_id, st.last_name, st.dob, st.city from sec_serv_department sec "
				+ "join staff st on st.empid = sec.emp_id join service_department ser on ser.dept_code = sec.dept_code "
				+ "join FacilityDept fac on fac.dept_code = ser.dept_code join medical_facility med on med.fac_id = fac.fac_id)) "
				+ "res where res.empid = " + empId + " and res.last_name = '" + person.lname + "'and res.city = '"
				+ person.city + "' and res.dob = '" + person.dob + "' and res.fac_id = " + person.fac_id;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(empQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 5) {
					resEmp = rs.getInt(i);
					int fac_id = rs.getInt(i + 1);
					String l_name = rs.getString(i + 2);
					String dob = rs.getString(i + 3);
					String city = rs.getString(i + 4);
				}
			}
			if (empId == resEmp) {
				return empId;
			} else {
				System.out.println("\t\t\tUser unauthorized, please provide valid credentials");
				StartPage.startup();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void patientSelfCheckin() {
		int pt_id = 0;
		String query = "select pt.p_id from medical_facility fac join patients pt on fac.fac_id = pt.admit where fac.fac_id = '"
				+ person.fac_id + "'and pt.l_name = '" + person.lname + "'and pt.dob = '" + person.dob
				+ "'and pt.city in '" + person.city + "'";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					pt_id = rs.getInt(i);
				}
			}
			if (pt_id != 0) {
				person.pt_id = pt_id;
				PatientRouting pr = new PatientRouting();
				pr.Routing(person);
			} else {
				System.out.println("\t\t\tSign In incorrect");
				login();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int MedicalFacility() {
		int medical_facility_id = 0;
		int length = 0;
		Map<Integer, Integer> fac_ids = new HashMap<Integer, Integer>();
		Scanner scan = new Scanner(System.in);
		System.out.println("\t\tFrom the list of medical facilities select your preference.");
		System.out.println();
		String medFacilityQuery = "select distinct med.fac_id, med.name,med.class_code, med.class_desc, res.codes, res.certnames from med_facility_information med join(\r\n"
				+ "select fac_id, listagg(cert_code, ',') within group (order by cert_code) as codes, listagg(CERT_NAME, ',') within group (order by CERT_NAME) as certnames\r\n"
				+ "from med_facility_information group by fac_id) res on med.fac_id = res.fac_id";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(medFacilityQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 6) {
					int med_id = rs.getInt(i);
					String med_name = rs.getString(i + 1);
					String class_desc = rs.getString(i + 3);
					String cert_name = rs.getString(i + 5);
					int index = length + 1;
					System.out.println("\t\t[" + index + "].    | " + med_id + "    |  " + med_name + " - "
							+ class_desc + " - " + cert_name);
					fac_ids.put(length + 1, med_id);
					length++;
				}
			}
			medical_facility_id = scan.nextInt();
			while (medical_facility_id > 0 && medical_facility_id <= length) {
				return fac_ids.get(medical_facility_id);
			}
			System.out.println();
			System.out.println("\t\t\tThe entered option is in-valid");
			System.out.println("\t\t\t[1].Re-choose the medical facility\n\t\t\t[2].Go back");
			int option = scan.nextInt();
			if (option == 1) {
				MedicalFacility();
			} else if (option == 2) {
				login();
			} else {
				System.out.println("\t\t\tOperation un-recognized");
				System.out.println("\t\t\tLogging out user...");
				StartPage.startup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return medical_facility_id;
	}
}
