package App;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Custom Exception 1: Thrown when a guest requests a room type that doesn't exist
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

// Custom Exception 2: Thrown when inventory hits zero to prevent negative counts
class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}

// Actor 1: Validates input and system state before processing requests
class InvalidBookingValidator {
    // A fixed list of valid room types for our hotel
    private static final List<String> VALID_ROOM_TYPES = Arrays.asList("Deluxe", "Standard", "Suite");

    // Input Validation: Ensures incoming data conforms to expected rules
    public static void validateRoomType(String roomType) throws InvalidRoomTypeException {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidRoomTypeException("Validation Failed: Room type cannot be empty.");
        }
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidRoomTypeException("Validation Failed: '" + roomType + "' is not a recognized room type.");
        }
    }
}

// Actor 2: Maintains and updates room availability state securely
class SecureInventoryService {
    private Map<String, Integer> availableRooms;

    public SecureInventoryService() {
        availableRooms = new HashMap<>();
        availableRooms.put("Deluxe", 1); // Only 1 Deluxe room left for testing
        availableRooms.put("Standard", 5);
        availableRooms.put("Suite", 2);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + availableRooms);
    }

    // Guarding System State: Checks are performed before inventory updates
    public void allocateRoom(String roomType) throws InsufficientInventoryException {
        int currentStock = availableRooms.getOrDefault(roomType, 0);
        
        if (currentStock <= 0) {
            // System prevents invalid state changes (negative inventory)
            throw new InsufficientInventoryException("Allocation Failed: No '" + roomType + "' rooms are currently available.");
        }
        
        // Safe to decrement
        availableRooms.put(roomType, currentStock - 1);
    }
}

// Main execution class
public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 9\n");

        SecureInventoryService inventory = new SecureInventoryService();
        inventory.displayInventory();
        System.out.println();

        // Scenario 1: Valid Booking (Happy Path)
        processBooking("Alice Smith", "Suite", inventory);

        // Scenario 2: Invalid Input (Guest mistypes the room)
        processBooking("Bob Johnson", "Presidential", inventory);

        // Scenario 3: Valid Booking that drains inventory to zero
        processBooking("Charlie Brown", "Deluxe", inventory);

        // Scenario 4: Inventory Depleted (Preventing negative stock)
        processBooking("Diana Prince", "Deluxe", inventory);

        System.out.println("\nSystem remained stable after handling errors.");
        inventory.displayInventory();
    }

    // Helper method to process bookings and demonstrate Graceful Failure Handling
    private static void processBooking(String guestName, String roomType, SecureInventoryService inventory) {
        System.out.println("Processing request for: " + guestName + " (" + roomType + ")");
        
        try {
            // 1. System validates input values (Fail-Fast)
            InvalidBookingValidator.validateRoomType(roomType);
            
            // 2. System validates system constraints (Inventory check)
            inventory.allocateRoom(roomType);
            
            // 3. Success
            System.out.println("-> SUCCESS: Room allocated successfully for " + guestName + ".\n");
            
        } catch (InvalidRoomTypeException | InsufficientInventoryException e) {
            // 4. If validation fails, an error is raised immediately and caught here
            // 5. A meaningful failure message is displayed safely
            System.out.println("-> ERROR CAUGHT: " + e.getMessage() + "\n");
        }
    }
}