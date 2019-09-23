package scw.locks;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DistributedLockFactory.class })
public interface LockFactory {
	Lock getLock(String name);

	Lock getLock(String name, int timeout);
}
