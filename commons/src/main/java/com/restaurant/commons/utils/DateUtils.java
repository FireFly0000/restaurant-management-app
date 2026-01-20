package com.restaurant.commons.utils;

import com.restaurant.commons.constant.Constant;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);


    public static final String DISPLAY_DATE = "dd-MM-yyyy";
    public static final String DISPLAY_DATETIME = "dd-MM-yyyy HH:mm:ss";
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE);
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATETIME);


    public static final ZoneId ZONE_VN = ZoneId.of(Constant.DEFAULT_ZONE);

    public static String nowStr() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    public static LocalDateTime parse(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
        }
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMATTER);
    }

    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) return null;
        try{
            return dateTime.format(formatter);
        }catch (Exception e){
            return dateTime.format(DATETIME_FORMATTER);
        }
    }

    public static LocalDateTime atStartOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime atEndOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static LocalDateTime fromEpoch(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZONE_VN).toLocalDateTime();
    }

    public static long toEpoch(LocalDateTime dateTime) {
        return dateTime.atZone(ZONE_VN).toInstant().toEpochMilli();
    }

    public static boolean isOver18(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    public static String convertIsoToDisplayDate(String isoDateStr) {
        if (isoDateStr == null || isoDateStr.isEmpty()) return "";
        try {
            LocalDate date = LocalDate.parse(isoDateStr, DATE_FORMATTER);
            return date.format(DISPLAY_DATE_FORMATTER);
        } catch (Exception e) {

            return isoDateStr;
        }
    }

    public static String convertIsoToDisplayDateTime(String isoDateTimeStr) {
        if (isoDateTimeStr == null || isoDateTimeStr.isEmpty()) return "";
        try {
            if (isoDateTimeStr.contains("T")) {
                return LocalDateTime.parse(isoDateTimeStr).format(DISPLAY_DATETIME_FORMATTER);
            }
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTimeStr, DATETIME_FORMATTER);
            return dateTime.format(DISPLAY_DATETIME_FORMATTER);
        } catch (Exception e) {
            return isoDateTimeStr;
        }
    }
}
