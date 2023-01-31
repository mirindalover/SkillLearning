package com.hualala.v2.laobantong.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author moulianchao
 * @date 2022/5/9 10:03
 * 周相关的工具
 */
@Slf4j
public class WeekUtil {

    /**
     * 获取日期数据哪一年第几周
     * 默认从周一开始。一周在哪年多就属于哪年
     *
     * @param currentDate
     * @return
     */
    public static YearAndWeek getWeekOfYear(long currentDate) {
        return getWeekOfYear(currentDate, Calendar.MONDAY, 4);
    }

    /**
     * 获取年周的范围日期
     *
     * @return
     */
    public static YearAndWeek getWeekRange(YearAndWeek yearAndWeek) {
        return getWeekRange(yearAndWeek.getYear(), yearAndWeek.getWeek(), Calendar.MONDAY, 4);
    }

    public static YearAndWeek getWeekRange(int year, int week, int firstDayOfWeek, int minimalDaysInFirstWeek) {
        SimpleDateFormat commonFormat = new SimpleDateFormat("yyyyMMdd");
        YearAndWeek result = new YearAndWeek();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        calendar.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
        calendar.setWeekDate(year, week, Calendar.MONDAY);
        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        Date end = calendar.getTime();
        result.setYear(year);
        result.setWeek(week);
        result.setBeginDate(Long.parseLong(commonFormat.format(start)));
        result.setEndDate(Long.parseLong(commonFormat.format(end)));
        return result;
    }

    /**
     * 获取年的周列表
     */
    public static List<YearAndWeek> getWeekList(Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        boolean selectCurrent = year == currentYear;
        int monthNum = selectCurrent ? calendar.get(Calendar.WEEK_OF_YEAR) : 52;
        List<YearAndWeek> result = new ArrayList<>();
        YearAndWeek weekRange;
        for (int i = monthNum; i > 0; i--) {
            weekRange = getWeekRange(year, i, Calendar.MONDAY, 4);
            if (selectCurrent) {
                weekRange.setCurrentWeek(true);
                selectCurrent = false;
            }
            result.add(weekRange);
        }
        return result;
    }


    /**
     * @param currentDate
     * @param firstDayOfWeek         一周从周几开始算
     * @param minimalDaysInFirstWeek 一周几天在当年就算当年的
     * @return
     */
    public static YearAndWeek getWeekOfYear(long currentDate, int firstDayOfWeek, int minimalDaysInFirstWeek) {
        SimpleDateFormat commonFormat = new SimpleDateFormat("yyyyMMdd");
        YearAndWeek result = new YearAndWeek();
        try {
            Date date = commonFormat.parse(String.valueOf(currentDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(firstDayOfWeek);
            calendar.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
            calendar.setTime(date);

            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            //一月份，需要year-1
            if (month == Calendar.JANUARY && week >= 52) {
                year = year - 1;
            }

            //十二月份，需要year+1
            if (month == Calendar.DECEMBER && week == 1) {
                year = year + 1;
            }
            result.setWeek(week);
            result.setYear(year);
        } catch (Exception e) {
            log.error("",e);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("20211231:" + getWeekOfYear(20211231));
        System.out.println("20220101:" + getWeekOfYear(20220101));
        System.out.println("20220508:" + getWeekOfYear(20220508));
        System.out.println("20220509:" + getWeekOfYear(20220509));
        YearAndWeek yearAndWeek = new YearAndWeek();
        yearAndWeek.setYear(2021);
        yearAndWeek.setWeek(52);
        System.out.println("20220509:" + getWeekRange(yearAndWeek));
        long first = System.currentTimeMillis();
        System.out.println(getWeekList(2021));
        long second = System.currentTimeMillis();
        System.out.println(second - first);
        System.out.println(getWeekList(2022));
        long third = System.currentTimeMillis();
        System.out.println(third - second);
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YearAndWeek {

        private Integer year;

        private Integer week;

        private Long beginDate;

        private Long endDate;

        private boolean currentWeek;

        public YearAndWeek(Integer year, Integer week) {
            this.year = year;
            this.week = week;
        }
    }

}
