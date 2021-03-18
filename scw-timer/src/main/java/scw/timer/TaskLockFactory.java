package scw.timer;

import java.util.concurrent.locks.Lock;

public interface TaskLockFactory {
	Lock getLock(TaskConfig taskConfig, long executionTime);
}
