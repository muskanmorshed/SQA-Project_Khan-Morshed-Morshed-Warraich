public class Main {
    public static void main(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: java Main <accountsFile> <rentalUnitsFile> <dailyTransactionsFile> ");
            return;
        }

        String accountsFile = args[0];
        String rentalUnitsFile = args[1]; // accepted but may be unused for banking
        String txFile = args[2];
        
        AccountsRepository repo = new FileAccountsRepository(accountsFile);
        repo.load();

        TransactionLogger logger = new TransactionLogger(txFile);
        BankService service = new BankService(repo, logger);
        Session session = new Session();

        BankFrontEnd frontEnd = new BankFrontEnd(service, session);
        frontEnd.run();

        repo.save();
        // rentalUnitsFile intentionally not used (kept to satisfy interface requirement)
       

    }
}

