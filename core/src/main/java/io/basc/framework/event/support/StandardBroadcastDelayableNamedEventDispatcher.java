package io.basc.framework.event.support;

import io.basc.framework.event.BroadcastDelayableNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardBroadcastDelayableNamedEventDispatcher<K, T> extends StandardDelayableNamedEventDispatcher<K, T>
		implements BroadcastDelayableNamedEventDispatcher<K, T> {

	public StandardBroadcastDelayableNamedEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public StandardBroadcastDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		this(null, delayableExecutor);
	}

	public StandardBroadcastDelayableNamedEventDispatcher(@Nullable Matcher<K> matcher,
			DelayableExecutor delayableExecutor) {
		super((k) -> new StandardBroadcastEventDispatcher<>(), matcher, delayableExecutor);
	}

}
