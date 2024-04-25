package com.kdh.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeAgo {
	 public static String calculateTimeAgo(LocalDateTime createdDate) {
		 
	        LocalDateTime now = LocalDateTime.now();
	        long years = ChronoUnit.YEARS.between(createdDate, now);
	        long months = ChronoUnit.MONTHS.between(createdDate, now);
	        long days = ChronoUnit.DAYS.between(createdDate, now);
	        long hours = ChronoUnit.HOURS.between(createdDate, now);
	        long minutes = ChronoUnit.MINUTES.between(createdDate, now);

	        if (years > 0) return years + "년 전";
	        else if (months > 0) return months + "개월 전";
	        else if (days > 0) return days + "일 전";
	        else if (hours > 0) return hours + "시간 전";
	        else if (minutes > 0) return minutes + "분 전";
	        else return "방금 전";
	    }
}
