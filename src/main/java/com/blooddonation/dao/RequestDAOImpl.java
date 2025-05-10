package com.blooddonation.dao;

import com.blooddonation.model.Request;
import java.sql.*;
import java.util.*;

public class RequestDAOImpl implements RequestDAO {
    @Override
    public int addRequest(Request request) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO blood_requests (requester_name, blood_type, units_needed, hospital_name, priority, status, required_by_date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, request.getRequesterName());
            stmt.setString(2, request.getBloodType());
            stmt.setInt(3, request.getUnitsNeeded());
            stmt.setString(4, request.getHospitalName());
            stmt.setString(5, request.getPriority());
            stmt.setString(6, request.getStatus());
            stmt.setDate(7, new java.sql.Date(request.getRequiredByDate().getTime()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return 0;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
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
    public boolean updateRequest(Request request) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE blood_requests SET requester_name=?, blood_type=?, units_needed=?, hospital_name=?, priority=?, status=?, required_by_date=? WHERE request_id=?")) {
            stmt.setString(1, request.getRequesterName());
            stmt.setString(2, request.getBloodType());
            stmt.setInt(3, request.getUnitsNeeded());
            stmt.setString(4, request.getHospitalName());
            stmt.setString(5, request.getPriority());
            stmt.setString(6, request.getStatus());
            stmt.setDate(7, new java.sql.Date(request.getRequiredByDate().getTime()));
            stmt.setInt(8, request.getRequestId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRequest(int requestId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM blood_requests WHERE request_id = ?")) {
            stmt.setInt(1, requestId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Request getRequest(int requestId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM blood_requests WHERE request_id = ?")) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Request(
                    rs.getInt("request_id"),
                    rs.getString("requester_name"),
                    rs.getString("blood_type"),
                    rs.getInt("units_needed"),
                    rs.getString("hospital_name"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getDate("required_by_date")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM blood_requests")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Request req = new Request(
                    rs.getInt("request_id"),
                    rs.getString("requester_name"),
                    rs.getString("blood_type"),
                    rs.getInt("units_needed"),
                    rs.getString("hospital_name"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getDate("required_by_date")
                );
                requests.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }
} 