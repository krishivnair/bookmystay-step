package App;
import java.util.LinkedList;
import java.util.Queue;

// Actor 1: Represents a guest's intent to book a room.
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room Preference: " + roomType + "]";
    }
}

// Actor 2: Manages and orders incoming booking requests.
class BookingRequestQueue {
    // Queue Data Structure to ensure FIFO (First-Come-First-Served)
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        // LinkedList is a standard implementation of the Queue interface in Java
        this.requestQueue = new LinkedList<>();
    }

    // Goal: Handle multiple booking requests fairly by preserving arrival order
    public void submitRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Request added to queue: " + reservation.getGuestName());
    }

    // Displays the queue to prove order preservation (No inventory mutation occurs)
    public void displayQueue() {
        System.out.println("\n--- Current Booking Queue (Arrival Order) ---");
        if (requestQueue.isEmpty()) {
            System.out.println("The booking queue is currently empty.");
        } else {
            int position = 1;
            for (Reservation req : requestQueue) {
                System.out.println(position + ". " + req.toString());
                position++;
            }
        }
        System.out.println("---------------------------------------------\n");
    }

    // Prepares the earliest request for the next phase (Allocation System)
    public Reservation getNextRequestForAllocation() {
        // poll() retrieves and removes the head of the queue (the oldest request)
        return requestQueue.poll(); 
    }
}

// Main class to test the use case
public class UseCase5BookingRequestQueue {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 5\n");

        BookingRequestQueue bookingSystem = new BookingRequestQueue();

        // Flow Step 1 & 2: Guests submit booking requests, added to the queue
        System.out.println("Receiving simultaneous booking requests during peak demand...");
        bookingSystem.submitRequest(new Reservation("Alice Smith", "Deluxe"));
        bookingSystem.submitRequest(new Reservation("Bob Johnson", "Standard"));
        bookingSystem.submitRequest(new Reservation("Charlie Brown", "Suite"));

        // Flow Step 3: Requests are stored in arrival order
        bookingSystem.displayQueue();

        // Flow Step 4 & 5: Queued requests wait for processing. No inventory mutation yet.
        System.out.println("Sending the first request to the allocation system...");
        Reservation nextToProcess = bookingSystem.getNextRequestForAllocation();
        
        if (nextToProcess != null) {
            System.out.println("Handed over to Allocation System: " + nextToProcess.getGuestName());
        }

        System.out.println("\nQueue status after handing over the first request:");
        bookingSystem.displayQueue();
    }
}