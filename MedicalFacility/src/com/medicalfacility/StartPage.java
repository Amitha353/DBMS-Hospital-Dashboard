package com.medicalfacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class StartPage {
	public static void main(String[] args) {
		startup();
	}

	public static void startup() {
		System.out.println("\t=============================================================");
		System.out.println("\t\t\tHospital Dashboard Management Application\t");
		System.out.println("\t=============================================================");
		System.out.println();
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tMENU\t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t[1].Sign In\n\t\t\t[2].Sign Up\n\t\t\t[3].Demo Queries\n\t\t\t[4].Exit\n");
		Scanner input = new Scanner(System.in);
		int casevar = input.nextInt();
		switch (casevar) {
		case 1:
			SignIn sin = new SignIn();
			sin.login();
			break;
		case 2:
			SignUp sup = new SignUp();
			sup.register();
			break;
		case 3:
			DemoQueries queries = new DemoQueries();
			queries.dataVerify();
			break;
		case 4: startup();
			break;
		}
	}
}
