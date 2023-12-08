package io.basc.framework.event.support;

import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.select.Selector;

/**
 * 单播
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class DefaultUnicastDelayableEventDispatcher<T> extends DefaultDelayableEventDispatcher<T> {

	public DefaultUnicastDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		this(Selector.roundRobin(), delayableExecutor);
	}

	public DefaultUnicastDelayableEventDispatcher(Selector<BatchEventListener<T>> eventListenerSelector,
			DelayableExecutor delayableExecutor) {
		super(Assert.requiredArgument(eventListenerSelector != null, "Unicast event selector cannot be empty",
				eventListenerSelector), delayableExecutor);
	}

}
