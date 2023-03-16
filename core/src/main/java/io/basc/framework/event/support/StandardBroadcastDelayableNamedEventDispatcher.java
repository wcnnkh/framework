package io.basc.framework.event.support;

import io.basc.framework.event.BroadcastDelayableNamedEventDispatcher;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardBroadcastDelayableNamedEventDispatcher<K, T> extends StandardDelayableNamedEventDispatcher<K, T>
		implements BroadcastDelayableNamedEventDispatcher<K, T> {

	public StandardBroadcastDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		this(null, delayableExecutor);
	}

	public StandardBroadcastDelayableNamedEventDispatcher(Matcher<K> matcher, DelayableExecutor delayableExecutor) {
		super((k) -> new StandardBroadcastEventDispatcher<>(), matcher, delayableExecutor);
	}

}
