package com.company.common.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

public class TimeUtils {

    // private static Logger logger = Logger.getLogger(TimeUtil.class);

    public static final String STR_DATETIME_PATTERN = "yyyyMMddHHmmss";

    public static final String STR_DATETIME_PATTERN_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String STR_DATETIME_PATTERN_LONG2 = "yyyy-MM-dd HH:mm";
    public static final String DAY_FORMAT_1 = "yyyy-MM-dd";

    public static final String DAY_FORMAT_2 = "yyyyMMdd";

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DAY_FORMAT_3 = "yyyy-MM-dd HH:mm";

    public static final long MILLS_PER_DAY = 1000 * 60 * 60 * 24;

    public static final long MINUTE = 1000 * 60;


    public static String getYearMonth() {
        LocalDate localDate = LocalDate.now();
        return localDate.getYear() +""+ localDate.getMonthValue()  ;
    }

    public static String dateFormat(Integer date) {
        Date formatDate = null;
        try {
            formatDate = FastDateFormat.getInstance("yyyyMMdd").parse(String.valueOf(date));
        } catch (Exception e) {
            return "";
        }
        return FastDateFormat.getInstance("yyyy-MM-dd").format(formatDate);
    }

    public static String getGapTime(long time) {
        long ms = 3000;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }

    /**
     * 指定日期的前几天
     *
     * @param cal
     * @param day
     * @return
     */
    public static Date beforeDay(Calendar cal, int day) {
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - day);
        return cal.getTime();
    }

    /**
     * 每月第一个星期一是第一周
     * 传入日期判断属于哪一年哪一月第几周
     *
     * @param date 时间格式(yyyyMMdd) 20200701
     * @return java.lang.String 返回字符串(2020-06-W5) 2020年6月第5周
     * @apiNote 以每月的第一个周一所在的周作为每月第一周
     * @author kiring
     */
    public static String getWeekOfMonthByDay(int date) {
        DateTimeFormatter dfDay = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.of(date / 10000, (date / 100) % 100, date % 100);
        // 获得当前日期的所在周的周一(previousOrSame:如果当前日期是周一，就返回当前日期)
        LocalDate localDateMondy = LocalDate.of(date / 10000, (date / 100) % 100, date % 100).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate firstMonday = null;
        // 获得这个月的第一个周一(这个月的第一周)
        for (int day = 1; day <= 7; day++) {
            DayOfWeek dayOfWeek = LocalDate.of(date / 10000, (date / 100) % 100, day).getDayOfWeek();
            if (DayOfWeek.MONDAY == dayOfWeek) {
                firstMonday = LocalDate.of(date / 10000, (date / 100) % 100, day);
                break;
            }
        }
        String outYear = null;
        String outMonth = null;
        String outWeek = null;
        // 根据两个周一判断是这个月的第几周
        if (firstMonday.isBefore(localDateMondy) || firstMonday.isEqual(localDateMondy)) {
            //a. 如果当月第一个周一小于等于当前日期所在的周一
            outYear = localDateMondy.format(dfDay).substring(0, 4);
            outMonth = localDateMondy.format(dfDay).substring(4, 6);
            outWeek = String.valueOf((localDateMondy.toEpochDay() - firstMonday.toEpochDay()) / 7 + 1);
            return outYear + "-" + outMonth + "-" + "W" + outWeek;
        } else {
            //b. 如果当月第一个周一比当前日期所在的周一还要大
            // 计算上一个月最后一天所在周
            LocalDate lastMonthDate = localDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            Integer lastMonthDay = Integer.valueOf(lastMonthDate.format(dfDay));
            return getWeekOfMonthByDay(lastMonthDay);
        }
    }


    /**
     * 每月1号第一周
     * 计算日期在月中的周
     *
     * @param day 时间格式如：20200606
     * @return java.lang.Integer 日期在月中的周数
     * @author kiring
     */
    public static Integer calculateWeekInMonth(Integer day) {
        if (day < 19700101 || day > 99999999) {
            throw new RuntimeException("时间不正确");
        }
        // ISO算法：每个月的第一周至少4天，如果小于4天，算出来是第0周
        // WeekFields weekFields = WeekFields.ISO;
        // 以周一作为一周的开始，每周至少一天
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        int monthInWeek = LocalDateTime.of(day / 10000, (day / 100) % 100, day % 100, 0, 0, 0).atZone(ZoneOffset.ofHours(8)).get(weekFields.weekOfMonth());
        return monthInWeek;
    }

    public static void main(String[] args) {

        System.out.println(getYearMonth());
//        System.out.println(calculateWeekInMonth(20220606));
//
//        System.out.println(getWeekOfMonthByDay(20220606));
//        long ms = 3000;
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
//        String hms = formatter.format(ms);
//        System.out.println(hms);
    }


    public static BigDecimal secondToMinute(Long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        BigDecimal a = new BigDecimal(remainingSeconds);
        BigDecimal b = new BigDecimal(60);
        BigDecimal c = new BigDecimal(minutes);
        BigDecimal d = a.divide(b, 2, BigDecimal.ROUND_HALF_UP);
        return c.add(d);
    }

    public static List<Date> findDates(Date dBegin, Date dEnd) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }


    /**
     * 产生周序列
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1) {
            week = "0 " + week;
        }
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;

    }

    /**
     * 获得周一的日期
     *
     * @param date
     * @return
     */
    public static String getMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new SimpleDateFormat("yyyy-MM-dd ").format(c.getTime());
    }

    public static String dateTime2String(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        return simpleDateFormat.format(date);
    }

    /**
     * 获得周五的日期
     *
     * @param date
     * @return
     */
    public static String getFriday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return new SimpleDateFormat("yyyy-MM-dd ").format(c.getTime());
    }

    public static List<String> getMonth() {
        List<String> monthlst = new ArrayList<String>();

        monthlst.add("01");
        monthlst.add("02");
        monthlst.add("03");
        monthlst.add("04");
        monthlst.add("05");
        monthlst.add("06");
        monthlst.add("07");
        monthlst.add("08");
        monthlst.add("09");
        monthlst.add("10");
        monthlst.add("11");
        monthlst.add("12");

        return monthlst;
    }

    /**
     * 得到当前年开始时间
     *
     * @return
     */
    public static Date getCurrentYearStartDate() {
        Calendar cal = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(cal.get(Calendar.YEAR));
        sb.append("01");
        sb.append("01");
        sb.append("00").append("00").append("00");
        return new Date(dateTimeStrToMilsec(sb.toString(), null));
    }

    /**
     * 得到当前年结束时间
     *
     * @return
     */
    public static Date getCurrentYearEndDate() {
        Calendar cal = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(cal.get(Calendar.YEAR));

        sb.append("12");
        sb.append("31");
        sb.append("23").append("59").append("59");

        return new Date(dateTimeStrToMilsec(sb.toString(), null));
    }


    public static String getSta(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return new SimpleDateFormat("yyyyMMdd").format(c.getTime());

    }

    public static String getSum(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return new SimpleDateFormat("yyyyMMdd").format(c.getTime());

    }

    /**
     * 将日期时间的字符串转化为毫秒数
     *
     * @param strDateTime ,日期时间字符串,格式为pattern的格式
     * @param pattern     日期时间字符串的格式,可为空,默认为"yyyyMMddHHmmss"
     * @return 毫秒数
     */
    public static long dateTimeStrToMilsec(String strDateTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat((pattern == null) ? STR_DATETIME_PATTERN : pattern);

        try {
            return sdf.parse(strDateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 将日期时间的字符串转化为秒数
     *
     * @param strDateTime ,日期时间字符串,格式为pattern的格式
     * @param pattern     日期时间字符串的格式,可为空,默认为"yyyyMMddHHmmss"
     * @return 秒数
     */
    public static long dateTimeStrToSecond(String strDateTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat((pattern == null) ? STR_DATETIME_PATTERN : pattern);

        try {
            return sdf.parse(strDateTime).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 将毫秒数日期时间转化为字符串
     *
     * @param milsecDateTime ,毫秒数日期时间
     * @param pattern        日期时间字符串的格式,可为空,默认为"yyyyMMddHHmmss"
     * @return
     */
    public static String dateTimeMilsecToStr(long milsecDateTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat((pattern == null) ? STR_DATETIME_PATTERN : pattern);
        Date dateTime = new Date(milsecDateTime);
        return (dateTime == null) ? null : sdf.format(dateTime);
    }

    /**
     * 将日期时间转化为指定Pattern的字符串
     *
     * @param dateTime ,日期时间
     * @param pattern  ，格式,默认“yyyyMMddHHmmss”
     * @return
     */
    public static String dateTimeToStr(Date dateTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat((pattern == null) ? STR_DATETIME_PATTERN : pattern);

        return (dateTime == null) ? null : sdf.format(dateTime);
    }

    /**
     * 将时间字符串从原格式转化为目标格式
     *
     * @param dateTimeStr ,时间字符串
     * @param srcPattern  ，原格式
     * @param destPattern ，目标格式
     * @return
     */
    public static String dateTimeStrChangePattern(String dateTimeStr, String srcPattern, String destPattern) {
        if (dateTimeStr == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(srcPattern);
        Date date;

        try {
            date = sdf.parse(dateTimeStr);
            sdf.applyPattern(destPattern);

            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Get current date
     *
     * @return java.util.Date
     */
    public static java.util.Date getCurrentTime() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * @return
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get current year
     *
     * @return int
     */
    public static int getCurrentYear() {
        return getYear(getCurrentTime());
    }

    /**
     * Get current month
     *
     * @return int
     */
    public static int getCurrentMonth() {

        return getMonth(getCurrentTime());
    }

    public static int getMonth(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.MONTH);
    }

    /**
     * Get current minute
     *
     * @return int
     */
    public static int getCurrentMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    /**
     * Get current second
     *
     * @return int
     */
    public static int getCurrentSecond() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.SECOND);
    }

    /**
     * Get current second
     *
     * @return int
     */
    public static int getCurrentMillSecond() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MILLISECOND);
    }

    public static Date getCurrentStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getCurrentEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * Get current day in the month
     *
     * @return int
     */
    public static int getCurrentDay() {
        return getDay(TimeUtils.getCurrentTime());
    }

    public static int getDay(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get current hour
     *
     * @return int
     */
    public static int getCurrentHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Construct the Date object according to the string and the specifed format.
     *
     * @param dateValue
     * @param dateFormat
     * @return Date
     */
    public static java.util.Date string2Date(String dateValue, String dateFormat) {
        return string2Date(dateValue, dateFormat, null);
    }

    /**
     * Parse string to date according to the specified format,if exception
     * occurs,return the specified default value
     *
     * @param dateValue
     * @param dateFormat
     * @param defaultValue
     * @return date
     */
    public static java.util.Date string2Date(String dateValue, String dateFormat, Date defaultValue) {
        java.util.Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            date = sdf.parse(dateValue);
        } catch (Exception eDate) {
            return defaultValue;
        }
        return date;
    }

    /**
     * Parse the given string into date.
     *
     * @param dateValue
     * @return Date
     */
    public static Date string2Date(String dateValue) {
        if (dateValue == null) {
            return null;
        }
        if (dateValue.length() == DAY_FORMAT_1.length()) {
            return string2Date(dateValue, DAY_FORMAT_1);
        } else if (dateValue.length() == TIME_FORMAT.length()) {
            return string2Date(dateValue, TIME_FORMAT);
        } else {
            return null;
        }

    }

    /**
     * Parse string to date according to the specified format,if exception
     * occurs,return null
     *
     * @param dateValue
     * @param defaultValue
     * @return
     */
    public static Date string2Date(String dateValue, Date defaultValue) {
        return string2Date(dateValue, DAY_FORMAT_1, defaultValue);
    }

    /**
     * Adds the specified (signed) amount of time to the date time field.
     *
     * @param date
     * @param days
     * @return date
     */
    public static Date addDate(Date date, int days) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * Adds the specified (signed) amount of time to the hour-of-day field.
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date addHour(Date date, int hours) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c.getTime();
    }

    /**
     * Adds the specified (signed) amount of time to the minutes field.
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinute(Date date, int minutes) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /**
     * Adds the specified (signed) amount of time to the seconds field.
     *
     * @param date
     * @param seconds
     * @return
     */
    public static Date addSecond(Date date, int seconds) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    /**
     * Adds the specified (signed) amount of time to the date time field.
     *
     * @param date
     * @param days
     * @return date
     */
    public static Timestamp addDate(Timestamp date, int days) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new Timestamp(c.getTime().getTime());
    }

    /**
     * Adds the specified (signed) amount of time to the hour-of-day field.
     *
     * @param date
     * @param hours
     * @return
     */
    public static Timestamp addHour(Timestamp date, int hours) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return new Timestamp(c.getTime().getTime());
    }

    /**
     * Adds the specified (signed) amount of time to the minutes field.
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Timestamp addMinute(Timestamp date, int minutes) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return new Timestamp(c.getTime().getTime());
    }

    /**
     * Adds the specified (signed) amount of time to the seconds field.
     *
     * @param date
     * @param seconds
     * @return
     */
    public static Timestamp addSecond(Timestamp date, int seconds) {
        if (date == null) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return new Timestamp(c.getTime().getTime());
    }

    /**
     * Parse the give date time to string format with the default time format
     *
     * @param dateValue
     * @return string
     */
    public static String date2String(Date dateValue) {
        return date2String(dateValue, DAY_FORMAT_1);
    }

    /**
     * Parse the given time to string format
     *
     * @param dateValue
     * @param dateFormat
     * @return string
     */
    public static String date2String(java.util.Date dateValue, String dateFormat) {
        if (dateValue == null) {
            return "";
        }
        String sDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sDate = sdf.format(dateValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex.getMessage());
        }
        return sDate;
    }

    /**
     * Returns the time when is the fist monday from now on. Hours,minutes,and
     * seconds are all set to be zero.
     *
     * @return time
     */
    public static Calendar getFirstMonday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return getFirstMonday(c);
    }

    /**
     * Returns the time when is the fist monday from now on. Hours,minutes,and
     * seconds are all set to be zero.
     *
     * @return time
     */
    public static Calendar getFirstMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getFirstMonday(c);
    }

    /**
     * Returns the time when is the fist monday from the given time.
     * Hours,minutes,and seconds are all set to be zero.
     *
     * @return time
     */
    public static Calendar getFirstMonday(Calendar c) {
        for (int dow = c.get(Calendar.DAY_OF_WEEK); dow != Calendar.MONDAY; dow = c.get(Calendar.DAY_OF_WEEK)) {
            c.add(Calendar.DATE, 1);
        }
        return c;
    }

    public static Date getFirstDayOfYear() {
        Calendar c = Calendar.getInstance();
        return getFirstDayOfYear(c.get(Calendar.YEAR));
    }

    private static Date getFirstDayOfYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Format time by the default time.
     *
     * @param time
     * @return string
     */
    public static String format(Date time) {
        return format(time, DAY_FORMAT_1);
    }

    /**
     * Format time by the specified time.
     *
     * @param time
     * @param format
     * @return
     */
    public static String format(Date time, String format) {
        if (time == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(time);
        } catch (Exception eDate) {
            return time.toString();
        }
    }

    /**
     * Returns year of the specified time.
     *
     * @param time
     * @return year
     */
    public static int getYear(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.YEAR);
    }

    /**
     * Returns true iff the specified time is <code>Sunday</code>
     *
     * @param time
     * @return
     */
    public static boolean isSunday(Date time) {
        if (time == null) {
            throw new NullPointerException(" argument is null ");
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    /**
     * Returns true iff the specified time is <code>Saturday</code>
     *
     * @param time
     * @return
     */
    public static boolean isSaturday(Date time) {
        if (time == null) {
            throw new NullPointerException(" argument is null ");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    public static Date getMonday4Sunday2Saturday(Date date) {
        if (date == null) {
            throw new NullPointerException(" specified date is null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DATE, 1);
        } else {
            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                c.add(Calendar.DATE, -1);
            }
        }
        return c.getTime();
    }

    /**
     * Returns the time when is the monday of the current week.
     *
     * @return Date
     */
    public static Date getMondayOfWeek() {
        return getMondayOfWeek(getCurrentTime());
    }

    /**
     * Returns the time when is the monday of the current week.
     *
     * @param thisWeek
     * @return Date
     */
    public static Date getMondayOfWeek(Date thisWeek) {
        Calendar c = Calendar.getInstance();
        c.setTime(thisWeek);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            c.add(Calendar.DATE, -1);
        }
        return c.getTime();
    }

    public static int differentDays(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        c2.set(Calendar.HOUR, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);

        long l1 = c1.getTimeInMillis();
        long l2 = c2.getTimeInMillis();

        return (int) ((l1 - l2) / MILLS_PER_DAY);
    }

    public static int differentMinutes(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        long l1 = c1.getTimeInMillis();
        long l2 = c2.getTimeInMillis();

        return (int) ((l1 - l2) / MINUTE);
    }

}
