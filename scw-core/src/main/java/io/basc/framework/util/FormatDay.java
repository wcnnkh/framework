package io.basc.framework.util;

import java.io.Serializable;

public class FormatDay implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long day;
	private final long hour;
	private final long minute;
	private final long sec;
	private final long msec;

	public FormatDay(long time) {
		long t = time;
		this.msec = t % 1000;
		t -= msec;
		t /= 1000;

		this.sec = t % 60;
		t -= sec;
		t /= 60;

		this.minute = t % 60;
		t -= minute;
		t /= 60;

		this.hour = t % 24;
		t -= hour;
		t /= 24;

		this.day = t % 24;
	}

	public FormatDay(long day, long hour, long minute, long sec, long msec) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.sec = sec;
		this.msec = msec;
	}

	public long getDay() {
		return day;
	}

	public long getHour() {
		return hour;
	}

	public long getMinute() {
		return minute;
	}

	public long getSec() {
		return sec;
	}

	public long getMsec() {
		return msec;
	}
}
