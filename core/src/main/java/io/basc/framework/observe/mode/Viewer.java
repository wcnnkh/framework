package io.basc.framework.observe.mode;

import java.util.concurrent.TimeUnit;

/**
 * 被观察者的抽象
 * 
 * @author shuchaowen
 *
 */
public interface Viewer extends Runnable {
	void await() throws InterruptedException;

	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
}
