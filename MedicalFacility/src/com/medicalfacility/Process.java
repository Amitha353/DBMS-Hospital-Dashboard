package com.medicalfacility;

public class Process {
	int process_id;
	int patient_id;
	String symcode;
	int empid;
	String checkin;
	String checkinStart;
	String checkinEnd;
	String treatment;
	String tmtStart;
	String tmtEnd;
	int temp;
	int bpDiastolic;
	int bpSystolic;
	String priStatus;

	public int getProcess_id() {
		return process_id;
	}

	public void setProcess_id(int process_id) {
		this.process_id = process_id;
	}

	public int getPatient_id() {
		return patient_id;
	}

	public void setPatient_id(int patient_id) {
		this.patient_id = patient_id;
	}

	public String getSymcode() {
		return symcode;
	}

	public void setSymcode(String symcode) {
		this.symcode = symcode;
	}

	public int getEmpid() {
		return empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public String getCheckin() {
		return checkin;
	}

	public void setCheckin(String checkin) {
		this.checkin = checkin;
	}

	public String getCheckinStart() {
		return checkinStart;
	}

	public void setCheckinStart(String checkinStart) {
		this.checkinStart = checkinStart;
	}

	public String getCheckinEnd() {
		return checkinEnd;
	}

	public void setCheckinEnd(String checkinEnd) {
		this.checkinEnd = checkinEnd;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getTmtStart() {
		return tmtStart;
	}

	public void setTmtStart(String tmtStart) {
		this.tmtStart = tmtStart;
	}

	public String getTmtEnd() {
		return tmtEnd;
	}

	public void setTmtEnd(String tmtEnd) {
		this.tmtEnd = tmtEnd;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public int getBpDiastolic() {
		return bpDiastolic;
	}

	public void setBpDiastolic(int bpDiastolic) {
		this.bpDiastolic = bpDiastolic;
	}

	public int getBpSystolic() {
		return bpSystolic;
	}

	public void setBpSystolic(int bpSystolic) {
		this.bpSystolic = bpSystolic;
	}

	public String getPriStatus() {
		return priStatus;
	}

	public void setPriStatus(String priStatus) {
		this.priStatus = priStatus;
	}
}
