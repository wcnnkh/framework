package io.basc.framework.event.support;

import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.match.Matcher;
import io.basc.framework.util.select.Selector;

/**
 * 单播
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public class DefaultUnicastDelayableNamedEventDispatcher<K, T> extends DefaultDelayableNamedEventDispatcher<K, T> {

	public DefaultUnicastDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		this(null, delayableExecutor);
	}

	public DefaultUnicastDelayableNamedEventDispatcher(@Nullable Matcher<K> matcher,
			DelayableExecutor delayableExecutor) {
		this(Selector.random(), matcher, delayableExecutor);
	}

	public DefaultUnicastDelayableNamedEventDispatcher(Selector<BatchEventListener<T>> eventListenerSelector,
			@Nullable Matcher<K> matcher, DelayableExecutor delayableExecutor) {
		super(Assert.requiredArgument(eventListenerSelector != null, "Unicast event selector cannot be empty",
				(name) -> new DefaultUnicastEventDispatcher<>(eventListenerSelector)), matcher, delayableExecutor);
	}

}
