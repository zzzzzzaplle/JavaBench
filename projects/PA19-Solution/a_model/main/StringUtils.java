package util;

import org.jetbrains.annotations.NotNull;

/**
 * A collection of utilities for Strings.
 */
public class StringUtils {

    private StringUtils() {
        // You don't need to instantiate this class to use it!
    }

    /**
     * Creates a String using a character repeated a number of times.
     *
     * @param count Number of characters to pad.
     * @param ch Character to pad with.
     * @return A string with {@code count} number of {@code ch}.
     */
    @NotNull public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
