package scw.timer.lock;

import scw.data.cas.CASOperations;
import scw.locks.Lock;

public class CASTimerLockFactory implements TimerLockFactory {
	private CASOperations casOperations;

	public CASTimerLockFactory(CASOperations casOperations) {
		this.casOperations = casOperations;
	}

	public Lock getLock(String taskId, long executionTime) {
		return new CASTimerLock(casOperations, taskId, executionTime);
	}

}
