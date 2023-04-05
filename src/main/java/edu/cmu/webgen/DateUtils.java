package edu.cmu.webgen;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.Parser;

public class DateUtils {

    /**
     * print a date in a readable format
     *
     * @param date the date
     * @return string representing the date
     */
    public static String readableFormat(LocalDateTime date) {
        return WebGen.formatter.format(date.atZone(ZoneId.systemDefault()));
    }

    /**
     * using external library to flexibly parse dates
     * <p>
     * requires a bit of hacking with different time formats
     *
     * @param inputDate input date string in human readable time/date format
     * @return parsed date as LocalDateTime
     * @throws ParseException if input date string cannot be parsed
     */
    public static LocalDateTime parseDate(String inputDate) throws ParseException {
        List<Date> dates = new Parser().parse(inputDate,
                Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())).get(0).getDates();
        if (dates.isEmpty())
            throw new ParseException("Cannot parse date %s".formatted(inputDate), 0);
        return LocalDateTime.ofInstant(dates.get(0).toInstant(), ZoneId.systemDefault());
    }

    /**
     * helper functions to convert FileTime into LocalDateTime
     *
     * @param fileTime input time
     * @return LocalDateTime object for the same file
     */
    public static LocalDateTime getDateTime(FileTime fileTime) {
        return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
    }
  
}
