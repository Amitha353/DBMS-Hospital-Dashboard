package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreatPatient {
	public void treatmentEligibility(Credential emp, int selectedPatient, int empId) {
		patientReportSymptoms(emp, selectedPatient, empId);
		StaffProcessPatient staffProcess = new StaffProcessPatient();
		staffProcess.Menu(emp, empId);
	}

	private void patientReportSymptoms(Credential emp, int selectedPatient, int empId) {
		int process_id = 0;
		String reportids = "";
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		HashMap<String, String> employeeDepartment = new HashMap<>();
		String patReportSym = "select res.process_id, res.p_id, res.f_name, res.l_name, res.reportids from "
				+ "(select proc.process_id, proc.p_id, pt.f_name, pt.l_name, proc.reportids from "
				+ "process proc join patients pt on proc.p_id = pt.p_id where checkin_end is not null and treatment "
				+ "is null and proc.p_id = " + selectedPatient + " order by process_id desc) res where rownum < 2";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(patReportSym);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 5) {
					process_id = rs.getInt(i);
					int patient_id = rs.getInt(i + 1);
					String p_fname = rs.getString(i + 2);
					String p_lname = rs.getString(i + 3);
					reportids = rs.getString(i + 4);
//					System.out.println(process_id + " " + patient_id + " " + p_fname + " " + p_lname + " " + reportids);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String empWorkDepartmentQuery = "select s.empid,s.dept_code, dept.name from staff s join service_department dept"
				+ " on dept.dept_code = s.dept_code where s.empid = " + empId
				+ " union select sev.emp_id, sev.dept_code, dept.name from sec_serv_department sev"
				+ " join service_department dept on dept.dept_code = sev.dept_code where sev.emp_id = " + empId;

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(empWorkDepartmentQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 3) {
					String deptCode = rs.getString(i + 1);
					String deptName = rs.getString(i + 2);
//					System.out.println(empId + " " + deptCode + " " + deptName);
					employeeDepartment.put(deptCode, deptName);
					if (deptCode.equals("GP000")) {
						updateTreatment(process_id, empId);
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		processPatient(process_id, empId, selectedPatient, employeeDepartment, reportids);
	}

	private void processPatient(int processId, int employeeId, int selectedPatient, HashMap<String, String> empDeparts,
			String reportids) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		Map<String, String> patientSymptomDept = new HashMap<>();
		String processQuery = "Select rep.report_id, rep.p_id, p.f_name,p.l_name, rep.sym_code, rep.b_code, dept.dept_code, dept.name from reports rep"
				+ " join patients p on p.p_id = rep.p_id join body_part b on  b.body_code = rep.b_code "
				+ "join service_department dept on dept.dept_code = b.dept_code and\r\n"
				+ "rep.report_id in (select report_id from reports where report_id in (SELECT trim(regexp_substr((select reportids from process where process_id= "
				+ processId + "), '[^,]+', 1, LEVEL)) str_2_tab"
				+ " FROM dual CONNECT BY LEVEL <= LENGTH((select reportids from process where process_id = " + processId
				+ ")) - LENGTH(REPLACE((select reportids from process where process_id= " + processId
				+ "), ',', ''))+1))";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(processQuery);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 8) {
					int rep_id = rs.getInt(i);
					int pt_id = rs.getInt(i + 1);
					String fName = rs.getString(i + 2);
					String lName = rs.getString(i + 3);
					String symCode = rs.getString(i + 4);
					String bodyCode = rs.getString(i + 5);
					String deptCode = rs.getString(i + 6);
					String deptName = rs.getString(i + 7);
					patientSymptomDept.put(deptCode, deptName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Set<String> deptMatchCode = new HashSet<String>(empDeparts.keySet());
		deptMatchCode.retainAll(patientSymptomDept.keySet());
		if (deptMatchCode.size() > 0) {
		updateTreatment(processId, employeeId);
		} else {
			System.out.println();
			System.out.println("\t\t\tDepartment out of Medical Staff clearence");
		}
	}

	private void updateTreatment(int process_id, int empId) {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		String updateTreatment = "Update Process set treatment = 'T', t_emp_id = " + empId
				+ ", tmt_start = SYSDATE, tmt_end = SYSDATE where process_id = " + process_id;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(updateTreatment);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
