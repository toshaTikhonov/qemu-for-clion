package pl.merskip.qemu;

import java.util.ArrayList;
import java.util.List;

/** Parse shell-like arguments without launching a shell. */
public final class QemuArguments {
    private QemuArguments() {}

    public static List<String> parse(String arguments) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char quote = 0;
        boolean escape = false;
        boolean tokenStarted = false;
        for (char c : arguments.toCharArray()) {
            if (escape) {
                current.append(c);
                escape = false;
                tokenStarted = true;
            } else if (c == 92 && quote != 39) {
                escape = true;
                tokenStarted = true;
            } else if (quote != 0) {
                if (c == quote) quote = 0;
                else current.append(c);
            } else if (c == 39 || c == 34) {
                quote = c;
                tokenStarted = true;
            } else if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    result.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }
            } else {
                current.append(c);
                tokenStarted = true;
            }
        }
        if (escape) throw new IllegalArgumentException("Trailing escape in QEMU arguments");
        if (quote != 0) throw new IllegalArgumentException("Unclosed quote in QEMU arguments");
        if (tokenStarted) result.add(current.toString());
        return result;
    }
}
