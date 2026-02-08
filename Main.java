/**
 * Main.java
 * Program entry point for the banking system.
 * Coordinates system startup by loading input files, 
   constructing core components, and launching the front-end interface.
 * This class is responsible for:
 *  - Validating command-line arguments
 *  - Initializing repositories and services
 *  - Starting the user session and front-end
 *  - Saving account data on program termination
 */
public class Main {
    // Validates input file arguments, 
    // initializes system components, 
    // and starts the banking front-end
    public static void main(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: java Main <accountsFile> <rentalUnitsFile> <dailyTransactionsFile> ");
            return;
        }

        // Extract input file paths
        String accountsFile = args[0];
        String rentalUnitsFile = args[1]; // accepted but may be unused for banking
        String txFile = args[2];
        
        // Load account data from persistent storage
        AccountsRepository repo = new FileAccountsRepository(accountsFile);
        repo.load();

        // Initialize transaction logging
        TransactionLogger logger = new TransactionLogger(txFile);
        // Create core banking service and session state
        BankService service = new BankService(repo, logger);
        Session session = new Session();

        // Start the user-facing front-end
        BankFrontEnd frontEnd = new BankFrontEnd(service, session);
        frontEnd.run();

        // Persist account data before exiting
        repo.save();
        
        // rentalUnitsFile intentionally not used (included to satisfy interface requirement)
    }
}

