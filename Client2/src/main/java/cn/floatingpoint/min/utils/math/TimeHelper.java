package cn.floatingpoint.min.utils.math;

import cn.floatingpoint.min.utils.client.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeHelper {
    private long lastMS;

    public boolean isDelayComplete(double delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public static Pair<String, Long> getDurationAndReasonFromString(String s) {
        Pattern pattern = Pattern.compile("\\b(?:\\d+y|\\d+mo|\\d+w|\\d+d|\\d+h|\\d+m(?:in)?|\\d+s)\\b\\s*");
        Matcher matcher = pattern.matcher(s.toLowerCase());
        String reason = "";
        long duration = 0;
        int end = -1;
        while (matcher.find()) {
            String timeWord = matcher.group().trim();
            if (timeWord.endsWith("y")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier * 31536000L;
            } else if (timeWord.endsWith("mo")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 2));
                duration += amplifier * 2592000L;
            } else if (timeWord.endsWith("w")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier * 604800L;
            } else if (timeWord.endsWith("d")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier * 86400L;
            } else if (timeWord.endsWith("h")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier * 3600L;
            } else if (timeWord.endsWith("m")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier * 60L;
            } else if (timeWord.endsWith("min")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 3));
                duration += amplifier * 60L;
            } else if (timeWord.endsWith("s")) {
                int amplifier = Integer.parseInt(timeWord.substring(0, timeWord.length() - 1));
                duration += amplifier;
            }
            end = matcher.end();
        }
        if (matcher.hitEnd()) {
            if (end == -1) {
                reason = s;
            } else {
                reason = s.substring(end);
            }
        }
        return new Pair<>(reason.trim(), duration); // Permanent
    }
}
