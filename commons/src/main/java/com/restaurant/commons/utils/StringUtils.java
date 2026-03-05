package com.restaurant.commons.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class  StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static boolean isStringNotEmpty(String str) {
        return str != null && !str.isBlank();
    }

    public static String toSlug(String input){
        if (StringUtils.isBlank(input)) {
            return "";
        }
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(java.util.Locale.ENGLISH);
    }

    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email)) return "";
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;

        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedName = name.charAt(0) + "*******";
        if (name.length() > 2) {
            maskedName = name.charAt(0) + "*******" + name.charAt(name.length() - 1);
        }
        return maskedName + domain;
    }

    public static String maskString(String str, int visibleTailChars) {
        if (StringUtils.isBlank(str) || str.length() <= visibleTailChars) return str;
        String tail = str.substring(str.length() - visibleTailChars);
        return "*".repeat(str.length() - visibleTailChars) + tail;
    }

    public static String normalizeName(String name) {
        if (StringUtils.isBlank(name)) return "";
        name = name.trim().replaceAll("\\s+", " "); // Xóa khoảng trắng thừa
        char[] chars = name.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static boolean isJson(String json) {
        if (StringUtils.isBlank(json)) return false;
        String trimmed = json.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
               (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    public static boolean hasText(String text){
        return text != null && !text.trim().isEmpty();
    }
}
