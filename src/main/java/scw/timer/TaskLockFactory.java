package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.locks.JdkLockFactory;
import scw.locks.Lock;
import scw.timer.db.DBTaskLockFactory;
import scw.timer.support.CASTaskLockFactory;

@AutoImpl({ CASTaskLockFactory.class, DBTaskLockFactory.class, JdkLockFactory.class })
public interface TaskLockFactory {
	Lock getLock(TaskConfig taskConfig, long executionTime);
}
