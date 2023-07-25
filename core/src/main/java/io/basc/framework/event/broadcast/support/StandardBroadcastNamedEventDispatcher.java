package io.basc.framework.event.broadcast.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.broadcast.BroadcastNamedEventDispatcher;
import io.basc.framework.event.support.StandardNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.match.Matcher;

public class StandardBroadcastNamedEventDispatcher<K, T> extends StandardNamedEventDispatcher<K, T>
		implements BroadcastNamedEventDispatcher<K, T> {

	public StandardBroadcastNamedEventDispatcher() {
		this(null, null);
	}

	public StandardBroadcastNamedEventDispatcher(@Nullable Matcher<K> matcher, @Nullable Executor executor) {
		super((k) -> new StandardBroadcastEventDispatcher<>(), matcher, executor);
	}

}
