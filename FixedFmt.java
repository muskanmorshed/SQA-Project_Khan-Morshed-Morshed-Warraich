/**
 * FixedFmt.java
 - Utility class providing helper methods for formatting values
   into fixed-width fields required by the banking system file
   specifications.
 - These methods ensure consistent formatting for account IDs,
   names, monetary values, and miscellaneous fields when reading
   from or writing to fixed-format files.
 */
public class FixedFmt {
    /**
     * Formats a string as a left-aligned, space-padded 20-character field
     * @param name Input name string
     * @return Name trimmed or truncated to 20 characters and padded on the right
     */
    public static String alpha20(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.length() > 20) trimmed = trimmed.substring(0, 20);
        return padRight(trimmed, 20);
    }

    /**
     * Formats an account identifier as a 5-digit, zero-padded string
     * @param acct Raw account identifier
     * @return Right-justified, zero-filled 5-digit account ID
     */
    public static String acct5(String acct) {
        // right-justified, zero-filled
        int n = 0;
        try { n = Integer.parseInt(acct.trim()); } catch (Exception ignored) {}
        return String.format("%05d", n);
    }

    /**
     * Formats a monetary value as an 8-character, zero-padded field, to 2 dp.
     * @param amount Monetary amount
     * @return Fixed-width monetary string (8 characters)
     */
    public static String money8(double amount) {
        // 8 chars: PPPPPPPP where value has ".00" and is zero-filled on left
        // Example: 110 -> "00110.00"
        if (amount < 0) amount = 0; // keep tolerant for prototype
        String s = String.format("%.2f", amount); // "110.00"
        // Need total width 8; left pad with zeros
        if (s.length() > 8) {
            // clip for prototype; proper system would reject
            return s.substring(0, 8);
        }
        return padLeftZeros(s, 8);
    }

     /**
     * Formats a miscellaneous 2-character field
     * @param mm Input miscellaneous value
     * @return Left-aligned, space-padded 2-character string
     */
    public static String misc2(String mm) {
        String t = (mm == null) ? "" : mm.trim();
        if (t.length() > 2) t = t.substring(0, 2);
        return padRight(t, 2);
    }

    /**
     * Pads a string on the right with spaces to a fixed width
     * @param s     Input string
     * @param width Desired field width
     * @return Right-padded string of exact width
     */
    public static String padRight(String s, int width) {
        StringBuilder b = new StringBuilder(s == null ? "" : s);
        while (b.length() < width) b.append(' ');
        if (b.length() > width) return b.substring(0, width);
        return b.toString();
    }

    /**
     * Pads a string on the left with zeros to a fixed width
     * @param s     Input string
     * @param width Desired field width
     * @return Zero-padded string of exact width
     */
    public static String padLeftZeros(String s, int width) {
        String t = s == null ? "" : s;
        StringBuilder b = new StringBuilder();
        while (b.length() + t.length() < width) b.append('0');
        b.append(t);
        if (b.length() > width) return b.substring(b.length() - width);
        return b.toString();
    }
}

