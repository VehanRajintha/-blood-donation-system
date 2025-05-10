package com.blooddonation.dao;

import com.blooddonation.model.Donor;
import java.util.List;

public interface DonorDAO {
    int addDonor(Donor donor); // Returns new donor ID
    boolean updateDonor(Donor donor);
    boolean deleteDonor(int donorId);
    Donor getDonor(int donorId);
    List<Donor> getAllDonors();
} 