package com.blooddonation.model;

import java.util.Date;

public class Donor {
    private int donorId;
    private String firstName;
    private String lastName;
    private String bloodType;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private Date dateOfBirth;
    private Date lastDonationDate;
    private boolean isEligible;
    private String medicalConditions;

    public Donor() {}

    public Donor(int donorId, String firstName, String lastName, String bloodType, String gender, String phone, String email, String address, Date dateOfBirth, Date lastDonationDate, boolean isEligible, String medicalConditions) {
        this.donorId = donorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bloodType = bloodType;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.lastDonationDate = lastDonationDate;
        this.isEligible = isEligible;
        this.medicalConditions = medicalConditions;
    }

    // Getters and setters for all fields
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Date getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(Date lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public boolean isEligible() { return isEligible; }
    public void setEligible(boolean eligible) { isEligible = eligible; }
    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
} 