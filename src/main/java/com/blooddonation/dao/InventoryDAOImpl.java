package com.blooddonation.dao;

import com.blooddonation.model.Inventory;
import java.sql.*;
import java.util.*;

public class InventoryDAOImpl implements InventoryDAO {
    @Override
    public Inventory getInventory(int inventoryId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM blood_inventory WHERE inventory_id = ?")) {
            stmt.setInt(1, inventoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Inventory(
                    rs.getInt("inventory_id"),
                    rs.getString("blood_type"),
                    rs.getInt("units"),
                    rs.getDate("collection_date"),
                    rs.getDate("expiry_date"),
                    rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int addInventory(Inventory inventory) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO blood_inventory (blood_type, units, collection_date, expiry_date, status) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, inventory.getBloodType());
            stmt.setInt(2, inventory.getUnits());
            stmt.setDate(3, new java.sql.Date(inventory.getCollectionDate().getTime()));
            stmt.setDate(4, new java.sql.Date(inventory.getExpiryDate().getTime()));
            stmt.setString(5, inventory.getStatus());
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
    public boolean updateInventory(Inventory inventory) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE blood_inventory SET blood_type=?, units=?, collection_date=?, expiry_date=?, status=? WHERE inventory_id=?")) {
            stmt.setString(1, inventory.getBloodType());
            stmt.setInt(2, inventory.getUnits());
            stmt.setDate(3, new java.sql.Date(inventory.getCollectionDate().getTime()));
            stmt.setDate(4, new java.sql.Date(inventory.getExpiryDate().getTime()));
            stmt.setString(5, inventory.getStatus());
            stmt.setInt(6, inventory.getInventoryId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteInventory(int inventoryId) {
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM blood_inventory WHERE inventory_id = ?")) {
            stmt.setInt(1, inventoryId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        try (Connection conn = com.blooddonation.util.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM blood_inventory")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Inventory inv = new Inventory(
                    rs.getInt("inventory_id"),
                    rs.getString("blood_type"),
                    rs.getInt("units"),
                    rs.getDate("collection_date"),
                    rs.getDate("expiry_date"),
                    rs.getString("status")
                );
                inventoryList.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
} 