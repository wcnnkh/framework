package io.basc.framework.event.unicast.support;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.support.StandardDelayableEventDispatcher;
import io.basc.framework.event.unicast.UnicastDelayableEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.select.Selector;

public class StandardUnicastDelayableEventDispatcher<T> extends StandardDelayableEventDispatcher<T>
		implements UnicastDelayableEventDispatcher<T> {

	public StandardUnicastDelayableEventDispatcher() {
		this(Selector.roundRobin(), new DefaultDelayableExecutor());
	}

	public StandardUnicastDelayableEventDispatcher(Selector<EventListener<T>> selector,
			DelayableExecutor delayableExecutor) {
		super(selector, delayableExecutor);
		Assert.requiredArgument(selector != null, "selector");
	}
}
