import java.util.Scanner;

public class Main {

    private static final int DEFAULT_ROWS = 5;
    private static final int DEFAULT_COLS = 6;
    private static final String[] DEFAULT_MOVIES = {"Superman", "Avatar", "Minecraft", "Inside Out", "F1"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Welcome to the Cinema Booking System");
        System.out.println("==========================================\n");

        // Ask whether to use custom dimensions & movies or static defaults
        String customChoice;
        while (true) {
            System.out.print("Do you want custom rows, columns, and movies? (y/n): ");
            customChoice = scanner.nextLine().trim().toLowerCase();
            if (customChoice.equals("y") || customChoice.equals("yes")
                    || customChoice.equals("n") || customChoice.equals("no")) {
                break;
            }
            System.out.println("Error: Please enter 'y', 'yes', 'n', or 'no'.\n");
        }

        int rows = DEFAULT_ROWS;
        int cols = DEFAULT_COLS;
        String[] movieNames = DEFAULT_MOVIES;

        if (customChoice.equals("y") || customChoice.equals("yes")) {
            rows = readPositiveInt(scanner, "Enter number of rows: ");
            cols = readPositiveInt(scanner, "Enter number of seats per row (columns): ");

            int movieCount = readPositiveInt(scanner, "Enter number of movies: ");
            movieNames = new String[movieCount];
            for (int i = 0; i < movieCount; i++) {
                movieNames[i] = readUniqueMovieName(
                        scanner,
                        "Enter name for movie #" + (i + 1) + ": ",
                        movieNames,
                        i
                );
            }
        }

        // 2D array representing seats ('O' = Available, 'X' = Booked)
        char[][] seats = new char[rows][cols];
        initializeSeats(seats);

        int choice;
        do {
            printMenu();
            choice = readIntInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    displaySeats(seats);
                    break;
                case 2:
                    bookSeat(scanner, seats);
                    break;
                case 3:
                    cancelBooking(scanner, seats);
                    break;
                case 4:
                    showMovies(movieNames);
                    break;
                case 5:
                    showSeatCounts(seats);
                    break;
                case 0:
                    System.out.println("\nThank you for using the Cinema Ticket Booking System. Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select an option from the menu.");
            }
        } while (choice != 0);

        scanner.close();
    }

    // Initializes all seats to 'O' (Available)
    private static void initializeSeats(char[][] seats) {
        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {
                seats[r][c] = 'O';
            }
        }
    }

    // Prints the main menu
    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Display Seats");
        System.out.println("2. Book Seat");
        System.out.println("3. Cancel Booking");
        System.out.println("4. Show all movies.");
        System.out.println("5. Show number of available and booked seats");
        System.out.println("0. Exit");
    }

    // Displays the seating chart
    private static void displaySeats(char[][] seats) {
        int rows = seats.length;
        int cols = seats[0].length;

        System.out.println("\n=== Seating Chart ('O' = Available, 'X' = Booked) ===");
        System.out.print("      ");
        for (int c = 1; c <= cols; c++) {
            System.out.printf("Seat %-2d ", c);
        }
        System.out.println();

        for (int r = 0; r < rows; r++) {
            System.out.printf("Row %d: ", r + 1);
            for (int c = 0; c < cols; c++) {
                System.out.printf("  [%c]   ", seats[r][c]);
            }
            System.out.println();
        }
        checkAlmostFull(seats);
    }

    // Books a seat after validating row and seat numbers
    private static void bookSeat(Scanner scanner, char[][] seats) {
        int[] coords = promptAndValidateSeat(scanner, seats);
        if (coords == null) {
            return;
        }

        int rowIdx = coords[0];
        int colIdx = coords[1];

        if (seats[rowIdx][colIdx] == 'X') {
            System.out.println("Seat " + (rowIdx + 1) + (colIdx + 1) + " is already booked.");
        } else {
            seats[rowIdx][colIdx] = 'X';
            System.out.println("Seat " + (rowIdx + 1) + (colIdx + 1) + " booked successfully!");
            checkAlmostFull(seats);
        }
    }

    // Cancels an existing booking after validating row and seat numbers
    private static void cancelBooking(Scanner scanner, char[][] seats) {
        int[] coords = promptAndValidateSeat(scanner, seats);
        if (coords == null) {
            return;
        }

        int rowIdx = coords[0];
        int colIdx = coords[1];

        if (seats[rowIdx][colIdx] == 'O') {
            System.out.println("Seat " + (rowIdx + 1) + (colIdx + 1) + " is not currently booked.");
        } else {
            seats[rowIdx][colIdx] = 'O';
            System.out.println("Booking for seat " + (rowIdx + 1) + (colIdx + 1) + " has been cancelled.");
        }
    }

    // Displays all movies
    private static void showMovies(String[] movieNames) {
        System.out.println("\n=== Now Showing ===");
        for (int i = 0; i < movieNames.length; i++) {
            System.out.println((i + 1) + ". " + movieNames[i]);
        }
    }

    // Displays counts of available and booked seats + occupancy warning
    private static void showSeatCounts(char[][] seats) {
        int booked = countBookedSeats(seats);
        int total = seats.length * seats[0].length;
        int available = total - booked;

        System.out.println("\n=== Seat Summary ===");
        System.out.println("Available seats: " + available);
        System.out.println("Booked seats:    " + booked);
        System.out.println("Total seats:     " + total);
        checkAlmostFull(seats);
    }

    // Bonus: Displays "Almost Full" when occupancy exceeds 80%
    private static void checkAlmostFull(char[][] seats) {
        int booked = countBookedSeats(seats);
        int total = seats.length * seats[0].length;
        double occupancy = (double) booked / total;

        if (occupancy > 0.80) {
            System.out.printf("Almost Full (%.1f%% booked)%n", occupancy * 100);
        }
    }

    // Counts booked ('X') seats
    private static int countBookedSeats(char[][] seats) {
        int booked = 0;
        for (char[] row : seats) {
            for (char seat : row) {
                if (seat == 'X') {
                    booked++;
                }
            }
        }
        return booked;
    }

    // Prompts for a seat number like "12" (row 1, col 2) and validates bounds
    private static int[] promptAndValidateSeat(Scanner scanner, char[][] seats) {
        int rows = seats.length;
        int cols = seats[0].length;

        System.out.print("Enter seat number (e.g., 12 for Row 1, Seat 2): ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{2,}")) {
            System.out.println("Invalid Seat.");
            return null;
        }

        int col = input.charAt(input.length() - 1) - '0';
        int row = Integer.parseInt(input.substring(0, input.length() - 1));

        if (row < 1 || row > rows || col < 1 || col > cols) {
            System.out.println("Invalid Seat.");
            return null;
        }

        return new int[] { row - 1, col - 1 };
    }

    // Helper to safely read an integer (rejects empty input or non-numbers)
    private static int readIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Error: Input cannot be empty. Please enter a number.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    // Helper to read a positive integer (> 0) for dimensions and counts
    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int val = readIntInput(scanner, prompt);
            if (val > 0) {
                return val;
            }
            System.out.println("Error: Please enter a number greater than 0.");
        }
    }

    // Helper to ensure movie names are non-empty and not duplicated
    private static String readUniqueMovieName(Scanner scanner, String prompt, String[] existingMovies, int countSoFar) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Error: Movie name cannot be empty. Please enter a value.");
                continue;
            }

            boolean duplicate = false;
            for (int j = 0; j < countSoFar; j++) {
                if (existingMovies[j] != null && existingMovies[j].equalsIgnoreCase(value)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                System.out.println("Error: \"" + value + "\" has already been entered. Please enter a different movie name.");
            } else {
                return value;
            }
        }
    }
}