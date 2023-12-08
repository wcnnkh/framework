package io.basc.framework.event.support;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.match.Matcher;

/**
 * 广播
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public class DefaultBroadcastDelayableNamedEventDispatcher<K, T> extends DefaultDelayableNamedEventDispatcher<K, T> {

	public DefaultBroadcastDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		this(null, delayableExecutor);
	}

	public DefaultBroadcastDelayableNamedEventDispatcher(@Nullable Matcher<K> matcher,
			DelayableExecutor delayableExecutor) {
		super((name) -> new DefaultBroadcastEventDispatcher<>(), matcher, delayableExecutor);
	}

}
