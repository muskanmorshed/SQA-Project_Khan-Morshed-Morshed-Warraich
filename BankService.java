import java.util.HashMap;
import java.util.Map;



public class BankService {
    private final AccountsRepository repo;
    private final TransactionLogger logger;

    // deposits are recorded but not usable until logout
    private final Map<String, Double> pendingDeposits = new HashMap<>();

    public BankService(AccountsRepository repository, TransactionLogger logger) {
        this.repo = repository;
        this.logger = logger;
    }

    // Validate for STANDARD mode: holder name must match account owner and status active
    public void validateStandardAccount(String holderName, String accountId) {
        Account acc = repo.get(FixedFmt.acct5(accountId));
        if (acc.isDisabled()) throw new IllegalArgumentException("Account is disabled.");
        if (!acc.getName().equalsIgnoreCase(holderName.trim())) {
            throw new IllegalArgumentException("Account does not belong to current user.");
        }
    }

    // ADMIN mode can target any existing non-disabled account unless the transaction is delete/disable
    public void validateExistingActive(String accountId) {
        Account acc = repo.get(FixedFmt.acct5(accountId));
        if (acc.isDisabled()) throw new IllegalArgumentException("Account is disabled.");
    }

    public void withdrawal(Session session, String holderNameIfAdmin, String accountId, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");

        String acct5 = FixedFmt.acct5(accountId);

        if (session.isAdmin()) {
            validateExistingActive(acct5);
        } else {
            validateStandardAccount(session.getHolderName(), acct5);
            if (session.getTotalWithdraw() + amount > 500.0) {
                throw new IllegalArgumentException("Standard session withdrawal limit is $500.00.");
            }
        }

        Account acc = repo.get(acct5);
        acc.debit(amount);

        if (!session.isAdmin()) session.addWithdraw(amount);

        // Log: 01 withdrawal
        String nameForLog = session.isAdmin() ? holderNameIfAdmin : session.getHolderName();
        logger.add(new TransactionRecord("01", nameForLog, acct5, amount, ""));


    }

    public void transfer(Session session, String holderNameIfAdmin, String fromId, String toId, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");

        String from5 = FixedFmt.acct5(fromId);
        String to5 = FixedFmt.acct5(toId);

        if (!repo.exists(to5)) throw new IllegalArgumentException("Destination account does not exist.");

        if (session.isAdmin()) {
            validateExistingActive(from5);
            validateExistingActive(to5);
        } else {
            validateStandardAccount(session.getHolderName(), from5);
            validateExistingActive(to5);
            if (session.getTotalTransfer() + amount > 1000.0) {
                throw new IllegalArgumentException("Standard session transfer limit is $1000.00.");
            }
        }

        Account from = repo.get(from5);
        Account to = repo.get(to5);

        from.debit(amount);
        to.credit(amount);

        if (!session.isAdmin()) session.addTransfer(amount);

        // Log: 02 transfer (MM unused in format, so blank)
        String nameForLog = session.isAdmin() ? holderNameIfAdmin : session.getHolderName();
        logger.add(new TransactionRecord("02", nameForLog, from5, amount, ""));
    }

    public void paybill(Session session, String holderNameIfAdmin, String accountId, String companyCode, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");

        String acct5 = FixedFmt.acct5(accountId);
        String cc = companyCode == null ? "" : companyCode.trim().toUpperCase();

        if (!(cc.equals("EC") || cc.equals("CQ") || cc.equals("FI"))) {
            throw new IllegalArgumentException("Invalid company code. Use EC, CQ, or FI.");
        }

        if (session.isAdmin()) {
            validateExistingActive(acct5);
        } else {
            validateStandardAccount(session.getHolderName(), acct5);
            if (session.getTotalPaybill() + amount > 2000.0) {
                throw new IllegalArgumentException("Standard session paybill limit is $2000.00.");
            }
        }

        Account acc = repo.get(acct5);
        acc.debit(amount);

        if (!session.isAdmin()) session.addPaybill(amount);

        // Log: 03 paybill, MM holds company code
        String nameForLog = session.isAdmin() ? holderNameIfAdmin : session.getHolderName();
        logger.add(new TransactionRecord("03", nameForLog, acct5, amount, cc));
    }

    public void deposit(Session session, String holderNameIfAdmin, String accountId, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");

        String acct5 = FixedFmt.acct5(accountId);

        if (session.isAdmin()) {
            validateExistingActive(acct5);
        } else {
            validateStandardAccount(session.getHolderName(), acct5);
        }

        // Not available until logout -> pending
        pendingDeposits.put(acct5, pendingDeposits.getOrDefault(acct5, 0.0) + amount);

        // Log: 04 deposit
        String nameForLog = session.isAdmin() ? holderNameIfAdmin : session.getHolderName();
        logger.add(new TransactionRecord("04", nameForLog, acct5, amount, ""));
    }

    // Privileged: create (05)
    public void create(Session session, String holderName, double initialBalance) {
        if (!session.isAdmin()) throw new IllegalArgumentException("Admin only.");
        if (holderName == null) holderName = "";
        holderName = holderName.trim();
        if (holderName.length() > 20) throw new IllegalArgumentException("Name max 20 characters.");
        if (initialBalance < 0 || initialBalance > 99999.99) throw new IllegalArgumentException("Invalid initial balance.");

        String id = repo.nextAccountId();
        Account acc = new Account(id, holderName, 'A', initialBalance, "SP");
        repo.add(acc);

        logger.add(new TransactionRecord("05", holderName, id, initialBalance, ""));
    }

    // Privileged: delete (06)
    public void delete(Session session, String holderName, String accountId) {
        if (!session.isAdmin()) throw new IllegalArgumentException("Admin only.");
        String acct5 = FixedFmt.acct5(accountId);
        Account acc = repo.get(acct5);
        if (!acc.getName().equalsIgnoreCase(holderName.trim())) {
            throw new IllegalArgumentException("Holder name does not match account.");
        }
        repo.remove(acct5);
        logger.add(new TransactionRecord("06", holderName, acct5, 0.0, ""));
    }

    // Privileged: disable (07)
    public void disable(Session session, String holderName, String accountId) {
        if (!session.isAdmin()) throw new IllegalArgumentException("Admin only.");
        String acct5 = FixedFmt.acct5(accountId);
        Account acc = repo.get(acct5);
        if (!acc.getName().equalsIgnoreCase(holderName.trim())) {
            throw new IllegalArgumentException("Holder name does not match account.");
        }
        acc.setStatus('D');
        logger.add(new TransactionRecord("07", holderName, acct5, 0.0, ""));
    }

    // Privileged: changeplan (08) -> set SP to NP
    public void changeplan(Session session, String holderName, String accountId) {
        if (!session.isAdmin()) throw new IllegalArgumentException("Admin only.");
        String acct5 = FixedFmt.acct5(accountId);
        Account acc = repo.get(acct5);
        if (!acc.getName().equalsIgnoreCase(holderName.trim())) {
            throw new IllegalArgumentException("Holder name does not match account.");
        }
        acc.setPlan("NP");
        logger.add(new TransactionRecord("08", holderName, acct5, 0.0, "NP"));
    }

    // Called at logout: apply pending deposits to balances
    public void applyPendingDeposits() {
        for (Map.Entry<String, Double> e : pendingDeposits.entrySet()) {
            if (repo.exists(e.getKey())) {
                repo.get(e.getKey()).credit(e.getValue());
            }
        }
        pendingDeposits.clear();
    }

    public void writeTransactionsAtLogout() {
        logger.writeAndClear();
    }
}
