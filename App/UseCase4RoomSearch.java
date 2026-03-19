package App;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Domain Models representing the actual rooms.
 * @version 4.0
 */
abstract class Room {
    private String name;
    private int capacity;
    private double pricePerNight;

    public Room(String name, int capacity, double pricePerNight) {
        this.name = name;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public double getPricePerNight() { return pricePerNight; }

    public abstract void displayRoomFeatures();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 100.00); }
    @Override
    public void displayRoomFeatures() { System.out.println("  - Features: 1 Twin Bed, Standard Wi-Fi"); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 150.00); }
    @Override
    public void displayRoomFeatures() { System.out.println("  - Features: 1 Queen Bed, High-Speed Wi-Fi"); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Luxury Suite", 4, 350.00); }
    @Override
    public void displayRoomFeatures() { System.out.println("  - Features: 1 King Bed, Ocean View, Mini-Bar"); }
}

/**
 * Centralized Inventory acting as the Single Source of Truth for state.
 * @version 4.0
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Luxury Suite", 1); // We will set this to 0 later to test the search filter
    }

    public int getAvailableRooms(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateInventory(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
        }
    }
}

/**
 * NEW: Search Service
 * Handles Read-Only access. It merges domain data (Room objects) with state data (Inventory)
 * to provide a view for the user, without modifying anything.
 * @version 4.0
 */
class SearchService {
    private RoomInventory inventory;
    private List<Room> catalog;

    public SearchService(RoomInventory inventory, List<Room> catalog) {
        this.inventory = inventory;
        this.catalog = catalog;
    }

    /**
     * Searches for and displays rooms that have an availability greater than zero.
     */
    public void displayAvailableRooms() {
        System.out.println("\n--- Available Rooms for Booking ---");
        boolean foundAny = false;

        for (Room room : catalog) {
            // Read-Only Access: We only GET data, we do not PUT/UPDATE.
            int availableCount = inventory.getAvailableRooms(room.getName());

            // Validation Logic: Only show rooms that actually have availability
            if (availableCount > 0) {
                foundAny = true;
                System.out.println("[" + room.getName() + "]");
                System.out.println("  Price: $" + room.getPricePerNight() + " | Capacity: " + room.getCapacity());
                room.displayRoomFeatures();
                System.out.println("  --> Only " + availableCount + " left!");
                System.out.println();
            }
        }

        if (!foundAny) {
            System.out.println("Sorry, the hotel is completely sold out.");
        }
        System.out.println("-----------------------------------");
    }
}

/**
 * Hotel Booking Management System - Room Search & Availability Check
 * Demonstrates Read-Only access, filtering, and separation of concerns.
 *
 * @author Krishiv
 * @version 4.1
 */
public class UseCase4RoomSearch {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("     Book My Stay - Room Search Portal    ");
        System.out.println("==========================================");

        // 1. System Setup
        RoomInventory inventory = new RoomInventory();
        
        List<Room> roomCatalog = new ArrayList<>();
        roomCatalog.add(new SingleRoom());
        roomCatalog.add(new DoubleRoom());
        roomCatalog.add(new SuiteRoom());

        SearchService searchService = new SearchService(inventory, roomCatalog);

        // 2. Simulate a guest searching for rooms normally
        System.out.println("\n[Guest 1] Initiating Room Search...");
        searchService.displayAvailableRooms();

        // 3. Simulate a booking taking the last Luxury Suite, then searching again
        System.out.println("\n[System] Simulating a VIP booking the last Luxury Suite...");
        inventory.updateInventory("Luxury Suite", 0); 

        System.out.println("\n[Guest 2] Initiating Room Search...");
        searchService.displayAvailableRooms(); // The Suite should now be hidden!
        
        System.out.println("==========================================");
        System.out.println("System Status: Search operations completed successfully.");
    }
}