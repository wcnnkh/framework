package scw.timer.support;

import java.util.concurrent.locks.Lock;

import scw.locks.NoOpLock;
import scw.timer.TaskConfig;
import scw.timer.TaskLockFactory;

public final class EmptyTaskLockFactory implements TaskLockFactory {

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return NoOpLock.NO;
	}
}
