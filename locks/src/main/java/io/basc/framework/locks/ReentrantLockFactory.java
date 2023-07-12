package io.basc.framework.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockFactory extends MemoryLockFactory {

	@Override
	protected Lock createLock(String name) {
		return new ReentrantLock();
	}

}
