package io.basc.framework.event.unicast.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.support.StandardEventDispatcher;
import io.basc.framework.event.unicast.UnicastEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.select.Selector;

public class StandardUnicastEventDispatcher<T> extends StandardEventDispatcher<T> implements UnicastEventDispatcher<T> {

	public StandardUnicastEventDispatcher() {
		this(Selector.roundRobin(), null);
	}

	public StandardUnicastEventDispatcher(Selector<EventListener<T>> selector) {
		this(selector, null);
	}

	public StandardUnicastEventDispatcher(Selector<EventListener<T>> selector, @Nullable Executor executor) {
		super(selector, executor);
		Assert.requiredArgument(selector != null, "selector");
	}
}
