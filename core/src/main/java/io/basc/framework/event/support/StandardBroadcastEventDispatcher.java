package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.BroadcastEventDispatcher;

public class StandardBroadcastEventDispatcher<T> extends StandardEventDispatcher<T>
		implements BroadcastEventDispatcher<T> {

	public StandardBroadcastEventDispatcher() {
		this(null);
	}

	public StandardBroadcastEventDispatcher(Executor executor) {
		super(null, executor);
	}

}
