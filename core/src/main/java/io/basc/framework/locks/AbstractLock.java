package io.basc.framework.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public abstract class AbstractLock implements Lock {
	private static Logger logger = LoggerFactory.getLogger(AbstractLock.class);

	@Override
	public void lock() {
		while (true) {
			try {
				lockInterruptibly();
				break;
			} catch (InterruptedException e) {
				// ignore 一直等
				logger.trace(e, "ignore this error");
			}
		}
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("NoOpLock can't provide a condition");
	}

}
