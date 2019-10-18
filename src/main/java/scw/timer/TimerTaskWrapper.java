package scw.timer;

import scw.locks.Lock;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.timer.lock.TimerLockFactory;

public class TimerTaskWrapper implements TimerTask {
	private static Logger logger = LoggerUtils.getLogger(TimerTask.class);
	private TimerLockFactory timerLockFactory;
	private TimerTaskListener timerTaskListener;
	private TimerTaskContext timerTaskContext;

	public TimerTaskWrapper(TimerLockFactory timerLockFactory, TimerTaskContext timerTaskContext,
			TimerTaskListener timerTaskListener) {
		this.timerLockFactory = timerLockFactory;
		this.timerTaskContext = timerTaskContext;
		this.timerTaskListener = timerTaskListener;
	}

	public void run(long executionTime) {
		Lock lock = timerLockFactory.getLock(timerTaskContext.getId(), executionTime);
		try {
			if (lock.lock()) {
				try {
					logger.debug("开始执行 [{}]", timerTaskContext.getId());
					timerTaskListener.begin(timerTaskContext, executionTime);
					timerTaskContext.getTimerTask().run(executionTime);
					logger.debug("执行[{}]成功", timerTaskContext.getId());
				} catch (Throwable e) {
					timerTaskListener.error(timerTaskContext, executionTime, e);
					logger.error(e, "执行[{}]异常", timerTaskContext.getId());
				} finally {
					timerTaskListener.complete(timerTaskContext, executionTime);
				}
			}
		} finally {
			lock.unlock();
		}
	}

}
