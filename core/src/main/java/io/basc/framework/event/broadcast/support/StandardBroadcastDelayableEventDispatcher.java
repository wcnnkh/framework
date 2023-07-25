package io.basc.framework.event.broadcast.support;

import io.basc.framework.event.broadcast.BroadcastDelayableEventDispatcher;
import io.basc.framework.event.support.StandardDelayableEventDispatcher;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardBroadcastDelayableEventDispatcher<T> extends StandardDelayableEventDispatcher<T>
		implements BroadcastDelayableEventDispatcher<T> {

	public StandardBroadcastDelayableEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public StandardBroadcastDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		super(null, delayableExecutor);
	}

}
