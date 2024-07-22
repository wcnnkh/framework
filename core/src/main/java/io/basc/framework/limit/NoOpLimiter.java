package io.basc.framework.limit;

import java.util.concurrent.locks.Lock;

import io.basc.framework.locks.NoOpLock;

/**
 * 无限制
 * 
 * @author shuchaowen
 *
 */
public class NoOpLimiter extends DisposableLimiter {

	@Override
	public Lock getResource() {
		return isLimited() ? NoOpLock.DEAD : NoOpLock.NO;
	}

}
