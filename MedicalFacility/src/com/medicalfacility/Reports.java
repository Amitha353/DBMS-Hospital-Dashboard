package com.medicalfacility;

public class Reports {
	int reportId;
	int patId;
	String bodyPart;
	String symCode;
	String firstOccur;
	float duration;
	String reason;
	String severity;

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getPatId() {
		return patId;
	}

	public void setPatId(int patId) {
		this.patId = patId;
	}

	public String getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(String bodyPart) {
		this.bodyPart = bodyPart;
	}

	public String getSymCode() {
		return symCode;
	}

	public void setSymCode(String symCode) {
		this.symCode = symCode;
	}

	public String getFirstOccur() {
		return firstOccur;
	}

	public void setFirstOccur(String firstOccur) {
		this.firstOccur = firstOccur;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}
}
