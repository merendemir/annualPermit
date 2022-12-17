package com.module.annual.permit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 * @param currentDate @Description Date to decrease.
	 * @param previousDate Date to subtrahend
	 * @return Difference between Dates as year.
	 *
	 * This method finds the year difference between the entered dates.
	 */
	public static int getYearDifferenceBetweenDates(Date currentDate, Date previousDate) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(currentDate);
		int currentYear = calendar.get(Calendar.YEAR);

		calendar.setTime(previousDate);
		int previousYear = calendar.get(Calendar.YEAR);

		return currentYear - previousYear;
	}

	/**
	 * @param date @Description Start date to be added.
	 * @param year @Description The number of years to add to the start date.
	 * @return The date the years are added to the start date.
	 *
	 * This method adds the desired number of years to the start date.
	 */
	public static Date getFutureYearByDateAndYear(Date date, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);

		return calendar.getTime();
	}

	/**
	 * @param date @Description The date you want to know if it is a weekend.
	 * @return Boolean
	 *
	 * This method determines whether the given day is a weekend or not.
	 */
	public static Boolean isDayWeekend(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	/**
	 * @param date @Description Start date to be added.
	 * @param days @Description The number of days to add to the start date.
	 * @return The date the days are added to the start date.
	 *
	 * This method adds the desired number of days to the start date.
	 */
	public static Date getFutureDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.add(Calendar.DATE, + days);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * @param date The day, on which the starting point will be calculated.
	 * @return Start Of day.
	 *
	 * This method returns midnight of the given day.
	 */
	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * @param date @Description Date to be translated.
	 * @return Translated date.
	 *
	 * This method converts the given date to dd.MM.yyyy format.
	 */
	public static String formatDateToSimpleDateFormat(Date date) {
		return simpleDateFormat.format(date);
	}

	/**
	 *
	 * @param parseDate @Description String to convert to date format.
	 * @return Date
	 *
	 * This method converts the value given as a string to date format.
	 */
	public static Date parseDateToSimpleDateFormat(String parseDate) throws ParseException {
		return simpleDateFormat.parse(parseDate);
	}


}
