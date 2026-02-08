import java.io.*;
import java.util.*;

/**
 * FileAccountsRepository.java
 - Implements account persistence using a fixed-width text file format.
 - File-based implementation of the AccountsRepository interface.
 - This class loads account records from a fixed-format accounts file
   into memory and writes all account data back to the file on save.
 - Accounts are stored internally in a map keyed by their 5-digit account ID.
 */

public class FileAccountsRepository implements AccountsRepository {
    private final String accountsFilePath;
    private final Map<String, Account> accounts = new HashMap<>();

    public FileAccountsRepository(String filename) {
        this.accountsFilePath = filename;
    }

    // loads account data from persistent storage
    @Override
    public void load() {
        accounts.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 37) continue; // tolerate bad lines

                // Format (37 chars):
                // NNNNN_ AAAAAAAAAAAAAAAAAAAA _S_ PPPPPPPP
                String id = FixedFmt.acct5(line.substring(0, 5));
                String name = line.substring(6, 26).trim();
                char status = line.charAt(27);
                String balStr = line.substring(29, 37);

                if ("END_OF_FILE".equals(name)) break;

                if (status != 'A' && status != 'D') status = 'A';

                double balance = parseMoney(balStr);

                // Plan isn't in current accounts file (per spec) — default to SP
                Account acc = new Account(id, name, status, balance, "SP");
                accounts.put(id, acc);
            }
        } catch (IOException ignored) {
            // start empty if file missing/unreadable
        }
    }

    // writes account data to persistent storage
    @Override
    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(accountsFilePath))) {
            // write sorted by account id
            List<String> ids = new ArrayList<>(accounts.keySet());
            Collections.sort(ids);

            for (String id : ids) {
                Account acc = accounts.get(id);
                if (acc == null) continue;

                String line =
                        FixedFmt.acct5(acc.getId()) + " " +
                        FixedFmt.alpha20(acc.getName()) + " " +
                        acc.getStatus() + " " +
                        FixedFmt.money8(acc.getBalance());

                // must be exactly 37 chars (plus newline)
                writer.println(FixedFmt.padRight(line, 37));
            }

            // END_OF_FILE record
            String eofLine =
                    "00000 " +
                    FixedFmt.alpha20("END_OF_FILE") + " " +
                    "A " +
                    "00000.00";
            writer.println(FixedFmt.padRight(eofLine, 37));

        } catch (IOException ignored) {
        }
    }

    // determines whether an account exists
    @Override
    public boolean exists(String accountId) {
        return accounts.containsKey(FixedFmt.acct5(accountId));
    }

    // retrieves an account by identifier
    @Override
    public Account get(String accountId) {
        String id = FixedFmt.acct5(accountId);
        Account a = accounts.get(id);
        if (a == null) throw new IllegalArgumentException("Account does not exist.");
        return a;
    }

    // adds a new account to storage
    @Override
    public void add(Account account) {
        String id = FixedFmt.acct5(account.getId());
        accounts.put(id, account);
    }

    // removes an account from storage
    @Override
    public void remove(String accountId) {
        accounts.remove(FixedFmt.acct5(accountId));
    }

    // generates the next available account identifier
    @Override
    public String nextAccountId() {
        int max = 0;
        for (String id : accounts.keySet()) {
            try {
                max = Math.max(max, Integer.parseInt(id));
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("%05d", max + 1);
    }

    // parses a money-formatted string into a double value, returning 0.0 if the input is invalid.
    private static double parseMoney(String s) {
        // money field is like 00110.00 (8 chars)
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
