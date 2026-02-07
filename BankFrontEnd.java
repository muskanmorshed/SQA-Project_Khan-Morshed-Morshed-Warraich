/**
 * BankFrontEnd.java
 - Provides a command-line user interface for interacting with the banking system.
 - This class displays menus, reads user input, enforces session rules
   (login/logout, admin vs standard), and passes all business logic
   to the BankService.
 - Input is read from keyboard input, and output is written onto the console.
 */
import java.util.Scanner;

public class BankFrontEnd {
    private final BankService service; // service layer that performs all the banking operations
    private final Session session; // tracks the current session
    private final Scanner input = new Scanner(System.in); // scanner to read user input

    /**
     * Constructs the front-end interface
     * @param service Banking service handling transaction logic
     * @param session Session object tracking login state and permissions
     */
    public BankFrontEnd(BankService service, Session session) {
        this.service = service;
        this.session = session;
    }

    /* Displays the menu to the user*/
    private void showMenu() {
        System.out.println();
        System.out.println("Select a transaction:");
        System.out.println("1 - login");
        System.out.println("2 - withdrawal");
        System.out.println("3 - transfer");
        System.out.println("4 - paybill");
        System.out.println("5 - deposit");
        System.out.println("6 - create (admin)");
        System.out.println("7 - delete (admin)");
        System.out.println("8 - disable (admin)");
        System.out.println("9 - changeplan (admin)");
        System.out.println("0 - logout");
    }
    
    /**
     * Maps a numeric menu choice to an internal transaction code
     * @param choice User-selected menu option
     * @return Corresponding transaction code, or null if invalid
     */
    private String mapChoiceToCode(String choice) {
        switch (choice) {
            case "1": return "login";
            case "2": return "withdrawal";
            case "3": return "transfer";
            case "4": return "paybill";
            case "5": return "deposit";
            case "6": return "create";
            case "7": return "delete";
            case "8": return "disable";
            case "9": return "changeplan";
            case "0": return "logout";
            default:  return null;
        }
    }
    
    /**
     * Main execution loop for the front-end
     * Continuously displays the menu, processes user input,
       enforces login rules, and dispatches transaction flows
     */
    public void run() {
        System.out.println("Bank Front End");
    
        while (true) {
            showMenu();                 // one option per line
            System.out.print("Choice: ");
            String choice = safeLine();
    
            String code = mapChoiceToCode(choice);
            if (code == null) {
                System.out.println("Invalid choice.");
                continue;
            }
    
            // Enforce: must login first
            if (!session.isLoggedIn()) {
                if ("login".equals(code)) {
                    loginFlow();
                } else {
                    System.out.println("Please login first.");
                }
                continue;
            }
    
            // Enforce: no second login until logout
            if ("login".equals(code)) {
                System.out.println("Already logged in. Logout first.");
                continue;
            }
    
            switch (code) {
                case "withdrawal": withdrawalFlow(); break;
                case "transfer": transferFlow(); break;
                case "paybill": paybillFlow(); break;
                case "deposit": depositFlow(); break;
    
                case "create": createFlow(); break;
                case "delete": deleteFlow(); break;
                case "disable": disableFlow(); break;
                case "changeplan": changeplanFlow(); break;
    
                case "logout": logoutFlow(); break;
    
                default: System.out.println("Unknown transaction."); break;
            }
        }
    }
    
    /*Handles the login process for standard and admin users*/
    private void loginFlow() {
        if (session.isLoggedIn()) {
            System.out.println("Already logged in. Logout first.");
            return;
        }

        System.out.print("Session type (standard/admin): ");
        String kind = safeLine().toLowerCase();

        if ("admin".equals(kind)) {
            session.loginAdmin();
            System.out.println("Admin login accepted.");
            return;
        }

        if ("standard".equals(kind)) {
            System.out.print("Account holder name: ");
            String name = safeLine();
            session.loginStandard(name);
            System.out.println("Standard login accepted.");
            return;
        }

        System.out.println("Invalid session type.");
    }

    /* Handles user logout: applies pending deposits, writes the 
       transaction file and ends the current session. */
    private void logoutFlow() {
        if (!session.isLoggedIn()) {
            System.out.println("Not logged in.");
            return;
        }

        // Apply deposits now (so they were NOT usable in session)
        service.applyPendingDeposits();

        // Write transaction file and end with 00
        service.writeTransactionsAtLogout();

        session.logout();
        System.out.println("Logged out. Transactions written.");
    }

    /*Handles withdrawal transactions*/
    private void withdrawalFlow() {
        try {
            String nameForAdmin = "";
            if (session.isAdmin()) {
                System.out.print("Account holder name: ");
                nameForAdmin = safeLine();
            }

            System.out.print("Account number: ");
            String acct = safeLine();

            Double amt = readDouble("Amount to withdraw: ");
            if (amt == null) { System.out.println("Bad amount."); return; }

            service.withdrawal(session, nameForAdmin, acct, amt);
            System.out.println("Withdrawal recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles transfer transactions*/
    private void transferFlow() {
        try {
            String nameForAdmin = "";
            if (session.isAdmin()) {
                System.out.print("Account holder name: ");
                nameForAdmin = safeLine();
            }

            System.out.print("From account number: ");
            String from = safeLine();

            System.out.print("To account number: ");
            String to = safeLine();

            Double amt = readDouble("Amount to transfer: ");
            if (amt == null) { System.out.println("Bad amount."); return; }

            service.transfer(session, nameForAdmin, from, to, amt);
            System.out.println("Transfer recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles bill payement transactions*/
    private void paybillFlow() {
        try {
            String nameForAdmin = "";
            if (session.isAdmin()) {
                System.out.print("Account holder name: ");
                nameForAdmin = safeLine();
            }

            System.out.print("Account number: ");
            String acct = safeLine();

            System.out.print("Company (EC/CQ/FI): ");
            String company = safeLine();

            Double amt = readDouble("Amount to pay: ");
            if (amt == null) { System.out.println("Bad amount."); return; }

            service.paybill(session, nameForAdmin, acct, company, amt);
            System.out.println("Paybill recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles deposit transactions*/
    private void depositFlow() {
        try {
            String nameForAdmin = "";
            if (session.isAdmin()) {
                System.out.print("Account holder name: ");
                nameForAdmin = safeLine();
            }

            System.out.print("Account number: ");
            String acct = safeLine();

            Double amt = readDouble("Amount to deposit: ");
            if (amt == null) { System.out.println("Bad amount."); return; }

            service.deposit(session, nameForAdmin, acct, amt);
            System.out.println("Deposit recorded (not available until logout).");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles account creation (admit only)*/
    private void createFlow() {
        if (!session.isAdmin()) { System.out.println("Admin only."); return; }

        try {
            System.out.print("Account holder name: ");
            String name = safeLine();
            Double amt = readDouble("Initial balance: ");
            if (amt == null) { System.out.println("Bad amount."); return; }

            service.create(session, name, amt);
            System.out.println("Create recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles account deletion (admit only)*/
    private void deleteFlow() {
        if (!session.isAdmin()) { System.out.println("Admin only."); return; }

        try {
            System.out.print("Account holder name: ");
            String name = safeLine();
            System.out.print("Account number: ");
            String acct = safeLine();

            service.delete(session, name, acct);
            System.out.println("Delete recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles account disabling (admit only)*/
    private void disableFlow() {
        if (!session.isAdmin()) { System.out.println("Admin only."); return; }

        try {
            System.out.print("Account holder name: ");
            String name = safeLine();
            System.out.print("Account number: ");
            String acct = safeLine();

            service.disable(session, name, acct);
            System.out.println("Disable recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /*Handles account plan changes (admit only)*/
    private void changeplanFlow() {
        if (!session.isAdmin()) { System.out.println("Admin only."); return; }

        try {
            System.out.print("Account holder name: ");
            String name = safeLine();
            System.out.print("Account number: ");
            String acct = safeLine();

            service.changeplan(session, name, acct);
            System.out.println("Changeplan recorded.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

     /**
     * Safely reads a trimmed line of input from the user
     * @return trimmed input string (never null)
     */
    private String safeLine() {
        String s = input.nextLine();
        return s == null ? "" : s.trim();
    }

    /**
     * Prompts the user for a numeric value and parses it as a Double
     * @param prompt Prompt message displayed to the user
     * @return Parsed Double value, or null if input is invalid
     */
    private Double readDouble(String prompt) {
        System.out.print(prompt);
        String s = safeLine();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
