package io.basc.framework.event.support;

import io.basc.framework.util.concurrent.DelayableExecutor;

/**
 * 广播
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class DefaultBroadcastDelayableEventDispatcher<T> extends DefaultDelayableEventDispatcher<T> {

	public DefaultBroadcastDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		super(null, delayableExecutor);
	}

}
