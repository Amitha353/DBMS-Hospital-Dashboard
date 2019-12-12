package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PatientRouting {
	public void Routing(Credential patient) {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tPatient Routing\t");
		System.out.println("\t\t----------------------------------");
		Process processPatient = new Process();
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Check In\n\t\t\t[2].Check-out Acknowledgement\n\t\t\t[3].Go Back\n");
		Scanner input = new Scanner(System.in);
		int casevar = input.nextInt();
		switch (casevar) {
		case 1:
			checkInCompletion(patient);
			break;
		case 2:
			PatientCheckoutAckn sup = new PatientCheckoutAckn();
			sup.Checkout(patient.getPt_id(),patient.getFac_id());
			break;
		case 3:
			SignIn signin = new SignIn();
			signin.login();
			break;
		default:
			break;
		}
	}

	public void checkInCompletion(Credential patient) {
		int counter = 0;
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String checkinProgess = "select count(*) from (select res.process_id, res.p_id, res.f_name, res.l_name from (select process_id, pt.p_id, pt.f_name, pt.l_name from process join patients pt \r\n"
				+ "on process.p_id = pt.p_id where check_in  = 'T' and emp_id is null /*and substr(checkin_start,0,8) = to_date(sysdate,'dd-mm-yy')*/ order by process_id desc) res\r\n"
				+ "where res.p_id = " + patient.pt_id + " and rownum < 2)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(checkinProgess);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i++) {
					counter = rs.getInt(i);
				}
			}

			if (counter == 0) {
				PatientCheckin sin = new PatientCheckin();
				System.out.println(patient.pt_id);
				sin.Checkin(patient);
			} else {
				System.out.println("\t\t\tCheck-in phase in current facility in progress.");
				StartPage.startup();
			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int PreferredFacility(Credential patient) {
		int chosen_facid = 0;
		int length = 0;
		Map<Integer, Integer> fac_ids = new HashMap<Integer, Integer>();
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String medFacilityQuery = "select distinct med.fac_id, med.name,med.class_code, med.class_desc, res.codes, res.certnames from med_facility_information med join "
				+ "(select FAC_ID, wm_concat(cert_code) as codes,wm_concat(CERT_NAME) as certnames from med_facility_information group by fac_id)res "
				+ "on med.fac_id = res.fac_id";
		Scanner input = new Scanner(System.in);
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
					System.out.println("\t\t\t["+ index + "].  | " + med_id + " - " + med_name + " - " + class_desc + "- "+ cert_name);
					fac_ids.put(length + 1, med_id);
					length++;
				}
			}
			chosen_facid = input.nextInt();
			while (chosen_facid > 0 && chosen_facid <= length) {
				return fac_ids.get(chosen_facid);
			}
			System.out.println("\t\t\tThe entered option is in-valid");
			System.out.println("\t\t\t[1].Re-choose the medical facility\n\t\t\t[2].Go back");
			int option = input.nextInt();
			if (option == 1) {
				PreferredFacility(patient);
			} else if (option == 2) {
				Routing(patient);
			} else {
				System.out.println("\t\t\tOperation un-recognized");
				System.out.println("\t\t\tLogging out user...");
				StartPage.startup();
			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chosen_facid;
	}
}
