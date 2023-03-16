package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.BroadcastNamedEventDispatcher;
import io.basc.framework.util.Matcher;

public class StandardBroadcastNamedEventDispatcher<K, T> extends StandardNamedEventDispatcher<K, T>
		implements BroadcastNamedEventDispatcher<K, T> {

	public StandardBroadcastNamedEventDispatcher() {
		this(null, null);
	}

	public StandardBroadcastNamedEventDispatcher(Matcher<K> matcher) {
		this(matcher, null);
	}

	public StandardBroadcastNamedEventDispatcher(Executor executor) {
		this(null, executor);
	}

	public StandardBroadcastNamedEventDispatcher(Matcher<K> matcher, Executor executor) {
		super((k) -> new StandardBroadcastEventDispatcher<>(), matcher, executor);
	}

}
