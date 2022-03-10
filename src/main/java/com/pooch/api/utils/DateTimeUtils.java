package com.pooch.api.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtils {

    // Millisecond Based
    public static final long   MILLISECOND                        = 1000L;

    public static final long   SECOND                             = MILLISECOND;

    public static final long   MINUTE                             = SECOND * 60;

    public static final long   HOUR                               = MINUTE * 60;

    public static final long   DAY                                = HOUR * 24;

    public static final long   WEEK                               = DAY * 7;

    public static final int    NUMBER__OF_DAYS_TIL_START_COVERAGE = 13;

    public static final String UTC_FORMAT                         = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String UTC_FORMAT_WITHOUT_TAG             = "yyyy-MM-dd HH:mm:ss";

    public static final String MONTH_DATE_YEAR                    = "MMMM dd, yyyy";

    public static final String DAYOFWEEK_MONTH_DATE_YEAR          = "E, MMMM dd, yyyy";

    /**
     * Based on Milliseconds
     * 
     * @param hr
     * @return hours in milliseconds
     */
    public static long getHoursInMilliseconds(long hr) {
        return HOUR * hr;
    }

    public static String getTimeStamp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateFormat formmatter = new SimpleDateFormat("M-dd-yyyy h:mm:ss a");
        return formmatter.format(new Date());
    }

    public static String getFormattedDate(Date date, String format) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateFormat formmatter = new SimpleDateFormat(format);
        return formmatter.format(date);
    }

    public static Date getUTCDateFromMysqlString(String strDate) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new SimpleDateFormat(UTC_FORMAT_WITHOUT_TAG).parse(strDate);
    }

    /**
     * start is inclusive<br>
     * end is exclusive<br>
     * Work around for exclusive end date is to add one more day to end date.<br>
     */
    public static int getDiffMonths(Date start, Date end) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        LocalDate startDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period period = Period.between(startDate, endDate);

        int numOfYears = period.getYears();

        int numOfMonths = period.getMonths();

        if (numOfYears >= 1) {
            numOfMonths += (numOfYears * 12);
        }
        return numOfMonths;
    }

    public static int getDiffHours(Date start, Date end) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        long different = end.getTime() - start.getTime();
        return (int) (different / HOUR);
    }

    public static int getDiffDays(Date start, Date end) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        long different = end.getTime() - start.getTime();
        return (int) (different / DAY);
    }

    /**
     * Get the Date in UTC from the Local Time Zone based on start of the day.
     * 
     * @param effectiveDate
     * @param timeZone
     * @return
     */
    public static Date getDateInUTCFromLocalTimeZone(Date effectiveDate, String timeZone) {
        ZoneId z = ZoneId.of(timeZone);
        LocalDateTime effectiveLocalDate = effectiveDate.toInstant().atZone(z).toLocalDate().atStartOfDay();
        Date effectiveSystemDate = Date.from(effectiveLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        return effectiveSystemDate;
    }

    public static LocalDate getNextLocalDateInUTCFromLocalTimeZone(Date effectiveDate, ZoneId zoneId) {
        LocalDate effectiveLocalDate = effectiveDate.toInstant().atZone(zoneId).toLocalDate().plusDays(1).atStartOfDay().toLocalDate();
        return effectiveLocalDate;
    }

    /**
     * Get the whole days based on dates. Since the endDate is not inclusive, we add an extra date
     * 
     * @param start
     * @param end
     * @return
     */
    public static int getWholeDaysBetween(Date start, Date end) {
        LocalDate startLocal = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocal = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (int) ChronoUnit.DAYS.between(startLocal, endLocal);
    }

    public static String getFormattedDate(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateFormat formmatter = new SimpleDateFormat("M-dd-yyyy h:mm:ss.SSS a");
        return formmatter.format(date);
    }

    public static String getUTCFormattedDate(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateFormat formmatter = new SimpleDateFormat(UTC_FORMAT);
        return formmatter.format(date);
    }

    public static String getDOBAsText(Date dob) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateFormat formmatter = new SimpleDateFormat("yyyy-MM-dd");
        return formmatter.format(dob);
    }

    public static Date getLastSecondOfDayDateTime(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(date);

        startDateCalendar.set(Calendar.HOUR_OF_DAY, startDateCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        startDateCalendar.set(Calendar.MINUTE, startDateCalendar.getActualMaximum(Calendar.MINUTE));
        startDateCalendar.set(Calendar.SECOND, startDateCalendar.getActualMaximum(Calendar.SECOND));
        startDateCalendar.set(Calendar.MILLISECOND, startDateCalendar.getActualMaximum(Calendar.MILLISECOND));

        return startDateCalendar.getTime();
    }

    public static Date getFirstSecondOfDayDateTime(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(date);

        startDateCalendar.set(Calendar.HOUR_OF_DAY, startDateCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        startDateCalendar.set(Calendar.MINUTE, startDateCalendar.getActualMinimum(Calendar.MINUTE));
        startDateCalendar.set(Calendar.SECOND, startDateCalendar.getActualMinimum(Calendar.SECOND));
        startDateCalendar.set(Calendar.MILLISECOND, startDateCalendar.getActualMinimum(Calendar.MILLISECOND));

        return startDateCalendar.getTime();
    }

    public static boolean isValidStartDate(Date startDate) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date now = new Date();

        int numOfDays = DateTimeUtils.getDiffDays(now, startDate);

        return (numOfDays >= NUMBER__OF_DAYS_TIL_START_COVERAGE) ? true : false;
    }

    public static Date getBeginingOfDate(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(date);

        startDateCalendar.set(Calendar.HOUR_OF_DAY, startDateCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        startDateCalendar.set(Calendar.MINUTE, startDateCalendar.getActualMinimum(Calendar.MINUTE));
        startDateCalendar.set(Calendar.SECOND, startDateCalendar.getActualMinimum(Calendar.SECOND));
        startDateCalendar.set(Calendar.MILLISECOND, startDateCalendar.getActualMinimum(Calendar.MILLISECOND));

        return startDateCalendar.getTime();
    }

    public static Date getEndingOfDate(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(date);

        endDateCalendar.set(Calendar.HOUR_OF_DAY, endDateCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        endDateCalendar.set(Calendar.MINUTE, endDateCalendar.getActualMaximum(Calendar.MINUTE));
        endDateCalendar.set(Calendar.SECOND, endDateCalendar.getActualMaximum(Calendar.SECOND));
        endDateCalendar.set(Calendar.MILLISECOND, endDateCalendar.getActualMaximum(Calendar.MILLISECOND));

        return endDateCalendar.getTime();
    }

    public static int calculateAge(Date birthDate) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if (birthDate == null) {
            return 0;
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar today = Calendar.getInstance();

        int yearDifference = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
            yearDifference--;
        } else {
            if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
                yearDifference--;
            }
        }
        return yearDifference < 0 ? 0 : yearDifference;
    }

    /**
     * get date and set it to date at start of day on depicted timezone
     * 
     * @param date
     * @param zone
     * @return
     */
    public static Date getDateAtStartOfDay(Date date, ZoneId zone) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if (date != null && zone != null) {
            return Date.from(date.toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant());
        }
        return date;
    }

    /**
     * get date and set it to date at end of day for specified timezone
     * 
     * @param date
     * @param zone
     * @return
     */
    public static Date getDateAtEndOfDay(Date date, ZoneId zone) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if (date != null && zone != null) {
            return Date.from(date.toInstant().atZone(zone).toLocalDate().atTime(LocalTime.MAX).atZone(zone).toInstant());
        }
        return date;
    }

    /**
     * return date as UTC date string for database yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String toStringForDb(Date date) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if (date != null) {
            Instant instant = Instant.ofEpochMilli(date.getTime());
            LocalDateTime startLocalDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return startLocalDate.format(formatter);
        }
        return null;
    }

    /**
     * return zone, default to system if failing
     * 
     * @param timezone
     * @return
     */
    public static ZoneId getZoneDefault(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            // ignore return default
        }
        return ZoneId.systemDefault();
    }

    /**
     * reset to the start of the hour for a date in timezone
     * 
     * @param date
     * @param zone
     * @return
     */
    public static Date getDateAtStartOfTheHour(Date date, ZoneId zone) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if (date != null && zone != null) {
            LocalDateTime localDateTime = date.toInstant().atZone(zone).toLocalDateTime();
            localDateTime = localDateTime.truncatedTo(ChronoUnit.HOURS);
            return Date.from(localDateTime.atZone(zone).toInstant());
        }
        return date;
    }

    /**
     * reset to the start of the hour before for a date in timezone
     * 
     * @param date
     * @param zone
     * @return
     */
    public static Date getDateAtStartOfThePreviousHour(Date date, ZoneId zone) {
        if (date != null && zone != null) {
            LocalDateTime localDateTime = date.toInstant().atZone(zone).toLocalDateTime();
            localDateTime = localDateTime.truncatedTo(ChronoUnit.HOURS);
            localDateTime = localDateTime.minusHours(1);
            return Date.from(localDateTime.atZone(zone).toInstant());
        }
        return date;
    }

    /**
     * reset to the max of the hour for a date in timezone
     * 
     * @param date
     * @param zone
     * @return
     */
    public static Date getDateAtEndOfTheHour(Date date, ZoneId zone) {
        if (date != null && zone != null) {
            LocalDateTime localDateTime = date.toInstant().atZone(zone).toLocalDateTime();
            localDateTime = localDateTime.withMinute(59).withSecond(59).withNano(999_999_999);
            return Date.from(localDateTime.atZone(zone).toInstant());
        }
        return date;
    }

    public static Date getFirstDayOfNextMonth() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return new Date(cal.getTimeInMillis());
    }

    public static Date getDateAtStartOfPreviousMonth(ZoneId zone) {
        LocalDateTime localDateTime = LocalDateTime.now(zone);
        localDateTime = localDateTime.minusMonths(1);
        localDateTime = localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(1).withNano(0);
        return Date.from(localDateTime.atZone(zone).toInstant());
    }

    public static Date getDateAtEndOfPreviousMonth(ZoneId zone) {
        LocalDateTime localDateTime = LocalDateTime.now(zone); // e.g. 9/6/2019

        // Set to 1st of the month at 12:00:01 am e.g. 9/1/2019 00:00:01
        localDateTime = localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(1).withNano(0);

        // Subtract 2 seconds from 9/1/2019 12:00:01 am so that it becomes 8/31/2019
        // 23:59:59
        localDateTime = localDateTime.minusSeconds(2);

        return Date.from(localDateTime.atZone(zone).toInstant());
    }

    /**
     * yyyy-MM-dd
     * 
     * @param dateOfBirth
     * @return
     */
    public static Date getDOBFromString(String dateOfBirth) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth);
        } catch (ParseException e) {
            return null;
        }
    }

    public static SimpleDateFormat getFormat(String format) {
        // TODO Auto-generated method stub
        return new SimpleDateFormat(format);
    }

    public static int getCurrentYearFirstTwoDigits() {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        return Integer.parseInt(year.substring(0, 2));
    }

    public static int getCurrentYearLastTwoDigits() {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        return Integer.parseInt(year.substring(2, 4));
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getCurrentErraYear() {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        return Integer.parseInt(year.substring(0, 2) + "00");
    }

    public static int getRecentErraYear() {
        // current year first two digits - 1. example: 20 - 1 = 19
        String year = Integer.toString(getCurrentYearFirstTwoDigits() - 1);
        return Integer.parseInt(year.substring(0, 2) + "00");
    }

    public static Date getNow() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new Date();
    }

    public static LocalDate getUTCDateFromStripeTimestamp(Long stripeTimeStamp) {
        try {
            /**
             * Stripe timestamp is in unix_timestamp<br>
             * unix_timestamp * 1000 = UTC
             */
            return LocalDate.ofInstant(Instant.ofEpochMilli(stripeTimeStamp * 1000), ZoneId.of("UTC"));
        } catch (Exception e) {
            log.warn("getUTCDateFromStripeTimestamp Exception msg=" + e.getLocalizedMessage() + ", stripeTimeStamp=" + stripeTimeStamp);
            return null;
        }
    }

    public static LocalDateTime getUTCDateTimeFromStripeTimestamp(Long stripeTimeStamp) {
        try {
            /**
             * Stripe timestamp is in unix_timestamp<br>
             * unix_timestamp * 1000 = UTC
             */
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(stripeTimeStamp * 1000), ZoneId.of("UTC"));
        } catch (Exception e) {
            log.warn("getUTCDateTimeFromStripeTimestamp Exception msg=" + e.getLocalizedMessage() + ", stripeTimeStamp=" + stripeTimeStamp);
            return null;
        }
    }

    public static LocalDateTime getUTCDateTime() {
        try {
            return LocalDateTime.now(ZoneId.of("UTC"));
        } catch (Exception e) {
            log.warn("getUTCDateTimeFromStripeTimestamp Exception msg=" + e.getLocalizedMessage());
            return null;
        }
    }

    public static Date getDateFromString(String value) {
        // TODO Auto-generated method stub
        try {
            return new SimpleDateFormat("mm/dd/yyyy").parse(value);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static Date getDate(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    /**
     * Check if day1 is the same day as day2<br/>
     * day1 - 2020-6-12, day2 - 2020-6-13, return false <br/>
     * day1 - 2020-6-12, day2 - 2020-6-12, return false <br/>
     * day1 - 2020-6-13, day2 - 2020-6-12, return false <br/>
     */
    public static boolean isSameDay(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(day2);

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Check if day1 is after day2<br/>
     * day1 - 2020-6-12, day2 - 2020-6-13, return false <br/>
     * day1 - 2020-6-12, day2 - 2020-6-12, return false <br/>
     * day1 - 2020-6-13, day2 - 2020-6-12, return true <br/>
     */
    public static boolean isAfterDay(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(day2);

        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) {
            return false;
        } else if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)) {
            return false;
        } else if (cal1.get(Calendar.DAY_OF_MONTH) <= cal2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

    /**
     * Check if day1 is before day2<br/>
     * day1 - 2020-6-12, day2 - 2020-6-13, return true <br/>
     * day1 - 2020-6-12, day2 - 2020-6-12, return false <br/>
     * day1 - 2020-6-13, day2 - 2020-6-12, return false <br/>
     */
    public static boolean isBeforeDay(Date day1, Date day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(day2);

        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) {
            return false;
        } else if (cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH)) {
            return false;
        } else if (cal1.get(Calendar.DAY_OF_MONTH) >= cal2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

}
