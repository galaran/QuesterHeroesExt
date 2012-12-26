package me.galaran.bukkitutils.questerhex.text;

import com.google.common.base.Function;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final Pattern AMP_COLOR_PATTERN = Pattern.compile("&([0-9A-FK-OR])", Pattern.CASE_INSENSITIVE);

    /**
     * @param toStringer tostring function or null to use Object.toString()
     */
    public static <T> String join(Iterable<T> collection, String delimiter, Function<T, String> toStringer) {
        return join(collection, null, delimiter, null, toStringer);
    }

    /**
     * @param toStringer tostring function or null to use Object.toString()
     */
    public static <T> String join(Iterable<T> collection, ChatColor elementColor, String delimiter, ChatColor delimiterColor, Function<T, String> toStringer) {
        StringBuilder sb = new StringBuilder();
        Iterator<T> itr = collection.iterator();
        while (itr.hasNext()) {
            T obj = itr.next();
            String objString = (toStringer == null) ? obj.toString() : toStringer.apply(obj);

            if (elementColor != null) {
                sb.append(elementColor);
            }
            sb.append(objString);
            if (itr.hasNext()) {
                if (delimiterColor != null) {
                    sb.append(delimiterColor);
                }
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String colorizeAmps(String string) {
        Matcher m = AMP_COLOR_PATTERN.matcher(string);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            m.appendReplacement(sb, ChatColor.COLOR_CHAR + m.group(1));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public static String parameterizeString(String pattern, Object... params) {
        String result = pattern;
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                result = result.replace("$" + (i + 1), params[i].toString());
            }
        }
        return result;
    }

    public static String formatDouble(double val) {
        return formatDouble(val, 1);
    }

    public static String formatDouble(double val, int decimalPlaces) {
        return String.format(Locale.US, "%." + decimalPlaces + "f", val);
    }

    private static final int KEY = 0;
    private static final int VALUE_START = 1;
    private static final int VALUE_CONTINUE = 2;

    /** -data 3 -a "he he he" -t "title 123" */
    public static Map<String, String> parseParameters(String[] args) throws IllegalArgumentException {
        Map<String, String> result = new LinkedHashMap<String, String>();

        int expectedState = KEY;
        String currentKey = null;
        StringBuilder currentValue = null;
        for (String arg : args) {
            switch (expectedState) {
            case KEY:
                if (arg.charAt(0) == '-') {
                    currentKey = arg.substring(1);
                } else {
                    throw new IllegalArgumentException("Parameter key expected");
                }
                expectedState = VALUE_START;
                break;
            case VALUE_START:
                if (arg.charAt(0) == '"') {
                    if (arg.charAt(arg.length() - 1) == '"') {
                        result.put(currentKey, arg.substring(1, arg.length() - 1));
                        expectedState = KEY;
                    } else {
                        currentValue = new StringBuilder();
                        currentValue.append(arg.substring(1));
                        expectedState = VALUE_CONTINUE;
                    }
                } else {
                    result.put(currentKey, arg);
                    expectedState = KEY;
                }
                break;
            case VALUE_CONTINUE:
                currentValue.append(' ');
                if (arg.charAt(arg.length() - 1) == '"') {
                    currentValue.append(arg.substring(0, arg.length() - 1));
                    result.put(currentKey, currentValue.toString());
                    expectedState = KEY;
                } else {
                    currentValue.append(arg);
                    expectedState = VALUE_CONTINUE;
                }
                break;
            }
        }

        if (expectedState != KEY) throw new IllegalArgumentException("Illegal end");
        return result;
    }

    /** -data 3 -a "he he he" -t "title 123" */
    public static Map<String, String> parseParameters(String string) throws IllegalArgumentException {
        // special case: empty string is empty array
        String[] args = string.trim().isEmpty() ? new String[0] : string.trim().split("\\s+");

        return parseParameters(args);
    }

    public static boolean stringContainsIgnoreCaseAndColor(String line, String matchingString) {
        String lineRaw = ChatColor.stripColor(colorizeAmps(line)).trim().toLowerCase();
        String matchingStringRaw = ChatColor.stripColor(matchingString).trim().toLowerCase();
        return lineRaw.contains(matchingStringRaw);
    }

    /** Parameterize + colorize */
    public static String decorateString(String string, Object... params) {
        return colorizeAmps(parameterizeString(string, params));
    }

    public static String surroundString(String before, String text, String after, ChatColor surrColor, ChatColor textColor) {
        StringBuilder sb = new StringBuilder();
        sb.append(surrColor);
        sb.append(before);
        sb.append(textColor);
        sb.append(text);
        sb.append(surrColor);
        sb.append(after);
        return sb.toString();
    }
}
