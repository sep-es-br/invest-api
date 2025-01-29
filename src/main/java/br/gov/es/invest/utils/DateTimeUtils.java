package br.gov.es.invest.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return zonedDateTime.format(formatter);
    }

    public static ZonedDateTime getZonedDateTime(String formatedDateTime) {
              
        return ZonedDateTime.parse(formatedDateTime);
    }

}
