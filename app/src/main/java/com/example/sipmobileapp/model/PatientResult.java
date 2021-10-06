package com.example.sipmobileapp.model;

public class PatientResult {

    private String error;
    private String errorCode;
    private PatientInfo[] patients;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public PatientInfo[] getPatients() {
        return patients;
    }

    public void setPatients(PatientInfo[] patients) {
        this.patients = patients;
    }

    public class PatientInfo {

        private int sickID;
        private String Date;
        private String patientName;
        private String services;

        public int getSickID() {
            return sickID;
        }

        public void setSickID(int sickID) {
            this.sickID = sickID;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }
    }
}
