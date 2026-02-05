public class TransactionRecord {
    private final String code2;   // "01".."08" or "00"
    private final String name;    // account holder name (20)
    private final String acct5;   // account number (5)
    private final double amount;  // money
    private final String misc2;   // "EC","CQ","FI" or "NP" etc, or blanks

    public TransactionRecord(String code2, String name, String acct5, double amount, String misc2) {
        this.code2 = code2;
        this.name = name;
        this.acct5 = acct5;
        this.amount = amount;
        this.misc2 = misc2;
    }

    public String toFixed40() {
        // 2 + 1 + 20 + 1 + 5 + 1 + 8 + 2 = 40
        String line =
                code2 + " " +
                FixedFmt.alpha20(name) + " " +
                FixedFmt.acct5(acct5) + " " +
                FixedFmt.money8(amount) +
                FixedFmt.misc2(misc2);

        return FixedFmt.padRight(line, 40);
    }
}

