import java.io.*;
import java.util.*;



public class FileAccountsRepository implements AccountsRepository {
    private final String accountsFilePath;
    private final Map<String, Account> accounts = new HashMap<>();

    public FileAccountsRepository(String filename) {
        this.accountsFilePath = filename;
    }

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

    @Override
    public boolean exists(String accountId) {
        return accounts.containsKey(FixedFmt.acct5(accountId));
    }

    @Override
    public Account get(String accountId) {
        String id = FixedFmt.acct5(accountId);
        Account a = accounts.get(id);
        if (a == null) throw new IllegalArgumentException("Account does not exist.");
        return a;
    }

    @Override
    public void add(Account account) {
        String id = FixedFmt.acct5(account.getId());
        accounts.put(id, account);
    }

    @Override
    public void remove(String accountId) {
        accounts.remove(FixedFmt.acct5(accountId));
    }

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

    private static double parseMoney(String s) {
        // money field is like 00110.00 (8 chars)
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

  

}
