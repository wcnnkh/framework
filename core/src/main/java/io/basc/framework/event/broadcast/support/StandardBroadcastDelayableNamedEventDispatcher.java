package io.basc.framework.event.broadcast.support;

import io.basc.framework.event.broadcast.BroadcastDelayableNamedEventDispatcher;
import io.basc.framework.event.support.StandardDelayableNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.match.Matcher;

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
