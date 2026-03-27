package App;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// Actor 1: Represents the booking request
class ConcurrentRequest {
    private String guestName;
    private String roomType;

    public ConcurrentRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// Actor 2: Shared mutable state (Inventory) protected by synchronization
class ThreadSafeInventory {
    private Map<String, Integer> availableRooms;

    public ThreadSafeInventory() {
        availableRooms = new HashMap<>();
        // We only have 2 Deluxe rooms, but we will have 5 guests trying to book them concurrently!
        availableRooms.put("Deluxe", 2); 
    }

    // CRITICAL SECTION: The 'synchronized' keyword ensures only ONE thread can execute this at a time.
    // Without this, multiple threads could read 'stock > 0' simultaneously and cause double-booking.
    public synchronized void allocateRoom(ConcurrentRequest request) {
        String roomType = request.getRoomType();
        String guestName = request.getGuestName();
        int currentStock = availableRooms.getOrDefault(roomType, 0);

        System.out.println("[" + Thread.currentThread().getName() + "] Checking availability for " + guestName + "...");

        if (currentStock > 0) {
            // Simulating a slight processing delay. If this method wasn't synchronized, 
            // a race condition would almost certainly occur right here!
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            availableRooms.put(roomType, currentStock - 1);
            System.out.println(" -> SUCCESS: " + roomType + " allocated to " + guestName + ".");
        } else {
            System.out.println(" -> FAILED: No " + roomType + " rooms left for " + guestName + ".");
        }
    }
    
    public void displayFinalInventory() {
        System.out.println("Final Inventory State: " + availableRooms);
    }
}

// Actor 3: Shared Booking Queue ensuring safe access
class SharedBookingQueue {
    private Queue<ConcurrentRequest> queue;

    public SharedBookingQueue() {
        queue = new LinkedList<>();
    }

    public synchronized void addRequest(ConcurrentRequest request) {
        queue.add(request);
    }

    // Synchronized to ensure two threads don't pull the exact same request off the queue
    public synchronized ConcurrentRequest getNextRequest() {
        return queue.poll();
    }
}

// Actor 4: The thread worker that processes bookings concurrently
class ConcurrentBookingProcessor implements Runnable {
    private SharedBookingQueue bookingQueue;
    private ThreadSafeInventory inventory;

    public ConcurrentBookingProcessor(SharedBookingQueue bookingQueue, ThreadSafeInventory inventory) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        ConcurrentRequest request;
        // Thread safely retrieves the next request from the shared queue
        while ((request = bookingQueue.getNextRequest()) != null) {
            // Enters the critical section to update inventory
            inventory.allocateRoom(request);
        }
    }
}

// Main execution class
public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 11\n");

        ThreadSafeInventory inventory = new ThreadSafeInventory();
        SharedBookingQueue queue = new SharedBookingQueue();

        // 1. Multiple guests submit booking requests simultaneously
        queue.addRequest(new ConcurrentRequest("Alice Smith", "Deluxe"));
        queue.addRequest(new ConcurrentRequest("Bob Johnson", "Deluxe"));
        queue.addRequest(new ConcurrentRequest("Charlie Brown", "Deluxe"));
        queue.addRequest(new ConcurrentRequest("Diana Prince", "Deluxe"));
        queue.addRequest(new ConcurrentRequest("Evan Wright", "Deluxe"));

        System.out.println("Spinning up 3 concurrent processing threads...\n");

        // 2. Create multiple threads acting as concurrent booking processors
        Thread processor1 = new Thread(new ConcurrentBookingProcessor(queue, inventory), "Thread-1");
        Thread processor2 = new Thread(new ConcurrentBookingProcessor(queue, inventory), "Thread-2");
        Thread processor3 = new Thread(new ConcurrentBookingProcessor(queue, inventory), "Thread-3");

        // 3. Start the threads (This begins the concurrent execution)
        processor1.start();
        processor2.start();
        processor3.start();

        // 4. Wait for all threads to finish processing before showing final state
        try {
            processor1.join();
            processor2.join();
            processor3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nAll concurrent processing completed cleanly.");
        inventory.displayFinalInventory();
    }
}