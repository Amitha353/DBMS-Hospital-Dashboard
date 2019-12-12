package com.medicalfacility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PatientCheckoutAckn {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();

	public void Checkout(int pid, int facid) {
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\tPatient Checkout Acknowledgement\t");
		System.out.println("\t\t----------------------------------");
		String f_name = null, l_name = null, treatment_given = null, neg_desc = null, neg_codename = null,
				dis_desc = null, ack_desc = null;
		int dis_id = -1, neg_code = -1, refid = -1, fid = -1;
		List<Integer> rcode = new ArrayList<Integer>();
		String query = "select f_name,l_name from patients where p_id=" + pid + "";
		PreparedStatement preparedStatement;
		int cout = -1;
		ResultSet rs;
		try {
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				f_name = rs.getString("f_name");
				l_name = rs.getString("l_name");
			}
			query = "select dis_id,neg_code,treatment_given,neg_desc,cout_id from checkout where p_id=" + pid
					+ " and checkout_time is null";
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				dis_id = rs.getInt("dis_id");
				neg_code = rs.getInt("neg_code");
				treatment_given = rs.getString("treatment_given");
				neg_desc = rs.getString("neg_desc");
				cout = rs.getInt("cout_id");
			}

			query = "select dis_desc from discharge where dis_id =" + dis_id + "";
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				dis_desc = rs.getString("dis_desc");
			}
			query = "select neg_desc from neg_experience where neg_code =" + neg_code + "";
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {

				neg_codename = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("\t\t===================================");
		System.out.println("\t\t\tREPORT SUMMARY \t");
		System.out.println("\t\t===================================");
		System.out.println();
		System.out.println("\t\tpid:\t" + pid + "\n");
		System.out.println("\t\tfirst name\t:" + f_name + "\n");
		System.out.println("\t\tlast name\t:" + l_name + "\n");
		System.out.println("\t\tDischarge Status:\t" + dis_desc);
		if (dis_id == 3) {
			query = "select fac_id,emp_id,rcode1,rcode2,rcode3,rcode4 from referred where p_id=" + pid + "";

			try {
				preparedStatement = connection.prepareStatement(query);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					refid = rs.getInt("emp_id");
					fid = rs.getInt("fac_id");
					if (rs.getInt("rcode1") != 0) {
						rcode.add(rs.getInt("rcode1"));
					}
					if (rs.getInt("rcode2") != 0) {
						rcode.add(rs.getInt("rcode2"));
					}
					if (rs.getInt("rcode3") != 0) {
						rcode.add(rs.getInt("rcode3"));
					}
					if (rs.getInt("rcode4") != 0) {
						rcode.add(rs.getInt("rcode4"));
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\n\t\tFacility id referred to:\t" + facid);
			System.out.println("\n\t\tReferrer id:\t" + refid);
			System.out.println("\n\t\tList of reasons:");
			for (int i = 0; i < rcode.size(); i++) {
				query = "select r_desc from reason where r_code=" + rcode.get(i) + "";
				try {
					preparedStatement = connection.prepareStatement(query);
					rs = preparedStatement.executeQuery();
					while (rs.next()) {

						System.out.println("\n\t\treason" + (i + 1) + ":\t" + rs.getString(1));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("\n\t\tTreatment description:\t" + treatment_given);
		System.out.println("\n\t\tNegative experience code name:\t" + neg_codename);
		if (neg_desc != null) {
			System.out.println("\n\t\tNegative experience description:\t" + neg_desc + "\n");
		}

		System.out.println("\t\t\tDo you acknowledge?\n");
		System.out.println("\t\t\t[1].Yes\n\t\t\t[2].No\n");
		Scanner input = new Scanner(System.in);
		int casevar = input.nextInt();
		if (casevar == 2) {
			System.out.println("\t\tPlease enter the acnowledgement description:\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				ack_desc = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		String testQuery = "update checkout set checkout_time=SYSDATE, ack_desc='" + ack_desc + "' where cout_id="
				+ cout + "";
		try {
			preparedStatement = connection.prepareStatement(testQuery);
			rs = preparedStatement.executeQuery();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StartPage.startup();
	}

}
