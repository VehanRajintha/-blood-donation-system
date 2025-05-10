package com.blooddonation.dao;

import com.blooddonation.model.Inventory;
import java.util.List;

public interface InventoryDAO {
    int addInventory(Inventory inventory);
    boolean updateInventory(Inventory inventory);
    boolean deleteInventory(int inventoryId);
    Inventory getInventory(int inventoryId);
    List<Inventory> getAllInventory();
} 