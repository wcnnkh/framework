package scw.timer.lock;

import scw.locks.Lock;

public interface TimerLockFactory {
	Lock getLock(String taskId, long executionTime);
}
