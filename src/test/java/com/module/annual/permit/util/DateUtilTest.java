package com.module.annual.permit.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    @Test
    public void whenGetYearDifferenceBetweenDatesCalled_itShouldReturnYearBetween() {
        //given
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);

        //then
        int actual = DateUtil.getYearDifferenceBetweenDates(oneYearLater, currentDate);

        assertEquals(1, actual);
    }

    @Test
    public void whenGetFutureYearByDateAndYearCalled_itShouldReturnNextYearDate() {
        //given
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);

        //then
        Date actual = DateUtil.getFutureYearByDateAndYear(currentDate, 1);

        assertEquals(oneYearLater, actual);
    }

    @Test
    public void whenIsDayWeekendCalledWithWeekendDay_itShouldReturnTrue() {
        //given
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        Date date = calendar.getTime();

        //then
        Boolean actual = DateUtil.isDayWeekend(date);

        assertEquals(true, actual);
    }

    @Test
    public void whenGetFutureDateCalled_itShouldReturnFutureDate() {
        //given
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();

        Date oneYearLater = this.getOneYearLater(currentDate);

        calendar.setTime(oneYearLater);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date oneYearLaterMidnight = calendar.getTime();


        //then
        Date actual = DateUtil.getFutureDate(currentDate, 365);

        assertEquals(oneYearLaterMidnight, actual);
    }

    @Test
    public void whenGetStartOfDayCalled_itShouldReturnStartOfDay() {
        //given
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startOfDay = calendar.getTime();

        //then
        Date actual = DateUtil.getStartOfDay(currentDate);

        assertEquals(startOfDay, actual);
    }

    @Test
    public void whenFormatDateToSimpleDateFormatCalled_itShouldReturnFormattedDate() {
        //given
        DateUtil.simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String formattedDate = "01.01.1970";

        //then
        String actual = DateUtil.formatDateToSimpleDateFormat(date);

        assertEquals(formattedDate, actual);
    }

    @Test
    public void whenParseDateToSimpleDateFormatCalled_itShouldReturnParsedDate() throws ParseException {
        //given
        DateUtil.simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        String formattedDate = "01.01.1970";

        //then
        Date actual = DateUtil.parseDateToSimpleDateFormat(formattedDate);

        assertEquals(date, actual);
    }


    public Date getOneYearLater(Date date) {
        return new Date(date.getTime() + 365 * 24 * 60 * 60 *1000L);
    }

}