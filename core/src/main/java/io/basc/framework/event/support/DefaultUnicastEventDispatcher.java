package io.basc.framework.event.support;

import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.util.Assert;
import io.basc.framework.util.select.Selector;

/**
 * 单播
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class DefaultUnicastEventDispatcher<T> extends DefaultEventDispatcher<T> {

	public DefaultUnicastEventDispatcher() {
		this(Selector.roundRobin());
	}

	public DefaultUnicastEventDispatcher(Selector<BatchEventListener<T>> eventListenerSelector) {
		super(Assert.requiredArgument(eventListenerSelector != null, "Unicast event selector cannot be empty",
				eventListenerSelector));
	}

}
