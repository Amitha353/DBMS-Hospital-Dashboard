package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class TreatedPatient {
	PreparedStatement preparedStatement;
	ResultSet rs;
	PreparedStatement preparedStatement1;
	ResultSet rs1;
	ResultSetMetaData rsmd;

	public void treatedPatientList(Credential emp, int empId) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String trtdpQuery = "select distinct new_pat.p_id from (select distinct p_id from patients where admit in (select fac_id from facilitydept \r\n" + 
				"where dept_code in (select dept_code from staff where empid = "+ empId +")) and p_id not in(\r\n" + 
				"select distinct p_id from checkout))new_pat join process prc on\r\n" + 
				"new_pat.p_id = prc.p_id where prc.tmt_end is not null\r\n" + 
				"union\r\n" + 
				"select  distinct pr.p_id from process pr join patients p on pr.p_id=p.p_id join \r\n" + 
				"checkout c on pr.p_id=c.p_id where p.admit in (select fac_id from facilitydept \r\n" + 
				"where dept_code in (select dept_code from staff where empid = " +empId+ " ) and pr.tmt_end>c.checkout_time and pr.tmt_end is not null)";
		try {
			
			preparedStatement = connection.prepareStatement(trtdpQuery);
			rs = preparedStatement.executeQuery();
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {					 
				System.out.println("");				
				for (int i = 1; i <= cols; i++) {
					String columnValue = rs.getString(i);
					 String query = "select f_name,l_name from patients where p_id="+columnValue+"";
					 preparedStatement1 = connection.prepareStatement(query);
					 rs1 = preparedStatement1.executeQuery();
					while(rs1.next())
					{
						System.out.println("\t\t"+columnValue+"\t"+rs1.getString("f_name")+"\t"+rs1.getString("l_name"));
					}
					
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Treated Patient Menu \t");
		System.out.println("\t\t----------------------------------");
		System.out.println();
		System.out.println("\t\t\t[1].Check out Patient\n\t\t\t[2].Go Back\n");
		Scanner input = new Scanner(System.in);
		databaseConnection = new DatabaseConnection();
		connection = databaseConnection.getConnection();
		int choice = input.nextInt();
		if (choice == 1) {
			System.out.println();
			System.out.println("\t\t\tEnter the patient id to checkout:");
			int id = input.nextInt();
			String chk = "select * from patients where p_id = " + id + "";
			try {
				preparedStatement = connection.prepareStatement(chk);
				rs = preparedStatement.executeQuery();
				if (!rs.next()) {
					System.out.println("\t\t\tInvalid patient id. Please reselect!!");
					treatedPatientList(emp, empId);
				}
				String ischkedout = "select * from checkout where p_id = " + id + "and checkout_time is null";

				preparedStatement = connection.prepareStatement(ischkedout);
				rs = preparedStatement.executeQuery();

				if (rs.next()) {

					System.out.println("\t\t\tPatient is already checked out.Please choose other patient to checkout!!");
					treatedPatientList(emp, empId);

				}

				StaffCheckoutPatient chout = new StaffCheckoutPatient();
				chout.Patient_Checkout(emp, id, empId);
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			StaffMenu staff = new StaffMenu();
			staff.Menu(emp, empId);
		}
	}
}
