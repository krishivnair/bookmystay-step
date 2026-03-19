package App;

/**
 * Abstract base class representing a generalized Room.
 * @version 2.0
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

    // Abstract method to enforce specific implementation in subclasses
    public abstract void displayRoomFeatures();
}

/**
 * Concrete implementation for a Single Room.
 * @version 2.0
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 100.00);
    }

    @Override
    public void displayRoomFeatures() {
        System.out.println("- Features: 1 Twin Bed, Standard Wi-Fi, No Window View.");
    }
}

/**
 * Concrete implementation for a Double Room.
 * @version 2.0
 */
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 150.00);
    }

    @Override
    public void displayRoomFeatures() {
        System.out.println("- Features: 1 Queen Bed, High-Speed Wi-Fi, City View.");
    }
}

/**
 * Concrete implementation for a Suite Room.
 * @version 2.0
 */
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Luxury Suite", 4, 350.00);
    }

    @Override
    public void displayRoomFeatures() {
        System.out.println("- Features: 1 King Bed, 1 Sofa Bed, Premium Wi-Fi, Ocean View, Mini-Bar.");
    }
}

/**
 * Hotel Booking Management System - Basic Room Types & Static Availability
 * Demonstrates abstract classes, inheritance, polymorphism, and basic state management.
 * * @author Krishiv
 * @version 2.1
 */
public class UseCase2RoomInitialization {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("       Book My Stay - Room Inventory      ");
        System.out.println("==========================================");

        // 1. Initializing Room Objects (Polymorphism in action)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // 2. Static Availability Representation (Hardcoded state variables)
        int availableSingles = 5;
        int availableDoubles = 3;
        int availableSuites = 1;

        // 3. Displaying Room Details and Availability
        displayRoomInfo(singleRoom, availableSingles);
        displayRoomInfo(doubleRoom, availableDoubles);
        displayRoomInfo(suiteRoom, availableSuites);
        
        System.out.println("==========================================");
        System.out.println("System Status: Inventory loaded successfully.");
    }

    /**
     * Helper method to format and print room details.
     * Accepts the abstract Room type, demonstrating polymorphism.
     */
    private static void displayRoomInfo(Room room, int availability) {
        System.out.println("\nRoom Type: " + room.getName());
        System.out.println("Capacity: " + room.getCapacity() + " Person(s)");
        System.out.println("Price: $" + room.getPricePerNight() + " / night");
        room.displayRoomFeatures();
        System.out.println("--> Currently Available: " + availability);
    }
}