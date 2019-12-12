package com.medicalfacility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StaffCheckoutPatient {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	Connection connection = databaseConnection.getConnection();
	int dis_id = 0, neg_code = 0;
	int facid = 0, refid = 0;
	List<Integer> rcode = new ArrayList<>();
	String treatment_desc = null, neg_desc = null;
	Scanner input = new Scanner(System.in);

	public void Patient_Checkout(Credential emp, int pid, int empId) {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\tStaff Checkout Patient \t");
		System.out.println("\t\t----------------------------------");
		System.out.println(
				"\t\t\t[1].Discharge Status\n\t\t\t[2].Referral Status\n\t\t\t[3].Treatment\n\t\t\t[4].Negative Experience\n\t\t\t[5].Go Back\n\t\t\t[6].Submit\n");
		int casevar = input.nextInt();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (casevar != 6) {
			switch (casevar) {
			case 1:
				DischargeStatus d = new DischargeStatus();
				dis_id = d.status();
				break;
			case 2:
				if (dis_id != 3) {
					System.out.println("\t\tNot applicable as the discharge status is not referred");
					break;
				}
				referral_status();
				break;
			case 3:
				System.out.println("\t\tEnter the treatment description:");

				try {
					treatment_desc = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 4:
				NegExperience ne = new NegExperience();
				neg_code = ne.nexperience();
				if (neg_code != 0) {
					System.out.println("\t\tDo you wish to enter the description:(Enter 'Y' or 'N')");
					if (input.next().equalsIgnoreCase("Y")) {
						System.out.println("\t\tPlease enter the description:\n");
						try {
							neg_desc = reader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			case 5:
				StaffMenu sm = new StaffMenu();
				sm.Menu(emp, empId);
				break;
			}
			System.out.println(
					"\t\t\t[1].Discharge Status\n\t\t\t[2].Referral Status\n\t\t\t[3].Treatment\n\t\t\t[4].Negative Experience\n\t\t\t[5].Go Back\n\t\t\t[6].Submit\n");
			casevar = input.nextInt();
		}

		if (dis_id == 0) {
			System.out.println("\t\tPlease enter the discharge id");
			Patient_Checkout(emp, pid, empId);
		} else if (dis_id == 3 && refid == 0 && rcode.size() == 0) {
			System.out.println("\t\tPlease complete the referral status");
			Patient_Checkout(emp, pid, empId);
		} else if (treatment_desc == null || treatment_desc == "") {
			System.out.println("\t\tPlease enter treatment description");
			Patient_Checkout(emp, pid, empId);
		}
		confirm(emp, pid, empId);
	}

	public void referral_status() {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMenu \t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Facility Id\n\t\t\t[2].Referrer Id\n\t\t\t[3].Add reason\n\t\t\t[4].Go Back\n");
		int casevar = input.nextInt();
		while (casevar != 4) {
			switch (casevar) {
			case 1:
				System.out.println("\t\tEnter the Facility Id(ENTER 0 IF NO FACILITY IS REFERRED):");
				facid = input.nextInt();
				break;
			case 2:
				System.out.println("\t\tEnter the Referrer Id");
				refid = input.nextInt();
				// validate
				break;
			case 3:
				if (rcode.size() == 4) {
					System.out.println("\t\tYou can enter upto 4 reasons only!!");
					break;
				}
				Reason reasn = new Reason();
				rcode.add(reasn.Choose_Reason());
				break;
			}
			System.out.println("\t\t----------------------------------");
			System.out.println("\t\t\t\tMenu \t");
			System.out.println("\t\t----------------------------------");
			System.out.println("\t\t\t[1].Facility Id\n\t\t\t[2].Referrer Id\n\t\t\t[3].Add reason\n\t\t\t[4].Go Back\n");
			casevar = input.nextInt();
		}
		if (refid == 0) {
			System.out.println("\t\tRefferer id not entered. Please enter the id:");
			referral_status();
		} else if (rcode.size() == 0) {
			System.out.println("\t\tReason not selected.Please select a reason");
			referral_status();
		}

	}

	public void confirm(Credential emp, int pid, int empId) {
		int coutid = -1;
		String disdesc = "", negdesc = "";
		PreparedStatement preparedStatement;
		ResultSet rs;
		String query = "select dis_desc from discharge where dis_id =" + dis_id + "";
		String negquery = "select neg_desc from neg_experience where neg_code =" + neg_code + "";
		try {
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				disdesc = rs.getString(1);
			}
			preparedStatement = connection.prepareStatement(negquery);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {

				negdesc = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("\t\t===================================");
		System.out.println("\t\t\tREPORT SUMMARY \t");
		System.out.println("\t\t===================================");
		System.out.println();
		System.out.println("\t\tEmployee id:\t" + empId + "\n\t\t" + "Employee name:\t" + emp.lname);
		 query = "select f_name,l_name from patients where p_id="+pid+""; 
		 try {
		 preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				System.out.println("\t\tpid:\t" + pid + "\t"+rs.getString("f_name")+"\t"+rs.getString("l_name")+"\n");
			}}catch (SQLException e) {
				e.printStackTrace();
			}
		
		System.out.println("\t\tDischarge Status:\t" + disdesc);
		if (dis_id == 3) {
			System.out.println("\n\t\tFacility id referred to:\t" + facid);
			System.out.println("\n\t\tReferrer id:\t" + refid);
			if (rcode.size() != 0) {
				System.out.println("\n\t\tList of reasons:");
				for (int i = 0; i < rcode.size(); i++) {
					query = "select r_desc from reason where r_code=" + rcode.get(i) + "";
					try {
						preparedStatement = connection.prepareStatement(query);
						rs = preparedStatement.executeQuery();
						while (rs.next()) {

							System.out.println("\n\t\t reason" + (i + 1) + ":\t" + rs.getString(1));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		}
		System.out.println("\n\t\tTreatment description:\t" + treatment_desc);
		System.out.println("\n\t\tNegative experience code name:\t" + negdesc);
		if (neg_desc != null) {
			System.out.println("\n\t\tNegative experience description:\t" + neg_desc + "\n");
		}
		System.out.println("\n\t\t\t[1].Confirm\n\t\t\t[2].Go back\n");
		int choice = input.nextInt();

		if (choice == 1) {
			try {
				if(neg_code != 0) {
				query = " insert into checkout values(checkoutidsequence.nextval," + empId + "," + pid + "," + neg_code
						+ "," + dis_id + "," + "null,null,'" + treatment_desc + "','" + neg_desc + "')";
				} else {
					query = " insert into checkout values(checkoutidsequence.nextval," + empId + "," + pid + ",null," 
				+ dis_id + "," + "null,null,'" + treatment_desc + "','" + neg_desc + "')";
				}

				preparedStatement = connection.prepareStatement(query);

				preparedStatement.executeQuery();
				if (dis_id == 3) {
					query = "select CHECKOUTIDSEQUENCE.currval from dual";
					preparedStatement = connection.prepareStatement(query);
					rs = preparedStatement.executeQuery();
					while (rs.next()) {
						coutid = rs.getInt(1);
					}
					for (int i = 0; i < rcode.size(); i++) {
						query = "update referred set rcode" + (i + 1) + "=" + rcode.get(i) + " " + " where cout_id ="
								+ coutid + "";
						preparedStatement = connection.prepareStatement(query);
						preparedStatement.executeQuery();
					}
					if(facid!=0)
					{
					query = "update referred set fac_id=" + facid + " where cout_id =" + coutid + "";
					
					preparedStatement = connection.prepareStatement(query);
					preparedStatement.executeQuery();
					}
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println();
			System.out.println("\t\t\t**********Success**********");
			System.out.println();
			StaffMenu sm = new StaffMenu();
			sm.Menu(emp, empId);
		} else if (choice == 2) {
			// go back to staff patient report page
			Patient_Checkout(emp, pid, empId);
		}
	}
}
