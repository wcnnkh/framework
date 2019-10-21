package scw.timer.lock;

import scw.locks.EmptyLock;
import scw.locks.Lock;
import scw.timer.TaskLockFactory;

public final class EmptyTaskLockFactory implements TaskLockFactory {

	public Lock getLock(String taskId, long executionTime) {
		return new EmptyLock(true);
	}

}
