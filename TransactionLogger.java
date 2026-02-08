import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionLogger.java
 * Collects and writes all transaction records generated during a session
   to the daily transactions output file in fixed-width format.
 */

public class TransactionLogger {
    private final String dailyTransactionsFilePath; // path to daily transactions output file
    private final List<TransactionRecord> records = new ArrayList<>(); // in-memory list of transaction records for current session

    // constructs a transaction logger for the given output file
    public TransactionLogger(String dailyTransactionsFilePath) {
        this.dailyTransactionsFilePath = dailyTransactionsFilePath;
    }

    // Records a transaction during a session.
    public void add(TransactionRecord record) {
        records.add(record);
    }

    // Writes all recorded transactions to file, appends end of session record, then clears log.
    public void writeAndClear() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dailyTransactionsFilePath, false))) {
            // Write all transaction records in fixed-width (40-character) format
            for (TransactionRecord r : records) {
                pw.println(r.toFixed40());
            }
            // End of session record (00)
            pw.println(new TransactionRecord("00", "", "00000", 0.0, "").toFixed40());
        } catch (IOException ignored) { // Fail silently if the transactions file cannot be written
        } finally {
            // Clear records regardless of write success
            records.clear();
        }
    }
}

