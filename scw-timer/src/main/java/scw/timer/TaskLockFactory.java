package scw.timer;

import scw.locks.Lock;

public interface TaskLockFactory {
	Lock getLock(TaskConfig taskConfig, long executionTime);
}
