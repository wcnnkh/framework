package scw.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.beans.annotaion.Destroy;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;

/**
 * 计划任务
 * 
 * @author shuchaowen
 *
 */
public final class Crontab {
	private Timer timer = new Timer();
	private ExecutorService executorService = new ThreadPoolExecutor(2, 100, 60L, TimeUnit.MINUTES,
			new LinkedBlockingQueue<Runnable>());
	private CopyOnWriteArrayList<CrontabInfo> crontabInfos = new CopyOnWriteArrayList<CrontabInfo>();

	public Crontab() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timer.scheduleAtFixedRate(new CrontabTimerTask(), new Date(calendar.getTimeInMillis()), XTime.ONE_MINUTE);
	}

	/**
	 * 按指定计划执行任务
	 * 
	 * @param dayOfWeek
	 * @param month
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param task
	 */
	public void crontab(String dayOfWeek, String month, String dayOfMonth, String hour, String minute, Runnable task) {
		crontabInfos.add(new CrontabInfo(dayOfWeek, month, dayOfMonth, hour, minute, task));
	}

	/**
	 * 按星期几来执行任务
	 * 
	 * @param dayOfWeek
	 *            星期几
	 * @param hour
	 * @param minute
	 * @param task
	 */
	public void crontabDayOfWeek(String dayOfWeek, String hour, String minute, Runnable task) {
		crontab(dayOfWeek, "*", "*", hour, minute, task);
	}

	/**
	 * 按天执行计划
	 * 
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param task
	 */
	public void crontabDayOfMonth(String dayOfMonth, String hour, String minute, Runnable task) {
		crontab("*", "*", dayOfMonth, hour, minute, task);
	}

	/**
	 * 每天凌晨一点执行
	 * 
	 * @param name
	 * @param task
	 */
	public void crontabDailyAtOneAM(Runnable task) {
		crontabDayOfMonth("*", "1", "0", task);
	}

	@Destroy
	public void shutdown() {
		timer.cancel();
		executorService.shutdownNow();
	}

	final class CrontabTimerTask extends TimerTask {

		@Override
		public void run() {
			executorService.execute(new CrontabRun(scheduledExecutionTime()));
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
					executorService.execute(crontab.getRunnable());
				}
			}
		}
	}
}

class CrontabInfo {
	private final String[] dayOfWeek;
	private final String[] month;
	private final String[] dayOfMonth;
	private final String[] hour;
	private final String[] minute;
	private final Runnable runnable;

	public CrontabInfo(String dayOfWeek, String month, String dayOfMonth, String hour, String minute,
			Runnable runnable) {
		this.dayOfWeek = StringUtils.commonSplit(dayOfWeek);
		this.month = StringUtils.commonSplit(month);
		this.dayOfMonth = StringUtils.commonSplit(dayOfMonth);
		this.hour = StringUtils.commonSplit(hour);
		this.minute = StringUtils.commonSplit(minute);
		this.runnable = runnable;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	private boolean checkBySplit(int value, String[] check) {
		for (String v : check) {
			if (StringUtils.isEmpty(v)) {
				return true;
			}
			
			if (StringUtils.test(value + "", v)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkTime(Calendar calendar) {
		return checkBySplit(calendar.get(Calendar.DAY_OF_WEEK), dayOfWeek)
				&& checkBySplit(calendar.get(Calendar.MONTH), month)
				&& checkBySplit(calendar.get(Calendar.DAY_OF_MONTH), dayOfMonth)
				&& checkBySplit(calendar.get(Calendar.HOUR_OF_DAY), hour)
				&& checkBySplit(calendar.get(Calendar.MINUTE), minute);
	}

}
