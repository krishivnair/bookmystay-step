package App;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Actor 1: Represents an individual optional offering
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }

    @Override
    public String toString() {
        return serviceName + " ($" + cost + ")";
    }
}

// Actor 2: Manages the association between reservations and selected services
class AddOnServiceManager {
    // Core Logic: Map a single reservation ID to a List of AddOnServices
    // This perfectly models the One-to-Many relationship
    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        this.reservationServicesMap = new HashMap<>();
    }

    // Guest selects one or more add-on services
    public void addService(String reservationId, AddOnService service) {
        // If the reservation doesn't have a list of services yet, create an empty one
        reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());
        
        // Add the selected service to the list
        reservationServicesMap.get(reservationId).add(service);
        System.out.println("Added '" + service.getServiceName() + "' to Reservation: " + reservationId);
    }

    // Additional cost for the reservation is calculated
    public double calculateTotalAddOnCost(String reservationId) {
        double totalCost = 0.0;
        // Retrieve the list of services, or an empty list if none exist
        List<AddOnService> services = reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
        
        for (AddOnService service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    // Display the mapped services and total cost without modifying core booking logic
    public void displayInvoice(String reservationId) {
        System.out.println("\n--- Add-On Invoice for Reservation: " + reservationId + " ---");
        List<AddOnService> services = reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
        
        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
        } else {
            for (AddOnService service : services) {
                System.out.println("- " + service.toString());
            }
        }
        System.out.println("Total Add-On Cost: $" + calculateTotalAddOnCost(reservationId));
        System.out.println("--------------------------------------------------\n");
    }
}

// Main execution class
public class UseCase7AddOnServiceSelection {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 7\n");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Pre-define some available real-world services
        AddOnService breakfast = new AddOnService("Buffet Breakfast", 25.0);
        AddOnService spa = new AddOnService("Spa Access", 50.0);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 40.0);
        AddOnService lateCheckout = new AddOnService("Late Checkout", 30.0);

        // Flow Step 1 & 2: Guest (Reservation DLX-101) selects services
        System.out.println("Guest DLX-101 is selecting services...");
        serviceManager.addService("DLX-101", breakfast);
        serviceManager.addService("DLX-101", airportPickup);

        // Simulating another guest (Reservation STD-205) selecting different services
        System.out.println("\nGuest STD-205 is selecting services...");
        serviceManager.addService("STD-205", spa);
        serviceManager.addService("STD-205", lateCheckout);
        serviceManager.addService("STD-205", breakfast);

        // Flow Step 3 & 4: Display mappings and calculate additional costs
        serviceManager.displayInvoice("DLX-101");
        serviceManager.displayInvoice("STD-205");
        
        // Demonstrating a guest with no add-ons (State remains unchanged)
        serviceManager.displayInvoice("SUI-301");
    }
}