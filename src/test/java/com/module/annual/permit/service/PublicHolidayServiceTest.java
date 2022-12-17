package com.module.annual.permit.service;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PublicHolidayServiceTest {

    @Before
    public void setUp() {
        PublicHolidayService.publicHolidayAsFormatList = Collections.singletonList("1970-01-01");
        PublicHolidayService.publicHolidayExternalFormat =  new SimpleDateFormat("yyyy-MM-dd");
    }

    @Test
    public void whenIsDayPublicHolidayCalledWithNonHolidayDate_itShouldReturnFalse() {
        //given
        Date date = new Date();

        //then
        Boolean actual = PublicHolidayService.isDayPublicHoliday(date);

        assertEquals(false, actual);
    }

}