package io.basc.framework.observe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.basc.framework.event.support.DefaultBroadcastEventDispatcher;
import io.basc.framework.util.Assert;

public class Observer<E> extends DefaultBroadcastEventDispatcher<E> implements PublishService<E> {
	private static volatile Timer defaultTimer;

	private static Timer getDefaultTimer() {
		if (defaultTimer == null) {
			synchronized (Observer.class) {
				if (defaultTimer == null) {
					defaultTimer = new Timer(Observer.class.getName());
					Runtime.getRuntime().addShutdownHook(new Thread(() -> defaultTimer.cancel()));
				}
			}
		}
		return defaultTimer;
	}

	private final AtomicLong atomicLastModified = new AtomicLong();

	private volatile Thread endlessLoopThread;

	private long refreshTimeCycle = 5;
	private TimeUnit refreshTimeUnit = TimeUnit.SECONDS;

	private volatile ScheduledFuture<?> scheduledFuture;

	private volatile TimerTask timerTask;

	public AtomicLong getAtomicLastModified() {
		return atomicLastModified;
	}

	public long getRefreshTimeCycle() {
		return refreshTimeCycle;
	}

	public TimeUnit getRefreshTimeUnit() {
		return refreshTimeUnit;
	}

	public boolean hasEndlessLoop() {
		synchronized (this) {
			return endlessLoopThread != null;
		}
	}

	public boolean hasScheduled() {
		synchronized (this) {
			return scheduledFuture != null;
		}
	}

	public boolean hasTimerTask() {
		synchronized (this) {
			return timerTask != null;
		}
	}

	public boolean isRunning() {
		synchronized (this) {
			return timerTask != null || scheduledFuture != null || endlessLoopThread != null;
		}
	}

	public void setRefreshTimeCycle(long refreshTimeCycle) {
		Assert.isTrue(refreshTimeCycle > 0, "RefreshTimeCycle needs to be greater than 0");
		this.refreshTimeCycle = refreshTimeCycle;
	}

	public void setRefreshTimeUnit(TimeUnit refreshTimeUnit) {
		Assert.requiredArgument(refreshTimeUnit != null, "refreshTimeUnit");
		this.refreshTimeUnit = refreshTimeUnit;
	}

	/**
	 * 使用死循环的方式
	 */
	public boolean startEndlessLoop(Runnable runnable) {
		if (endlessLoopThread == null) {
			synchronized (this) {
				if (endlessLoopThread == null) {
					endlessLoopThread = new Thread(() -> {
						while (!Thread.currentThread().isInterrupted()) {
							runnable.run();
						}
					});
					endlessLoopThread.start();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 使用定时器
	 * 
	 * @param scheduledExecutorService
	 * @param delay
	 * @param timeUnit
	 * @param runnable
	 * @return
	 */
	public boolean startScheduled(ScheduledExecutorService scheduledExecutorService, long delay, TimeUnit timeUnit,
			Runnable runnable) {
		if (scheduledFuture == null) {
			synchronized (this) {
				if (scheduledFuture == null) {
					scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, delay, delay, timeUnit);
					return true;
				}
			}
		}
		return false;
	}

	public boolean startScheduled(ScheduledExecutorService scheduledExecutorService, Runnable runnable) {
		return startScheduled(scheduledExecutorService, getRefreshTimeCycle(), getRefreshTimeUnit(), runnable);
	}

	public boolean startTimerTask(long delay, TimeUnit timeUnit, Runnable runnable) {
		return startTimerTask(getDefaultTimer(), delay, timeUnit, runnable);
	}

	/**
	 * 使用timer实现
	 * 
	 * @param runnable
	 * @return
	 */
	public boolean startTimerTask(Runnable runnable) {
		return startTimerTask(getRefreshTimeCycle(), getRefreshTimeUnit(), runnable);
	}

	/**
	 * 使用timer实现
	 * 
	 * @param timer
	 * @param delay
	 * @param timeUnit
	 * @param runnable
	 * @return
	 */
	public boolean startTimerTask(Timer timer, long delay, TimeUnit timeUnit, Runnable runnable) {
		Assert.requiredArgument(timer != null, "timer");
		Assert.isTrue(delay > 0, "Delay needs to be greater than 0");
		Assert.requiredArgument(timeUnit != null, "timeUnit");
		Assert.requiredArgument(runnable != null, "runnable");
		if (timerTask == null) {
			synchronized (this) {
				if (timerTask == null) {
					timerTask = new TimerTask() {

						@Override
						public void run() {
							runnable.run();
						}
					};
					long time = timeUnit.toMillis(delay);
					timer.schedule(timerTask, time, time);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 停止死循环
	 * 
	 * @return
	 */
	public boolean stopEndlessLoop() {
		if (endlessLoopThread != null) {
			synchronized (this) {
				if (endlessLoopThread != null) {
					endlessLoopThread.interrupt();
					endlessLoopThread = null;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 停止定时器
	 * 
	 * @return
	 */
	public boolean stopScheduled() {
		if (scheduledFuture != null) {
			synchronized (this) {
				if (scheduledFuture != null) {
					scheduledFuture.cancel(false);
					scheduledFuture = null;
					return true;
				}
			}
		}
		return true;
	}

	public boolean stopTimerTask() {
		if (timerTask != null) {
			synchronized (this) {
				if (timerTask != null) {
					timerTask.cancel();
					timerTask = null;
					return true;
				}
			}
		}
		return false;
	}
}
