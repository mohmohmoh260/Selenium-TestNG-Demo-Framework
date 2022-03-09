package com.test.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeAndDate {

	public String Date() {
		
		String DateStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		
		return DateStamp;
	}
	
	public String Time() {
		
		String TimeStamp = new SimpleDateFormat("HH.mm.ss").format(Calendar.getInstance().getTime());
		
		return TimeStamp;
	}
}
