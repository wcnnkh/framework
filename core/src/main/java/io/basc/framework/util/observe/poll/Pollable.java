package io.basc.framework.util.observe.poll;

import io.basc.framework.util.concurrent.locks.Lockable;

/**
 * 可轮询的定义
 * 
 * @author wcnnkh
 *
 */
public interface Pollable extends Lockable, Runnable {

	@Override
	void run();
}
