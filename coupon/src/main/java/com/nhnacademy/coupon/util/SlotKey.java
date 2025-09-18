package com.nhnacademy.coupon.util;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class SlotKey {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
    private static long ten() { return 10 * 60 * 1000L; }
    public static long slotStartMillis(long utcMillis) { return (utcMillis / ten()) * ten(); }
    public static String format(long utcMillis) {
        return F.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcMillis), ZoneId.of("UTC")));
    }
    public static String currentSlot(long utcMillis) { return format(slotStartMillis(utcMillis)); }
    public static String previousSlot(long utcMillis) { return format(slotStartMillis(utcMillis) - ten()); }
}