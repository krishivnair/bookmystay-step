package App;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the centralized inventory of all hotel rooms.
 * This encapsulates the state, preventing unauthorized or inconsistent modifications.
 * * @version 3.0
 */
class RoomInventory {
    // Centralized data structure for O(1) lookups
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        // Initializing default inventory upon creation
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Luxury Suite", 1);
    }

    /**
     * Retrieves the current available count for a specific room type.
     */
    public int getAvailableRooms(String roomType) {
        // Returns 0 if the room type doesn't exist in the map
        return inventory.getOrDefault(roomType, 0); 
    }

    /**
     * Controlled update method to change inventory counts safely.
     */
    public void updateInventory(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
            System.out.println("   -> [Update] " + roomType + " availability adjusted to: " + newCount);
        } else {
            System.out.println("   -> [Error] Room type '" + roomType + "' does not exist in inventory.");
        }
    }

    /**
     * Displays the current state of the entire inventory.
     */
    public void displayInventory() {
        System.out.println("\n--- Current Room Inventory ---");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("- " + entry.getKey() + " : " + entry.getValue() + " available");
        }
        System.out.println("------------------------------\n");
    }
}

/**
 * Hotel Booking Management System - Centralized Room Inventory Management
 * Demonstrates the use of HashMap for scalable state management.
 *
 * @author Krishiv
 * @version 3.1
 */
public class UseCase3InventorySetup {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("     Book My Stay - Inventory Manager     ");
        System.out.println("==========================================");

        // 1. Initialize the centralized inventory component
        System.out.println("Status: Initializing Centralized Inventory...");
        RoomInventory centralizedInventory = new RoomInventory();

        // 2. Display the initial state
        centralizedInventory.displayInventory();

        // 3. Demonstrate controlled updates (Simulating a booking and a cancellation/addition)
        System.out.println("Action: Simulating a booking for 1 Double Room...");
        int currentDoubleRooms = centralizedInventory.getAvailableRooms("Double Room");
        centralizedInventory.updateInventory("Double Room", currentDoubleRooms - 1);

        System.out.println("Action: Simulating maintenance finishing on a Single Room...");
        int currentSingleRooms = centralizedInventory.getAvailableRooms("Single Room");
        centralizedInventory.updateInventory("Single Room", currentSingleRooms + 1);

        // 4. Display the updated inventory state to prove the Single Source of Truth works
        centralizedInventory.displayInventory();
        
        System.out.println("==========================================");
        System.out.println("System Status: Inventory management operations completed successfully.");
    }
}