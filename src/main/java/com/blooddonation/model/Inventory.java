package com.blooddonation.model;

import java.util.Date;

public class Inventory {
    private int inventoryId;
    private String bloodType;
    private int units;
    private Date collectionDate;
    private Date expiryDate;
    private String status;

    public Inventory() {}

    public Inventory(int inventoryId, String bloodType, int units, Date collectionDate, Date expiryDate, String status) {
        this.inventoryId = inventoryId;
        this.bloodType = bloodType;
        this.units = units;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public Date getCollectionDate() { return collectionDate; }
    public void setCollectionDate(Date collectionDate) { this.collectionDate = collectionDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 