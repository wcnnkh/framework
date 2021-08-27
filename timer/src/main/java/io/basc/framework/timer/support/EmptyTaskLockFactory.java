package io.basc.framework.timer.support;

import io.basc.framework.locks.NoOpLock;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

import java.util.concurrent.locks.Lock;

public final class EmptyTaskLockFactory implements TaskLockFactory {

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return NoOpLock.NO;
	}
}
