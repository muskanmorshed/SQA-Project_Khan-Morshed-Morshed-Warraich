public class Session {
    private boolean loggedIn = false;
    private boolean admin = false;
    private String holderName = null;

    private double totalWithdraw = 0.0;
    private double totalTransfer = 0.0;
    private double totalPaybill = 0.0;

    public void loginStandard(String holderName) {
        this.loggedIn = true;
        this.admin = false;
        this.holderName = holderName;
        resetTotals();
    }

    public void loginAdmin() {
        this.loggedIn = true;
        this.admin = true;
        this.holderName = null;
        resetTotals();
    }

    public void logout() {
        this.loggedIn = false;
        this.admin = false;
        this.holderName = null;
        resetTotals();
    }

    public boolean isLoggedIn() { return loggedIn; }
    public boolean isAdmin() { return admin; }
    public String getHolderName() { return holderName; }

    public void addWithdraw(double amt) { totalWithdraw += amt; }
    public void addTransfer(double amt) { totalTransfer += amt; }
    public void addPaybill(double amt) { totalPaybill += amt; }

    public double getTotalWithdraw() { return totalWithdraw; }
    public double getTotalTransfer() { return totalTransfer; }
    public double getTotalPaybill() { return totalPaybill; }

    private void resetTotals() {
        totalWithdraw = 0.0;
        totalTransfer = 0.0;
        totalPaybill = 0.0;
    }
}

