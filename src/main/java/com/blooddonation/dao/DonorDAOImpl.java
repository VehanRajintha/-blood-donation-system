package com.blooddonation.dao;

import java.util.List;
import java.util.ArrayList;
import com.blooddonation.model.Donor;

public class DonorDAOImpl implements DonorDAO {
    @Override
    public int addDonor(Donor donor) {
        try (java.sql.Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO donors (first_name, last_name, blood_type, gender, phone, email, address, date_of_birth, last_donation_date, is_eligible, medical_conditions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, donor.getFirstName());
            stmt.setString(2, donor.getLastName());
            stmt.setString(3, donor.getBloodType());
            stmt.setString(4, donor.getGender());
            stmt.setString(5, donor.getPhone());
            stmt.setString(6, donor.getEmail());
            stmt.setString(7, donor.getAddress());
            stmt.setDate(8, new java.sql.Date(donor.getDateOfBirth().getTime()));
            stmt.setDate(9, donor.getLastDonationDate() != null ? new java.sql.Date(donor.getLastDonationDate().getTime()) : null);
            stmt.setBoolean(10, donor.isEligible());
            stmt.setString(11, donor.getMedicalConditions());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return 0;
            try (java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateDonor(Donor donor) {
        try (java.sql.Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "UPDATE donors SET first_name=?, last_name=?, blood_type=?, gender=?, phone=?, email=?, address=?, date_of_birth=?, last_donation_date=?, is_eligible=?, medical_conditions=? WHERE donor_id=?")) {
            stmt.setString(1, donor.getFirstName());
            stmt.setString(2, donor.getLastName());
            stmt.setString(3, donor.getBloodType());
            stmt.setString(4, donor.getGender());
            stmt.setString(5, donor.getPhone());
            stmt.setString(6, donor.getEmail());
            stmt.setString(7, donor.getAddress());
            stmt.setDate(8, new java.sql.Date(donor.getDateOfBirth().getTime()));
            stmt.setDate(9, donor.getLastDonationDate() != null ? new java.sql.Date(donor.getLastDonationDate().getTime()) : null);
            stmt.setBoolean(10, donor.isEligible());
            stmt.setString(11, donor.getMedicalConditions());
            stmt.setInt(12, donor.getDonorId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDonor(int donorId) {
        try (java.sql.Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM donors WHERE donor_id = ?")) {
            stmt.setInt(1, donorId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Donor getDonor(int donorId) {
        try (java.sql.Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donors WHERE donor_id = ?")) {
            stmt.setInt(1, donorId);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Donor(
                    rs.getInt("donor_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("blood_type"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getDate("date_of_birth"),
                    rs.getDate("last_donation_date"),
                    rs.getBoolean("is_eligible"),
                    rs.getString("medical_conditions")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public java.util.List<Donor> getAllDonors() {
        java.util.List<Donor> donors = new java.util.ArrayList<>();
        try (java.sql.Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donors")) {
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Donor donor = new Donor(
                    rs.getInt("donor_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("blood_type"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getDate("date_of_birth"),
                    rs.getDate("last_donation_date"),
                    rs.getBoolean("is_eligible"),
                    rs.getString("medical_conditions")
                );
                donors.add(donor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return donors;
    }
} 