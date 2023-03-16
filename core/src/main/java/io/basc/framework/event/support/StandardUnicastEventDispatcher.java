package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.UnicastEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Selector;

public class StandardUnicastEventDispatcher<T> extends StandardEventDispatcher<T> implements UnicastEventDispatcher<T> {

	public StandardUnicastEventDispatcher(Selector<EventListener<T>> selector) {
		this(selector, null);
	}

	public StandardUnicastEventDispatcher(Selector<EventListener<T>> selector, Executor executor) {
		super(selector, executor);
		Assert.requiredArgument(selector != null, "selector");
	}
}
