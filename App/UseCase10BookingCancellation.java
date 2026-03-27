package App;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// Actor 1: Represents the booking data (Renamed to avoid collisions with older files)
class BookingRecord {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public BookingRecord(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false; // Bookings are active by default
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }
    
    public void setCancelled(boolean cancelled) { this.isCancelled = cancelled; }
}

// Actor 2: Manages Inventory and handles rollback restorations
class RollbackInventoryService {
    private Map<String, Integer> availableRooms;

    public RollbackInventoryService() {
        availableRooms = new HashMap<>();
        // Starting with zero Deluxe/Suite rooms to prove that cancellation restores them
        availableRooms.put("Deluxe", 0); 
        availableRooms.put("Standard", 1);
        availableRooms.put("Suite", 0);
    }

    // Inventory Restoration: Incremented immediately after cancellation
    public void incrementInventory(String roomType) {
        availableRooms.put(roomType, availableRooms.getOrDefault(roomType, 0) + 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + availableRooms);
    }
}

// Actor 3: Validates cancellations and performs controlled rollback operations
class CancellationService {
    private Map<String, BookingRecord> activeBookings;
    
    // Core Logic: Stack Data Structure for LIFO Rollback of room IDs
    private Stack<String> releasedRoomIds;
    private RollbackInventoryService inventory;

    public CancellationService(RollbackInventoryService inventory) {
        this.activeBookings = new HashMap<>();
        this.releasedRoomIds = new Stack<>();
        this.inventory = inventory;
    }

    // Helper to simulate bookings that were already confirmed in previous steps
    public void setupExistingBooking(BookingRecord record) {
        activeBookings.put(record.getReservationId(), record);
    }

    // Goal: Enable safe cancellation of confirmed bookings
    public void cancelBooking(String reservationId) {
        System.out.println("Processing cancellation for: " + reservationId);

        // 1. Validate reservation existence
        if (!activeBookings.containsKey(reservationId)) {
            System.out.println("-> ERROR: Cancellation failed. Reservation '" + reservationId + "' does not exist.");
            return;
        }

        BookingRecord record = activeBookings.get(reservationId);

        // 2. Prevent cancellation of already cancelled bookings
        if (record.isCancelled()) {
            System.out.println("-> ERROR: Cancellation failed. Reservation '" + reservationId + "' is already cancelled.");
            return;
        }

        // 3. Update booking history to reflect the cancellation
        record.setCancelled(true);

        // 4. The allocated room ID is recorded in a rollback structure (Stack)
        releasedRoomIds.push(record.getRoomId());

        // 5. Inventory count for the corresponding room type is incremented
        inventory.incrementInventory(record.getRoomType());

        System.out.println("-> SUCCESS: Reservation cancelled. Room " + record.getRoomId() + " (" + record.getRoomType() + ") released back to inventory.");
    }

    public void displayRollbackState() {
        System.out.println("\n--- System Rollback State ---");
        System.out.println("Recently Released Rooms (LIFO Stack): " + releasedRoomIds);
        inventory.displayInventory();
        System.out.println("-----------------------------\n");
    }
}

// Main execution class
public class UseCase10BookingCancellation {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 10\n");

        RollbackInventoryService inventory = new RollbackInventoryService();
        CancellationService cancellationService = new CancellationService(inventory);

        // Simulating the system state with 3 existing active bookings
        cancellationService.setupExistingBooking(new BookingRecord("RES-001", "Alice Smith", "Deluxe", "DLX-101"));
        cancellationService.setupExistingBooking(new BookingRecord("RES-002", "Bob Johnson", "Suite", "SUI-301"));
        cancellationService.setupExistingBooking(new BookingRecord("RES-003", "Charlie Brown", "Deluxe", "DLX-102"));

        System.out.println("Initial State before any cancellations:");
        inventory.displayInventory();
        System.out.println();

        // Scenario 1: Valid cancellation (Alice cancels her Deluxe room)
        cancellationService.cancelBooking("RES-001");
        
        // Scenario 2: Valid cancellation (Bob cancels his Suite)
        // Notice how SUI-301 will be pushed to the top of the Stack
        cancellationService.cancelBooking("RES-002");

        // Scenario 3: Invalid cancellation (Trying to cancel a non-existent booking)
        cancellationService.cancelBooking("RES-999");

        // Scenario 4: Invalid cancellation (Alice tries to cancel again)
        cancellationService.cancelBooking("RES-001");

        // Display final state to prove the Stack and Inventory are correct
        cancellationService.displayRollbackState();
    }
}