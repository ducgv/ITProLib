/**
 * 
 */
package com.itpro.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author ducgv
 *
 */
public class DateTime {
	public static String getMMDDHHMISS(){
		long timestamp = System.currentTimeMillis();
		Date date = new Date(timestamp);
		Calendar currentTimestamp = Calendar.getInstance();
		currentTimestamp.setTime(date);
		String result = String.format("%02d%02d%02d%02d%02d",
				currentTimestamp.get(Calendar.MONTH)+1,
				currentTimestamp.get(Calendar.DAY_OF_MONTH),
				currentTimestamp.get(Calendar.HOUR),
				currentTimestamp.get(Calendar.MINUTE),
				currentTimestamp.get(Calendar.SECOND));
		return result;
	}
	
	public static String getYYMMDD(){
		long timestamp = System.currentTimeMillis();
		Date date = new Date(timestamp);
		Calendar currentTimestamp = Calendar.getInstance();
		currentTimestamp.setTime(date);
		String result = String.format("%02d%02d%02d",
				currentTimestamp.get(Calendar.YEAR)%100,
				currentTimestamp.get(Calendar.MONTH)+1,
				currentTimestamp.get(Calendar.DAY_OF_MONTH));
		return result;
	}
}
