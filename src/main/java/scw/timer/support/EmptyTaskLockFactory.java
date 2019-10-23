package scw.timer.support;

import scw.locks.EmptyLock;
import scw.locks.Lock;
import scw.timer.TaskConfig;
import scw.timer.TaskLockFactory;

public final class EmptyTaskLockFactory implements TaskLockFactory {

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new EmptyLock(true);
	}

}
