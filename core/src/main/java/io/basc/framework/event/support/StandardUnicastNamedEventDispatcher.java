package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.UnicastNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.Selector;

public class StandardUnicastNamedEventDispatcher<K, T> extends StandardNamedEventDispatcher<K, T>
		implements UnicastNamedEventDispatcher<K, T> {

	public StandardUnicastNamedEventDispatcher() {
		this(Selector.roundRobin());
	}

	public StandardUnicastNamedEventDispatcher(Selector<EventListener<T>> selector) {
		this(selector, null, null);
	}

	public StandardUnicastNamedEventDispatcher(Selector<EventListener<T>> selector, @Nullable Matcher<K> matcher,
			@Nullable Executor executor) {
		super((K) -> new StandardUnicastEventDispatcher<>(selector), matcher, executor);
		Assert.requiredArgument(selector != null, "selector");
	}

}
