package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 年
 * 
 * @author soeasy.run
 *
 */
public class Year extends TimeUnit {
	public static final Year DEFAULT = new Year();

	public Year() {
		super(Calendar.YEAR);
	}

	@Override
	public void setMaxValue(Calendar value) {
		Month.DEFAULT.setMaxValue(value);
		super.setMaxValue(value);
	}

	@Override
	public void setMinValue(Calendar value) {
		Month.DEFAULT.setMinValue(value);
		super.setMinValue(value);
	}

	@Override
	public long distance(Calendar start, Calendar end) {
		// 确保开始日期在结束日期之前
		if (start.after(end)) {
			Calendar temp = start;
			start = end;
			end = temp;
		}

		int startYear = start.get(Calendar.YEAR);
		int endYear = end.get(Calendar.YEAR);
		int startMonth = start.get(Calendar.MONTH);
		int endMonth = end.get(Calendar.MONTH);
		int startDay = start.get(Calendar.DAY_OF_MONTH);
		int endDay = end.get(Calendar.DAY_OF_MONTH);

		// 计算初始年份差
		int years = endYear - startYear;

		// 如果结束月份小于开始月份，年份差减1
		if (endMonth < startMonth) {
			years--;
		}
		// 如果月份相同，但结束日期小于开始日期，年份差减1
		else if (endMonth == startMonth && endDay < startDay) {
			years--;
		}

		return years;
	}
}
