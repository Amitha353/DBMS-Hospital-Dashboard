package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Query3 {
	public void processQuery() {
		DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t Query III \t");
		System.out.println();
		System.out.println("\t\t Facilities send Most Referrals to Hospitals");
		System.out.println("\t\t----------------------------------");
		String query = "select * from (select rl.org_fac, rl.ref_fac, rl.counter, rank() over (PARTITION by rl.org_fac "
				+ "order by rl.counter desc) as referRank from (select res.org_fac, res.ref_fac, count(res.ref_fac) as counter "
				+ "from (select cout.cout_id, pt.admit as org_fac,cout.cout_id, cout.emp_id, cout.p_id, ref.fac_id as ref_fac from "
				+ "checkout cout join patients pt on pt.p_id = cout.p_id join referred ref on ref.cout_id = cout.cout_id)res "
				+ "group by org_fac, res.ref_fac) rl) where referRank <= 1";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= cols; i = i + 4) {
					int original = rs.getInt(i);
					int refered = rs.getInt(i + 1);
					int count = rs.getInt(i + 2);
					int referRank = rs.getInt(i + 3);
					System.out.println("\t\t" + original + "\t" + refered + "\t" + count + "\t" + referRank);
				}
			}
			connection.close();
			DemoQueries queries = new DemoQueries();
			queries.dataVerify();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
