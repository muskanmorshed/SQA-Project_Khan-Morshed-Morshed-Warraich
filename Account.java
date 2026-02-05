
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

    public String getId() { return id; }
    public String getName() { return name; }
    public char getStatus() { return status; }
    public String getPlan() { return plan; }
    public double getBalance() { return balance; }

    public boolean isDisabled() { return status == 'D'; }

    public void setStatus(char status) { this.status = status; }
    public void setPlan(String plan) { this.plan = plan; }

    public void credit(double amount) {
        balance += amount;
    }

    public void debit(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
    }
}
