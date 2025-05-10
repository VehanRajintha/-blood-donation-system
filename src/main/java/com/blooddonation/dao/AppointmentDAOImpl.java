package com.blooddonation.dao;

import com.blooddonation.model.Appointment;
import java.sql.*;
import java.util.*;

public class AppointmentDAOImpl implements AppointmentDAO {
    @Override
    public int addAppointment(Appointment appointment) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO appointments (donor_id, appointment_date, appointment_time, status, notes) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getDonorId());
            stmt.setDate(2, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.setString(3, appointment.getAppointmentTime());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());
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
    public boolean updateAppointment(Appointment appointment) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE appointments SET donor_id=?, appointment_date=?, appointment_time=?, status=?, notes=? WHERE appointment_id=?")) {
            stmt.setInt(1, appointment.getDonorId());
            stmt.setDate(2, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.setString(3, appointment.getAppointmentTime());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());
            stmt.setInt(6, appointment.getAppointmentId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAppointment(int appointmentId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE appointment_id = ?")) {
            stmt.setInt(1, appointmentId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Appointment getAppointment(int appointmentId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM appointments WHERE appointment_id = ?")) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("donor_id"),
                    rs.getDate("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    rs.getString("notes")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM appointments")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("donor_id"),
                    rs.getDate("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    rs.getString("notes")
                );
                appointments.add(appt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }
} 