package scw.common;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scw.beans.annotaion.Destroy;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;

/**
 * 计划任务
 * 
 * @author shuchaowen
 *
 */
public class Crontab {
	private Timer timer = new Timer();
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();
	private LinkedList<CrontabInfo> crontabInfos = new LinkedList<CrontabInfo>();

	public Crontab() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timer.schedule(new CrontabTimerTask(),
				new Date(calendar.getTimeInMillis()), XTime.ONE_MINUTE);
	}

	/**
	 * 按指定计划执行任务
	 * 
	 * @param dayOfWeek
	 * @param month
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param name
	 * @param task
	 */
	public synchronized void crontab(String dayOfWeek, String month,
			String dayOfMonth, String hour, String minute, String name,
			Runnable task) {
		crontabInfos.add(new CrontabInfo(dayOfWeek, month, dayOfMonth, hour,
				minute, task, name));
	}

	/**
	 * 按星期几来执行任务
	 * 
	 * @param dayOfWeek
	 *            星期几
	 * @param hour
	 * @param minute
	 * @param name
	 * @param task
	 */
	public void crontabDayOfWeek(String dayOfWeek, String hour, String minute,
			String name, Runnable task) {
		crontab(dayOfWeek, "*", "*", hour, minute, name, task);
	}

	/**
	 * 按天执行计划
	 * 
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param name
	 * @param task
	 */
	public void crontabDayOfMonth(String dayOfMonth, String hour,
			String minute, String name, Runnable task) {
		crontab("*", "*", dayOfMonth, hour, minute, name, task);
	}

	/**
	 * 每天凌晨一点执行
	 * @param name
	 * @param task
	 */
	public void crontabDailyAtOneAM(String name, Runnable task) {
		crontabDayOfMonth("*", "1", "0", name, task);
	}

	@Destroy
	public void shutdown() {
		timer.cancel();
		executorService.shutdownNow();
	}

	final class CrontabTimerTask extends TimerTask {

		@Override
		public void run() {
			executorService.submit(new CrontabRun(scheduledExecutionTime()));
		}
	}

	final class CrontabRun implements Runnable {
		private final long cts;

		public CrontabRun(long cts) {
			this.cts = cts;
		}

		public void run() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(cts);

			for (CrontabInfo crontab : crontabInfos) {
				if (crontab.checkTime(calendar)) {
					Logger.info("正在执行任务[" + crontab.getName() + "]");
					try {
						crontab.getRunnable().run();
					} catch (Exception e) {
						Logger.error("执行任务[" + crontab.getName() + "]异常", e);
					}
				}
			}
		}
	}
}

class CrontabInfo {
	private final String dayOfWeek;
	private final String month;
	private final String dayOfMonth;
	private final String hour;
	private final String minute;
	private final Runnable runnable;
	private final String name;

	public CrontabInfo(String dayOfWeek, String month, String dayOfMonth,
			String hour, String minute, Runnable runnable, String name) {
		this.dayOfWeek = dayOfWeek;
		this.month = month;
		this.dayOfMonth = dayOfMonth;
		this.hour = hour;
		this.minute = minute;
		this.runnable = runnable;
		this.name = name;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public String getMonth() {
		return month;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public String getHour() {
		return hour;
	}

	public String getMinute() {
		return minute;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public String getName() {
		return name;
	}

	private boolean check(int value, String checkValue) {
		if (StringUtils.isEmpty(checkValue) || "*".equals(checkValue)) {
			return true;
		}

		if (StringUtils.isNumeric(checkValue)) {
			return value == Integer.parseInt(checkValue);
		} else {
			String v = value + "";
			for (int i = 0; i < v.length(); i++) {
				char a = v.charAt(i);
				char b = checkValue.charAt(i);
				if (b == '*') {
					continue;
				}

				if (a != b) {
					return false;
				}
			}
			return true;
		}
	}

	private boolean checkBySplit(int value, String check) {
		if (StringUtils.isEmpty(check)) {
			return true;
		}

		String[] vs = StringUtils.commonSplit(check);
		for (String v : vs) {
			if (check(value, v)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkTime(Calendar calendar) {
		return checkBySplit(calendar.get(Calendar.DAY_OF_WEEK), dayOfWeek)
				|| checkBySplit(calendar.get(Calendar.MONTH), month)
				|| checkBySplit(calendar.get(Calendar.DAY_OF_MONTH), dayOfMonth)
				|| checkBySplit(Calendar.HOUR_OF_DAY, hour)
				|| checkBySplit(calendar.get(Calendar.MINUTE), minute);
	}

}
