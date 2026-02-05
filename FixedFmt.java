public class FixedFmt {
    public static String alpha20(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.length() > 20) trimmed = trimmed.substring(0, 20);
        return padRight(trimmed, 20);
    }

    public static String acct5(String acct) {
        // right-justified, zero-filled
        int n = 0;
        try { n = Integer.parseInt(acct.trim()); } catch (Exception ignored) {}
        return String.format("%05d", n);
    }

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

    public static String misc2(String mm) {
        String t = (mm == null) ? "" : mm.trim();
        if (t.length() > 2) t = t.substring(0, 2);
        return padRight(t, 2);
    }

    public static String padRight(String s, int width) {
        StringBuilder b = new StringBuilder(s == null ? "" : s);
        while (b.length() < width) b.append(' ');
        if (b.length() > width) return b.substring(0, width);
        return b.toString();
    }

    public static String padLeftZeros(String s, int width) {
        String t = s == null ? "" : s;
        StringBuilder b = new StringBuilder();
        while (b.length() + t.length() < width) b.append('0');
        b.append(t);
        if (b.length() > width) return b.substring(b.length() - width);
        return b.toString();
    }
}

