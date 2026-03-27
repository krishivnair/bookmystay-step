package App;

import java.util.ArrayList;
import java.util.List;

// Actor 1: Renamed to HistoryReservation to avoid clashing with older Use Cases!
class HistoryReservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public HistoryReservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "[" + reservationId + "] " + guestName + " - " + roomType + " Room";
    }
}

// Actor 2: Maintains a chronological record of confirmed reservations using a List
class BookingHistory {
    private List<HistoryReservation> confirmedBookings;

    public BookingHistory() {
        this.confirmedBookings = new ArrayList<>();
    }

    public void addRecord(HistoryReservation reservation) {
        confirmedBookings.add(reservation);
        System.out.println("History Updated: Recorded confirmation for " + reservation.getReservationId());
    }

    public List<HistoryReservation> getRecords() {
        return confirmedBookings;
    }
}

// Actor 3: Generates summaries and reports from stored booking data
class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void displayFullAuditTrail() {
        System.out.println("\n=== Full Booking Audit Trail (Chronological) ===");
        List<HistoryReservation> records = history.getRecords();
        
        if (records.isEmpty()) {
            System.out.println("No booking history available.");
        } else {
            for (int i = 0; i < records.size(); i++) {
                System.out.println((i + 1) + ". " + records.get(i).toString());
            }
        }
        System.out.println("================================================\n");
    }

    public void generateSummaryReport() {
        System.out.println("=== Operational Summary Report ===");
        List<HistoryReservation> records = history.getRecords();
        
        int totalBookings = records.size();
        int deluxeCount = 0, standardCount = 0, suiteCount = 0;

        for (HistoryReservation r : records) {
            switch (r.getRoomType()) {
                case "Deluxe": deluxeCount++; break;
                case "Standard": standardCount++; break;
                case "Suite": suiteCount++; break;
            }
        }

        System.out.println("Total Confirmed Bookings: " + totalBookings);
        System.out.println("- Deluxe Rooms Booked: " + deluxeCount);
        System.out.println("- Standard Rooms Booked: " + standardCount);
        System.out.println("- Suite Rooms Booked: " + suiteCount);
        System.out.println("==================================\n");
    }
}

// Main execution class
public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {
        System.out.println("Starting Book My Stay App - Use Case 8\n");

        BookingHistory bookingHistory = new BookingHistory();
        BookingReportService reportService = new BookingReportService(bookingHistory);

        System.out.println("Simulating live booking confirmations...");
        bookingHistory.addRecord(new HistoryReservation("RES-1001", "Alice Smith", "Deluxe"));
        bookingHistory.addRecord(new HistoryReservation("RES-1002", "Bob Johnson", "Standard"));
        bookingHistory.addRecord(new HistoryReservation("RES-1003", "Charlie Brown", "Standard"));
        bookingHistory.addRecord(new HistoryReservation("RES-1004", "Diana Prince", "Suite"));
        bookingHistory.addRecord(new HistoryReservation("RES-1005", "Evan Wright", "Deluxe"));

        System.out.println("\n[Admin Action] Requesting Reports...");
        reportService.displayFullAuditTrail();
        reportService.generateSummaryReport();
    }
}