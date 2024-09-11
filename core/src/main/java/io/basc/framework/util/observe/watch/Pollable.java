package io.basc.framework.util.observe.watch;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class Pollable implements Runnable {

	private static volatile Timer defaultTimer;

	private static Timer getDefaultTimer() {
		if (defaultTimer == null) {
			synchronized (Pollable.class) {
				if (defaultTimer == null) {
					defaultTimer = new Timer(Pollable.class.getName(), true);
					Runtime.getRuntime().addShutdownHook(new Thread(() -> defaultTimer.cancel()));
				}
			}
		}
		return defaultTimer;
	}

	private volatile Thread endlessLoopThread;

	private volatile ScheduledFuture<?> scheduledFuture;

	private volatile TimerTask timerTask;

	public boolean isRunning() {
		synchronized (this) {
			return timerTask != null || scheduledFuture != null || endlessLoopThread != null;
		}
	}

	public void startEndlessLoop(long period, TimeUnit timeUnit) {
		startEndlessLoop(period, timeUnit, Executors.defaultThreadFactory());
	}

	/**
	 * 使用线程循环执行实现
	 * 
	 * @param period
	 * @param timeUnit
	 * @param threadFactory
	 * @return
	 */
	public void startEndlessLoop(long period, TimeUnit timeUnit, ThreadFactory threadFactory) {
		if (isRunning()) {
			return;
		}

		synchronized (this) {
			if (endlessLoopThread == null) {
				endlessLoopThread = threadFactory.newThread(() -> {
					while (!endlessLoopThread.isInterrupted()) {
						try {
							timeUnit.sleep(period);
						} catch (InterruptedException e) {
							// 线程中断结束循环
							break;
						}
						Pollable.this.run();
					}
				});
				// 使用守护线程,期望自动退出
				endlessLoopThread.setDaemon(true);
				endlessLoopThread.start();
			}
		}
	}

	/**
	 * 使用scheduledExecutorService
	 * 
	 * @param period
	 * @param timeUnit
	 * @param scheduledExecutorService
	 * @return
	 */
	public void startScheduled(long period, TimeUnit timeUnit, ScheduledExecutorService scheduledExecutorService) {
		if (isRunning()) {
			return;
		}

		synchronized (this) {
			if (scheduledFuture == null) {
				scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this, period, period, timeUnit);
			}
		}
	}

	public void startTimerTask(long period, TimeUnit timeUnit) {
		startTimerTask(period, timeUnit, getDefaultTimer());
	}

	/**
	 * 使用Timer
	 * 
	 * @param period
	 * @param timeUnit
	 * @param timer
	 * @return
	 */
	public void startTimerTask(long period, TimeUnit timeUnit, Timer timer) {
		if (isRunning()) {
			return;
		}

		synchronized (this) {
			if (timerTask == null) {
				timerTask = new TimerTask() {

					@Override
					public void run() {
						Pollable.this.run();
					}
				};
				long time = timeUnit.toMillis(period);
				timer.schedule(timerTask, time, time);
			}
		}
	}

	public void stop() {
		if (!isRunning()) {
			return;
		}

		synchronized (this) {
			if (endlessLoopThread != null) {
				endlessLoopThread.interrupt();
				endlessLoopThread = null;
			}

			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
				scheduledFuture = null;
			}

			if (timerTask != null) {
				timerTask.cancel();
				timerTask = null;
			}
		}
	}
}
