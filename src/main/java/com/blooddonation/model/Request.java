package com.blooddonation.model;

import java.util.Date;

public class Request {
    private int requestId;
    private String requesterName;
    private String bloodType;
    private int unitsNeeded;
    private String hospitalName;
    private String priority;
    private String status;
    private Date requiredByDate;

    public Request() {}

    public Request(int requestId, String requesterName, String bloodType, int unitsNeeded, String hospitalName, String priority, String status, Date requiredByDate) {
        this.requestId = requestId;
        this.requesterName = requesterName;
        this.bloodType = bloodType;
        this.unitsNeeded = unitsNeeded;
        this.hospitalName = hospitalName;
        this.priority = priority;
        this.status = status;
        this.requiredByDate = requiredByDate;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getRequiredByDate() { return requiredByDate; }
    public void setRequiredByDate(Date requiredByDate) { this.requiredByDate = requiredByDate; }
} 