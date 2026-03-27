package App;

import java.io.*;
import java.util.*;

// Actor 1: Represents a booking. MUST implement Serializable to be saved to a file.
class PersistentReservation implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
    
    private String reservationId;
    private String guestName;
    private String roomType;

    public PersistentReservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "[" + reservationId + "] " + guestName + " (" + roomType + ")";
    }
}

// Actor 2: A wrapper class holding the ENTIRE system state so we only have to save one object
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    public Map<String, Integer> inventory;
    public List<PersistentReservation> bookingHistory;

    public SystemState() {
        this.inventory = new HashMap<>();
        this.bookingHistory = new ArrayList<>();
        
        // Default startup inventory if no file exists
        inventory.put("Deluxe", 2);
        inventory.put("Standard", 5);
        inventory.put("Suite", 1);
    }
}

// Actor 3: Handles storing and retrieving system state from persistent storage
class PersistenceService {
    private static final String DATA_FILE = "hotel_system_state.ser";

    // Serialize the state and write it to a file
    public void saveState(SystemState state) {
        System.out.println("Initiating system shutdown sequence...");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(state);
            System.out.println("-> SUCCESS: System state successfully persisted to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("-> ERROR: Failed to save system state: " + e.getMessage());
        }
    }

    // Load the state from the file, or handle a missing file gracefully
    public SystemState loadState() {
        System.out.println("System starting up... Checking for persistent data...");
        File file = new File(DATA_FILE);
        
        // Failure Tolerance: Handle missing persistence data safely
        if (!file.exists()) {
            System.out.println("-> INFO: No previous data found. Booting with fresh system state.");
            return new SystemState(); // Return default state
        }

        // Deserialize the data back into memory
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            SystemState recoveredState = (SystemState) in.readObject();
            System.out.println("-> SUCCESS: Previous system state completely restored!");
            return recoveredState;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("-> WARNING: Data file corrupted or unreadable. Booting with fresh system state.");
            return new SystemState();
        }
    }
}

// Main execution class
public class UseCase12DataPersistenceRecovery {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 12\n");

        PersistenceService persistenceService = new PersistenceService();
        
        // 1. System starts up and attempts to load state
        SystemState appState = persistenceService.loadState();
        
        System.out.println("\n--- Current System State ---");
        System.out.println("Inventory: " + appState.inventory);
        System.out.println("Booking History: " + appState.bookingHistory);
        System.out.println("----------------------------\n");

        // 2. Simulate business operations occurring while the app is running
        if (appState.bookingHistory.isEmpty()) {
            System.out.println("Processing new bookings...");
            appState.bookingHistory.add(new PersistentReservation("RES-101", "Alice Smith", "Deluxe"));
            appState.inventory.put("Deluxe", appState.inventory.get("Deluxe") - 1); // Decrease inventory
            
            appState.bookingHistory.add(new PersistentReservation("RES-102", "Bob Johnson", "Suite"));
            appState.inventory.put("Suite", appState.inventory.get("Suite") - 1);
            
            System.out.println("Bookings processed successfully.\n");
            
            // 3. System prepares for shutdown and saves state
            persistenceService.saveState(appState);
            System.out.println("\n[System goes offline...]");
            System.out.println("Run this program a second time to see the data successfully recovered!");
        } else {
            System.out.println("Operations resumed normally using restored data. No data was lost!");
        }
    }
}