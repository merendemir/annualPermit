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

	public static Boolean isDayWeekend(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public static Date getFutureDate(Date currentDate, int days) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, + days);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}


}
