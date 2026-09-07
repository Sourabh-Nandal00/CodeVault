import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Airconnect {
    static Scanner sc = new Scanner(System.in);
    static Map<String, Flight> flights = new LinkedHashMap<>();
    static Map<Integer, Passenger> passengers = new LinkedHashMap<>();
    static Map<Integer, Ticket> tickets = new LinkedHashMap<>();
    static Map<Integer, Staff> staff = new LinkedHashMap<>();
    static List<String> securityLogs = new ArrayList<>();
    static List<String> serviceReports = new ArrayList<>();

    static int passengerIdSeq = 1;
    static int ticketIdSeq = 1;
    static int staffIdSeq = 1;

    public static void main(String[] args) {
        seedData();

        while (true) {
            System.out.println("\n==================================================");
            System.out.println("             AIRCONNECT AIRPORT SYSTEM");
            System.out.println("==================================================");
            System.out.println("1. Add Flight");
            System.out.println("2. List Flights");
            System.out.println("3. Register Passenger");
            System.out.println("4. Register Staff");
            System.out.println("5. Book Ticket");
            System.out.println("6. Check-in Passenger");
            System.out.println("7. View Passenger Tickets");
            System.out.println("8. Baggage Security Screening");
            System.out.println("9. Flight Status Report");
            System.out.println("10. Airport Operations Report");
            System.out.println("11. Exit");
            System.out.print("Choose an option: ");

            String choice = readLine("").trim();
            if (choice.isEmpty()) {
                System.out.println("\nInput stream ended. Exiting AirConnect.");
                return;
            }

            switch (choice) {
                case "1": addFlightManually(); break;
                case "2": listFlights(); break;
                case "3": registerPassenger(); break;
                case "4": registerStaff(); break;
                case "5": bookTicket(); break;
                case "6": checkIn(); break;
                case "7": viewTickets(); break;
                case "8": baggageScreening(); break;
                case "9": flightStatusReport(); break;
                case "10": airportOperationsReport(); break;
                case "11": System.out.println("Goodbye! Thank you for using AirConnect."); return;
                default: System.out.println("Invalid option. Please choose a valid menu item.");
            }
        }
    }

    static void seedData() {
        addFlight(new Flight("AI101", "Air India", "Delhi", "Mumbai", LocalDateTime.now().plusHours(4), 120, 4200));
        addFlight(new Flight("AI202", "Air India", "Mumbai", "Bengaluru", LocalDateTime.now().plusHours(6), 90, 3800));
        addFlight(new Flight("SG303", "SpiceJet", "Chennai", "Kolkata", LocalDateTime.now().plusHours(8), 110, 4500));

        registerPassenger("Riya Sharma", "P12345", "9876543210");
        registerPassenger("Aman Verma", "P98765", "9123456780");

        staff.put(1, new Staff(1, "Neha Singh", "Operations Manager", "Operations"));
        staff.put(2, new Staff(2, "Rahul Mehta", "Security Officer", "Security"));
        staff.put(3, new Staff(3, "Priya Nair", "Check-in Supervisor", "Passenger Services"));
    }

    static void addFlight(Flight flight) {
        flights.put(flight.flightNo, flight);
    }

    static void addFlightManually() {
        System.out.print("Enter flight number: ");
        String flightNo = readNonEmpty("Flight number");
        System.out.print("Enter airline: ");
        String airline = readNonEmpty("Airline");
        System.out.print("Enter origin city: ");
        String origin = readNonEmpty("Origin city");
        System.out.print("Enter destination city: ");
        String destination = readNonEmpty("Destination city");
        System.out.print("Enter departure date/time (yyyy-MM-dd HH:mm): ");
        LocalDateTime departure = readDateTime();
        System.out.print("Enter total capacity: ");
        int capacity = readPositiveInt("Capacity");
        System.out.print("Enter base fare: ");
        double fare = readPositiveDouble("Base fare");

        Flight flight = new Flight(flightNo, airline, origin, destination, departure, capacity, fare);
        addFlight(flight);
        System.out.println("Flight added successfully: " + flight.flightNo);
    }

    static void listFlights() {
        System.out.println("\nAvailable Flights:");
        if (flights.isEmpty()) {
            System.out.println("No flights available.");
            return;
        }

        for (Flight flight : flights.values()) {
            System.out.printf("%s | %s | %s -> %s | Depart: %s | Seats left: %d | Status: %s | Fare: ₹%.2f%n",
                    flight.flightNo,
                    flight.airline,
                    flight.origin,
                    flight.destination,
                    flight.departure.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")),
                    flight.seatsAvailable,
                    flight.status,
                    flight.baseFare);
        }
    }

    static void registerPassenger() {
        System.out.print("Enter passenger name: ");
        String name = readNonEmpty("Passenger name");
        System.out.print("Enter passport/ID: ");
        String passport = readNonEmpty("Passport/ID");
        System.out.print("Enter phone number: ");
        String phone = readNonEmpty("Phone number");

        registerPassenger(name, passport, phone);
    }

    static void registerPassenger(String name, String passport, String phone) {
        Passenger p = new Passenger(passengerIdSeq++, name, passport, phone);
        passengers.put(p.id, p);
        System.out.println("Passenger registered successfully. Passenger ID: " + p.id);
    }

    static void registerStaff() {
        System.out.print("Enter staff name: ");
        String name = readNonEmpty("Staff name");
        System.out.print("Enter role: ");
        String role = readNonEmpty("Role");
        System.out.print("Enter department: ");
        String department = readNonEmpty("Department");

        Staff s = new Staff(staffIdSeq++, name, role, department);
        staff.put(s.id, s);
        System.out.println("Staff registered successfully. Staff ID: " + s.id);
    }

    static void bookTicket() {
        System.out.print("Enter Passenger ID: ");
        int pid = readInt("Passenger ID");
        Passenger passenger = passengers.get(pid);
        if (passenger == null) {
            System.out.println("Passenger not found. Please register first.");
            return;
        }

        listFlights();
        System.out.print("Enter Flight Number: ");
        String flightNo = readNonEmpty("Flight number");
        Flight flight = flights.get(flightNo.toUpperCase());
        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }
        if (flight.seatsAvailable <= 0) {
            System.out.println("No seats available on this flight.");
            return;
        }

        System.out.print("Enter baggage weight (kg): ");
        double baggageWeight = readPositiveDouble("Baggage weight");
        double baggageFee = flight.calculateBaggageFee(baggageWeight);
        double totalFare = flight.baseFare + baggageFee;
        int seatNo = flight.allocateSeat();

        Ticket ticket = new Ticket(ticketIdSeq++, passenger, flight, seatNo, baggageWeight, totalFare);
        tickets.put(ticket.ticketId, ticket);

        System.out.printf("Booking successful! Ticket ID: %d | Seat: %d | Total Fare: ₹%.2f | Baggage Fee: ₹%.2f%n",
                ticket.ticketId, ticket.seatNo, ticket.fare, baggageFee);
    }

    static void checkIn() {
        System.out.print("Enter Ticket ID: ");
        int id = readInt("Ticket ID");
        Ticket ticket = tickets.get(id);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }
        if (ticket.checkedIn) {
            System.out.println("This passenger is already checked in.");
            return;
        }

        String confirm = readLine("Confirm check-in? (y/n): ").trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Check-in cancelled.");
            return;
        }

        ticket.checkedIn = true;
        ticket.boardingTime = LocalDateTime.now().plusMinutes(45);
        ticket.flight.status = "Boarding";
        System.out.println("Check-in successful for Passenger ID: " + ticket.passenger.id);
        printBoardingPass(ticket);
    }

    static void viewTickets() {
        System.out.print("Enter Passenger ID: ");
        int pid = readInt("Passenger ID");
        Passenger passenger = passengers.get(pid);
        if (passenger == null) {
            System.out.println("Passenger not found.");
            return;
        }

        List<Ticket> passengerTickets = tickets.values().stream()
                .filter(t -> t.passenger.id == pid)
                .collect(Collectors.toList());

        if (passengerTickets.isEmpty()) {
            System.out.println("No tickets found for this passenger.");
            return;
        }

        System.out.println("Tickets for passenger: " + passenger.name);
        for (Ticket t : passengerTickets) {
            System.out.println(t);
        }
    }

    static void baggageScreening() {
        System.out.print("Enter Ticket ID: ");
        int id = readInt("Ticket ID");
        Ticket ticket = tickets.get(id);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        System.out.print("Enter security officer ID: ");
        int staffId = readInt("Staff ID");
        Staff officer = staff.get(staffId);
        if (officer == null) {
            System.out.println("Staff not found.");
            return;
        }

        System.out.print("Is baggage cleared for security? (y/n): ");
        String result = sc.nextLine().trim().toLowerCase();
        if (result.equals("y") || result.equals("yes")) {
            ticket.baggageCleared = true;
            ticket.securityStatus = "CLEARED";
            System.out.println("Baggage cleared successfully.");
        } else {
            ticket.baggageCleared = false;
            ticket.securityStatus = "HOLD";
            System.out.println("Baggage placed on hold for manual inspection.");
        }

        String log = "Ticket " + ticket.ticketId + " | Officer " + officer.name + " | Security: " + ticket.securityStatus;
        securityLogs.add(log);
        serviceReports.add("Baggage review completed for passenger " + ticket.passenger.name);
    }

    static void flightStatusReport() {
        System.out.println("\nFlight Status Report");
        for (Flight flight : flights.values()) {
            System.out.printf("%s | %s -> %s | Status: %s | Seats Left: %d | Booked Tickets: %d%n",
                    flight.flightNo,
                    flight.origin,
                    flight.destination,
                    flight.status,
                    flight.seatsAvailable,
                    countBookingsForFlight(flight.flightNo));
        }
    }

    static void airportOperationsReport() {
        double totalRevenue = tickets.values().stream()
                .mapToDouble(t -> t.fare)
                .sum();

        long checkedInCount = tickets.values().stream()
                .filter(t -> t.checkedIn)
                .count();

        long clearedBaggage = tickets.values().stream()
                .filter(t -> t.baggageCleared)
                .count();

        System.out.println("\nAirport Operations Report");
        System.out.println("Total Flights: " + flights.size());
        System.out.println("Total Passengers: " + passengers.size());
        System.out.println("Total Tickets: " + tickets.size());
        System.out.println("Checked-in Passengers: " + checkedInCount);
        System.out.println("Baggage Cleared: " + clearedBaggage);
        System.out.printf("Total Revenue: ₹%.2f%n", totalRevenue);

        System.out.println("\nSecurity Logs:");
        if (securityLogs.isEmpty()) {
            System.out.println("No security entries yet.");
        } else {
            for (String log : securityLogs) {
                System.out.println("- " + log);
            }
        }

        System.out.println("\nService Reports:");
        if (serviceReports.isEmpty()) {
            System.out.println("No service reports yet.");
        } else {
            for (String report : serviceReports) {
                System.out.println("- " + report);
            }
        }
    }

    static int countBookingsForFlight(String flightNo) {
        return (int) tickets.values().stream()
                .filter(t -> t.flight.flightNo.equals(flightNo))
                .count();
    }

    static void printBoardingPass(Ticket ticket) {
        System.out.println("====================================");
        System.out.println("            BOARDING PASS");
        System.out.println("====================================");
        System.out.println("Passenger: " + ticket.passenger.name + " (ID: " + ticket.passenger.id + ")");
        System.out.println("Passport/ID: " + ticket.passenger.passport);
        System.out.println("Flight: " + ticket.flight.flightNo + " | " + ticket.flight.airline);
        System.out.println("Route: " + ticket.flight.origin + " -> " + ticket.flight.destination);
        System.out.println("Seat: " + ticket.seatNo + " | Gate: G" + (ticket.seatNo % 5 + 1));
        System.out.println("Boarding Time: " + ticket.boardingTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
        System.out.println("Baggage Status: " + ticket.securityStatus);
        System.out.println("====================================");
    }

    static String readLine(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {
            return "";
        }
        return sc.nextLine();
    }

    static int readInt(String fieldName) {
        while (true) {
            String value = readLine("").trim();
            if (value.isEmpty()) {
                System.out.println("No valid input received for " + fieldName + ".");
                return -1;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.print("Invalid " + fieldName + ". Please enter a valid number: ");
            }
        }
    }

    static int readPositiveInt(String fieldName) {
        while (true) {
            int value = readInt(fieldName);
            if (value > 0) {
                return value;
            }
            System.out.print(fieldName + " must be greater than zero. Enter again: ");
        }
    }

    static double readPositiveDouble(String fieldName) {
        while (true) {
            String value = readLine("").trim();
            if (value.isEmpty()) {
                System.out.println("No valid input received for " + fieldName + ".");
                return -1;
            }
            try {
                double num = Double.parseDouble(value);
                if (num > 0) {
                    return num;
                }
                System.out.print(fieldName + " must be greater than zero. Enter again: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid " + fieldName + ". Enter a valid positive number: ");
            }
        }
    }

    static LocalDateTime readDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (true) {
            String value = readLine("").trim();
            if (value.isEmpty()) {
                System.out.println("No valid date/time received.");
                return LocalDateTime.now();
            }
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (Exception e) {
                System.out.print("Invalid date/time. Use format yyyy-MM-dd HH:mm: ");
            }
        }
    }

    static String readNonEmpty(String fieldName) {
        while (true) {
            String value = readLine("").trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.print(fieldName + " cannot be empty. Please enter again: ");
        }
    }

    static class Passenger {
        int id;
        String name;
        String passport;
        String phone;

        Passenger(int id, String name, String passport, String phone) {
            this.id = id;
            this.name = name;
            this.passport = passport;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "Passenger " + id + " | " + name + " | " + passport;
        }
    }

    static class Flight {
        String flightNo;
        String airline;
        String origin;
        String destination;
        LocalDateTime departure;
        int capacity;
        int seatsAvailable;
        double baseFare;
        String status;

        Flight(String flightNo, String airline, String origin, String destination, LocalDateTime departure, int capacity, double baseFare) {
            this.flightNo = flightNo.toUpperCase();
            this.airline = airline;
            this.origin = origin;
            this.destination = destination;
            this.departure = departure;
            this.capacity = capacity;
            this.seatsAvailable = capacity;
            this.baseFare = baseFare;
            this.status = "Scheduled";
        }

        synchronized int allocateSeat() {
            if (seatsAvailable <= 0) {
                throw new IllegalStateException("No seats left for flight " + flightNo);
            }
            seatsAvailable--;
            return capacity - seatsAvailable;
        }

        double calculateBaggageFee(double weightKg) {
            double allowance = 15.0;
            if (weightKg <= allowance) {
                return 0.0;
            }
            return (weightKg - allowance) * 50.0;
        }
    }

    static class Ticket {
        int ticketId;
        Passenger passenger;
        Flight flight;
        int seatNo;
        double baggageKg;
        double fare;
        boolean checkedIn = false;
        LocalDateTime boardingTime = null;
        boolean baggageCleared = false;
        String securityStatus = "PENDING";

        Ticket(int ticketId, Passenger passenger, Flight flight, int seatNo, double baggageKg, double fare) {
            this.ticketId = ticketId;
            this.passenger = passenger;
            this.flight = flight;
            this.seatNo = seatNo;
            this.baggageKg = baggageKg;
            this.fare = fare;
        }

        @Override
        public String toString() {
            return "Ticket " + ticketId + " | " + passenger.name + " | Flight " + flight.flightNo +
                    " | Seat " + seatNo + " | Fare ₹" + String.format("%.2f", fare) +
                    " | Checked In: " + (checkedIn ? "Yes" : "No");
        }
    }

    static class Staff {
        int id;
        String name;
        String role;
        String department;

        Staff(int id, String name, String role, String department) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.department = department;
        }
    }
}