/*
Account.java
- Represents a single bank account in the system.
- An account stores ID, name, account status, subscription plan type, and the current monetary balance
- this class will provide basic operations such as crediting and debiting funds,
  as well as getters and setters for account state management.
*/ 
public class Account {
    private final String id;        // 5-digit string e.g., "00023"
    private final String name;      // raw name (trimmed)
    private char status;            // 'A' active, 'D' disabled
    private String plan;            // "SP" or "NP"
    private double balance;

    public Account(String id, String name, char status, double balance, String plan) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.balance = balance;
        this.plan = plan == null ? "SP" : plan;
    }

    /**
     * Constructs a new Account object.
     *
     * @param id      Unique 5-digit account identifier
     * @param name    Name of the account holder
     * @param status  Account status ('A' for active, 'D' for disabled)
     * @param balance Initial account balance
     * @param plan    Account plan ("SP" or "NP"); defaults to "SP" if null
     */
    
    /**
     * @return the account's unique identifier
     */
    public String getId() { return id; }

    /**
     * @return the name of the account holder
     */
    public String getName() { return name; }

    /**
     * @return the current account status
     */
    public char getStatus() { return status; }

    /**
     * @return the account plan type ("SP" or "NP")
     */
    public String getPlan() { return plan; }

    /**
     * @return the current account balance
     */
    public double getBalance() { return balance; }

    /**
     * Checks whether the account is disabled
     * @return true if the account status is 'D', false otherwise
     */
    public boolean isDisabled() { return status == 'D'; }

    /**
     * Updates the account status
     * @param status New status ('A' or 'D')
     */
    public void setStatus(char status) { this.status = status; }

    /**
     * Updates the account plan
     * @param plan New plan type ("SP" or "NP")
     */
    public void setPlan(String plan) { this.plan = plan; }

    /**
     * Adds a specified amount to the account balance
     * @param amount Amount to be added to the balance
     */
    public void credit(double amount) {
        balance += amount;
    }

    /**
     * Subtracts a specified amount from the account balance
     * @param amount Amount to be withdrawn
     * @throws IllegalArgumentException if the amount is negative
     *                                  or exceeds the available balance
     */
    public void debit(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
    }
}
