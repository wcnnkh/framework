package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 月
 * 
 * @author soeasy.run
 *
 */
public class Month extends TimeUnit {
	public static final Month DEFAULT = new Month();

	public Month() {
		super("yyyy-MM", Calendar.MONTH, Day.DEFAULT);
	}

	@Override
	public long distance(Calendar startDate, Calendar endDate) {
		int months = 0;

		// 确保开始日期在结束日期之前
		if (startDate.after(endDate)) {
			Calendar temp = startDate;
			startDate = endDate;
			endDate = temp;
		}

		int startYear = startDate.get(Calendar.YEAR);
		int endYear = endDate.get(Calendar.YEAR);
		int startMonth = startDate.get(Calendar.MONTH);
		int endMonth = endDate.get(Calendar.MONTH);

		// 计算年份差
		months += (endYear - startYear) * 12;

		// 计算月份差
		months += (endMonth - startMonth);

		// 如果结束日期的日期部分小于开始日期的日期部分，则减去1个月
		if (endDate.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
			months--;
		}

		return months;
	}
}
