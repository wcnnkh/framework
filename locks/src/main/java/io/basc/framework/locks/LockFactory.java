package io.basc.framework.locks;

import java.util.concurrent.locks.Lock;

@FunctionalInterface
public interface LockFactory {
	Lock getLock(String name) throws UnableToAcquireLockException;
}
