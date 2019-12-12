package com.medicalfacility;

import java.util.Scanner;

public class DemoQueries {
	public void dataVerify() {
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\t\t\tDemo Queries\t");
		System.out.println("\t\t----------------------------------");
		System.out.println("\t\tPlease selection the queries between 1 - 6");
		Scanner scan = new Scanner(System.in);
		int option = scan.nextInt();
		switch (option) {
		case 1:
			Query1 q1 = new Query1();
			q1.processQuery();
			break;
		case 2:
			Query2 q2 = new Query2();
			q2.processQuery();
			break;
		case 3:
			Query3 q3 = new Query3();
			q3.processQuery();
			break;
		case 4:
			Query4 q4 = new Query4();
			q4.processQuery();
			break;
		case 5:
			Query5 q5 = new Query5();
			q5.processQuery();
			break;
		case 6:
			Query6 q6 = new Query6();
			q6.processQuery();
			break;
		default:
			StartPage.startup();
			break;
		}
	}
}
