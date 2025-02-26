package br.gov.es.invest.utils;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return offsetDateTime.format(formatter);
    }

    public static ZonedDateTime getZonedDateTime(String formatedDateTime) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        return ZonedDateTime.parse(formatedDateTime, formatter);
    }

}
