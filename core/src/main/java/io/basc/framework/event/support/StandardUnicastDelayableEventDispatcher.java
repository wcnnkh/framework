package io.basc.framework.event.support;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.UnicastDelayableEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Selector;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardUnicastDelayableEventDispatcher<T> extends StandardDelayableEventDispatcher<T>
		implements UnicastDelayableEventDispatcher<T> {

	public StandardUnicastDelayableEventDispatcher(Selector<EventListener<T>> selector,
			DelayableExecutor delayableExecutor) {
		super(selector, delayableExecutor);
		Assert.requiredArgument(selector != null, "selector");
	}

}
