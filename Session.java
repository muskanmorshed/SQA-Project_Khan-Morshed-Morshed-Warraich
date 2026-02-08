/**
 * Session.java
 * Represents the current login session of the banking system
 * Maintains the current session state, including login status, 
   user role, account holder identity, and per-session transaction limits.
 */
public class Session {
    private boolean loggedIn = false;
    private boolean admin = false;
    private String holderName = null;

    private double totalWithdraw = 0.0;
    private double totalTransfer = 0.0;
    private double totalPaybill = 0.0;

    // starts a standard user session for the specified account holder
    public void loginStandard(String holderName) {
        this.loggedIn = true;
        this.admin = false;
        this.holderName = holderName;
        resetTotals();
    }

    // starts an administrative session with elevated priveleges
    public void loginAdmin() {
        this.loggedIn = true;
        this.admin = true;
        this.holderName = null;
        resetTotals();
    }

    // ends the current session and resets session state
    public void logout() {
        this.loggedIn = false;
        this.admin = false;
        this.holderName = null;
        resetTotals();
    }

    // indicates whether a user is currently logged in
    public boolean isLoggedIn() { return loggedIn; }
    // indicates whether the current session has administrative priveleges
    public boolean isAdmin() { return admin; }
    // returns the account holder name associated with the session.
    public String getHolderName() { return holderName; }

    // transaction tracking:
    // tracks the total withdrawal amount for the current session
    public void addWithdraw(double amt) { totalWithdraw += amt; }
    // tracks the total transfer amount for the current session
    public void addTransfer(double amt) { totalTransfer += amt; }
    // tracks the total bill payment for the current session.
    public void addPaybill(double amt) { totalPaybill += amt; }

    // returns the total amount of transaction for the current session
    public double getTotalWithdraw() { return totalWithdraw; }
    public double getTotalTransfer() { return totalTransfer; }
    public double getTotalPaybill() { return totalPaybill; }

    // resets the transactions, ensuring that transaction limits apply per session, not multiple logins
    private void resetTotals() {
        totalWithdraw = 0.0;
        totalTransfer = 0.0;
        totalPaybill = 0.0;
    }
}

