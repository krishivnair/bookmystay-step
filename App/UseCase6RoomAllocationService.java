package App;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

// Actor 1: Represents the guest's booking intent
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// Actor 2: Maintains and updates room availability state
class InventoryService {
    // Stores the count of available rooms for each type
    private Map<String, Integer> availableRooms;

    public InventoryService() {
        availableRooms = new HashMap<>();
        // Setting up initial inventory for the hotel
        availableRooms.put("Deluxe", 2); 
        availableRooms.put("Standard", 5);
        availableRooms.put("Suite", 1);
    }

    public boolean isAvailable(String roomType) {
        return availableRooms.getOrDefault(roomType, 0) > 0;
    }

    // Atomic update: reduces inventory count safely
    public void decrementInventory(String roomType) {
        if (isAvailable(roomType)) {
            availableRooms.put(roomType, availableRooms.get(roomType) - 1);
        }
    }
    
    public void displayInventory() {
        System.out.println("Current Inventory: " + availableRooms);
    }
}

// Actor 3: Processes queued requests and performs allocation safely
class BookingService {
    private Queue<Reservation> requestQueue;
    
    // Core Logic: HashMap maps Room Type -> Set of Unique Room IDs
    // The Set guarantees we can NEVER double-book the same room ID.
    private Map<String, Set<String>> allocatedRooms;
    
    private InventoryService inventoryService;
    private int roomCounter = 100;

    public BookingService(InventoryService inventoryService) {
        this.requestQueue = new LinkedList<>();
        this.allocatedRooms = new HashMap<>();
        this.inventoryService = inventoryService;
        
        // Initialize the empty Sets for each room type to hold our assigned IDs
        allocatedRooms.put("Deluxe", new HashSet<>());
        allocatedRooms.put("Standard", new HashSet<>());
        allocatedRooms.put("Suite", new HashSet<>());
    }

    // Adds incoming requests to the FIFO queue (From Use Case 5)
    public void submitRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Queued request for: " + reservation.getGuestName() + " (" + reservation.getRoomType() + ")");
    }

    // Goal: Confirm bookings safely and prevent double-booking
    public void processNextRequest() {
        // 1. Booking request is dequeued from the request queue
        Reservation request = requestQueue.poll();
        if (request == null) {
            System.out.println("No pending requests to process.");
            return;
        }

        String roomType = request.getRoomType();
        String guestName = request.getGuestName();

        System.out.println("\nProcessing allocation for: " + guestName);

        // 2. The system checks availability for the requested room type
        if (inventoryService.isAvailable(roomType)) {
            
            // 3. A unique room ID is generated
            String roomId = roomType.substring(0, 3).toUpperCase() + "-" + (++roomCounter);
            
            Set<String> assignedIdsForType = allocatedRooms.get(roomType);
            
            // 4. Uniqueness Enforcement: Check Set before assigning
            if (!assignedIdsForType.contains(roomId)) {
                // 5. The room ID is recorded to prevent reuse
                assignedIdsForType.add(roomId); 
                
                // 6. Inventory count is decremented immediately
                inventoryService.decrementInventory(roomType); 
                
                // 7. Reservation is confirmed
                System.out.println("SUCCESS: Reservation confirmed! Assigned Room: " + roomId);
            } else {
                System.out.println("ERROR: Room ID collision detected! Preventing double booking.");
            }
        } else {
            System.out.println("FAILED: Allocation failed. No '" + roomType + "' rooms available for " + guestName + ".");
        }
    }
    
    public void displayAllocations() {
        System.out.println("\n--- Allocated Rooms (Zero Double-Bookings) ---");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Booked: " + entry.getValue());
        }
        System.out.println("----------------------------------------------\n");
    }
}

// Main execution class
public class UseCase6RoomAllocationService {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 6\n");

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(inventory);

        System.out.println("Initial State:");
        inventory.displayInventory();
        System.out.println();

        // Queue up 3 guests who all want Deluxe rooms.
        // NOTE: Our inventory only has 2 Deluxe rooms available!
        bookingService.submitRequest(new Reservation("Alice Smith", "Deluxe"));
        bookingService.submitRequest(new Reservation("Bob Johnson", "Deluxe"));
        bookingService.submitRequest(new Reservation("Charlie Brown", "Deluxe")); 

        // Process the requests in FIFO order
        bookingService.processNextRequest(); // Alice gets DLX-101
        bookingService.processNextRequest(); // Bob gets DLX-102
        bookingService.processNextRequest(); // Charlie fails (Inventory empty)

        // Show the final, consistent state of the system
        bookingService.displayAllocations();
        System.out.println("Final State:");
        inventory.displayInventory();
    }
}