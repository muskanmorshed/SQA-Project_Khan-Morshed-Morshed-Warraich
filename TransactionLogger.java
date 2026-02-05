import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TransactionLogger {
    private final String dailyTransactionsFilePath;
    private final List<TransactionRecord> records = new ArrayList<>();

    public TransactionLogger(String dailyTransactionsFilePath) {
        this.dailyTransactionsFilePath = dailyTransactionsFilePath;
    }

    public void add(TransactionRecord record) {
        records.add(record);
    }

    public void writeAndClear() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dailyTransactionsFilePath, false))) {
            for (TransactionRecord r : records) {
                pw.println(r.toFixed40());
            }
            // End of session record (00)
            pw.println(new TransactionRecord("00", "", "00000", 0.0, "").toFixed40());
        } catch (IOException ignored) {
        } finally {
            records.clear();
        }
    }
}

