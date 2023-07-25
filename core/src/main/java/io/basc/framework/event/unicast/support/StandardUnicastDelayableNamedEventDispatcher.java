package io.basc.framework.event.unicast.support;

import io.basc.framework.event.support.StandardDelayableNamedEventDispatcher;
import io.basc.framework.event.unicast.UnicastDelayableNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.match.Matcher;

public class StandardUnicastDelayableNamedEventDispatcher<K, T> extends StandardDelayableNamedEventDispatcher<K, T>
		implements UnicastDelayableNamedEventDispatcher<K, T> {

	public StandardUnicastDelayableNamedEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public StandardUnicastDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		this(null, delayableExecutor);
	}

	public StandardUnicastDelayableNamedEventDispatcher(@Nullable Matcher<K> matcher,
			DelayableExecutor delayableExecutor) {
		super((k) -> new StandardUnicastEventDispatcher<>(), matcher, delayableExecutor);
	}
}
