package com.module.annual.permit.util;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static int getYearDifferenceBetweenDates(Date currentDate, Date previousDate) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(currentDate);
		int currentYear = calendar.get(Calendar.YEAR);

		calendar.setTime(previousDate);
		int previousYear = calendar.get(Calendar.YEAR);

		return currentYear - previousYear;
	}

	public static Long getDayDifferenceBetweenDates(Date firstDate, Date secondDate) {
		long oneDayInMilliseconds = 24 * 60 * 60 * 1000L;
		long difference = firstDate.getTime() - secondDate.getTime();

		return difference / oneDayInMilliseconds;

	}

}
