package scw.locks;

import scw.beans.annotation.AutoConfig;

@AutoConfig(service=DistributedLockFactory.class)
public interface LockFactory {
	Lock getLock(String name);
	
	Lock getLock(String name, int timeout);
}
