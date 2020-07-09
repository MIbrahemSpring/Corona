package com.mohamed.corona.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Utility {

	public String getDayBeforeFormated(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		String formateddate = sdf.format(yesterday);
		return formateddate;
	}

	public List<String> getFirstDayOfEachMonth(int year) {
		List<String> firstDayForEachMonth = new ArrayList<String>();
		List<Calendar> listOfCalendar = new ArrayList<Calendar>();
		for (int i = 0; i < 12; i++) {
			Calendar c = Calendar.getInstance();
			c.set(year, i, 1);
			listOfCalendar.add(c);
		}
		for (Calendar calendar : listOfCalendar) {

			if (calendar.getTime().before(new Date())) {
				firstDayForEachMonth.add(new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()));
			} else {
				firstDayForEachMonth.add(getDayBeforeFormated(new Date()));
				break;
			}

		}

		return firstDayForEachMonth;

	}
}
