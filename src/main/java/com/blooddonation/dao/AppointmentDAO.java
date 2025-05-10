package com.blooddonation.dao;

import com.blooddonation.model.Appointment;
import java.util.List;

public interface AppointmentDAO {
    int addAppointment(Appointment appointment);
    boolean updateAppointment(Appointment appointment);
    boolean deleteAppointment(int appointmentId);
    Appointment getAppointment(int appointmentId);
    List<Appointment> getAllAppointments();
} 