package com.dev.springbootserver.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {

    private static final String zoneId = "America/Sao_Paulo";

    public static boolean isSameDay(Long today, Long day) {
        ZonedDateTime zonedDateTimeToday = longToZonedDateTime(today);
        ZonedDateTime zonedDateTimeDay = longToZonedDateTime(day);

        return zonedDateTimeToday.getYear() == zonedDateTimeDay.getYear() &&
                zonedDateTimeToday.getDayOfYear() == zonedDateTimeDay.getDayOfYear();
    }

    public static ZonedDateTime longToZonedDateTime(long timestamp) {
        return new Timestamp(timestamp).toInstant().atZone(ZoneId.of(zoneId));
    }

    public static long getTimestampOfSP() {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of(zoneId));
        Timestamp timestamp = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        return timestamp.getTime();
    }
}
