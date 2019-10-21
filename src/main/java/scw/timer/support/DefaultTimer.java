package scw.timer.support;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import scw.beans.annotation.Bean;
import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.timer.CrontabConfig;
import scw.timer.Task;
import scw.timer.TaskLockFactory;
import scw.timer.TimerTaskConfig;

/**
 * 默认的Timer实现
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class DefaultTimer implements scw.timer.Timer, Destroy {
	private final ConcurrentHashMap<String, CrontabInfo> crontabMap = new ConcurrentHashMap<String, CrontabInfo>();
	private final TaskLockFactory taskLockFactory;
	private final java.util.Timer timer;
	private final Executor executor;

	public DefaultTimer(TaskLockFactory taskLockFactory, Executor executor) {
		this.taskLockFactory = taskLockFactory;
		this.timer = createTimer();
		this.executor = executor;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timer.scheduleAtFixedRate(new CrontabTimerTask(), new Date(calendar.getTimeInMillis()), XTime.ONE_MINUTE);
	}

	protected Timer createTimer() {
		return new Timer(getClass().getName());
	}

	public TaskLockFactory getTimerLockFactory() {
		return taskLockFactory;
	}

	public void schedule(TimerTaskConfig config) {
		DefaultTimerTask defaultTimerTask = new DefaultTimerTask(taskLockFactory, config);
		java.util.TimerTask timerTask = new DefaultTimerTaskWrapper(defaultTimerTask);
		if (config.getPeriod() < 0) {
			timer.schedule(timerTask, config.getTimeUnit().toMillis(config.getDelay()));
		} else {
			timer.schedule(timerTask, config.getTimeUnit().toMillis(config.getDelay()),
					config.getTimeUnit().toMillis(config.getPeriod()));
		}
	}

	public void scheduleAtFixedRate(TimerTaskConfig config) {
		DefaultTimerTask defaultTimerTask = new DefaultTimerTask(taskLockFactory, config);
		java.util.TimerTask timerTask = new DefaultTimerTaskWrapper(defaultTimerTask);
		timer.scheduleAtFixedRate(timerTask, config.getTimeUnit().toMillis(config.getDelay()),
				config.getTimeUnit().toMillis(config.getPeriod()));
	}

	public void destroy() {
		timer.cancel();
	}

	private final class CrontabTimerTask extends TimerTask {

		@Override
		public void run() {
			executor.execute(new CrontabRun(scheduledExecutionTime()));
		}
	}

	private final class DefaultTimerTaskWrapper extends TimerTask {
		private final scw.timer.Task task;

		public DefaultTimerTaskWrapper(scw.timer.Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			executor.execute(new TaskInvoker(scheduledExecutionTime(), task));
		}
	}

	private final class TaskInvoker implements Runnable {
		private long cts;
		private Task task;

		public TaskInvoker(long cts, Task task) {
			this.cts = cts;
			this.task = task;
		}

		public void run() {
			try {
				task.run(cts);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private final class CrontabRun implements Runnable {
		private final long cts;

		public CrontabRun(long cts) {
			this.cts = cts;
		}

		public void run() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(cts);

			for (Entry<String, CrontabInfo> entry : crontabMap.entrySet()) {
				if (entry.getValue().checkTime(calendar)) {
					executor.execute(new TaskInvoker(cts, entry.getValue().getTask()));
				}
			}
		}
	}

	private final class CrontabInfo {
		private final String[] dayOfWeek;
		private final String[] month;
		private final String[] dayOfMonth;
		private final String[] hour;
		private final String[] minute;
		private final Task task;

		public CrontabInfo(CrontabConfig crontabConfig) {
			this.dayOfWeek = StringUtils.commonSplit(crontabConfig.getDayOfWeek());
			this.month = StringUtils.commonSplit(crontabConfig.getMonth());
			this.dayOfMonth = StringUtils.commonSplit(crontabConfig.getDayOfMonth());
			this.hour = StringUtils.commonSplit(crontabConfig.getHour());
			this.minute = StringUtils.commonSplit(crontabConfig.getMinute());
			this.task = crontabConfig.getTask();
		}

		public Task getTask() {
			return task;
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

	public void crontab(CrontabConfig config) {
		crontabMap.putIfAbsent(config.getTaskId(), new CrontabInfo(config));
	}
}
