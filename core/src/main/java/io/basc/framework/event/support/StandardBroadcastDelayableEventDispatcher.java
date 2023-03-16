package io.basc.framework.event.support;

import io.basc.framework.event.BroadcastDelayableEventDispatcher;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardBroadcastDelayableEventDispatcher<T> extends StandardDelayableEventDispatcher<T>
		implements BroadcastDelayableEventDispatcher<T> {

	public StandardBroadcastDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		super(null, delayableExecutor);
	}

}
