package io.basc.framework.event.support;

import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
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
public class DefaultUnicastNamedEventDispatcher<K, T> extends DefaultNamedEventDispatcher<K, T> {

	public DefaultUnicastNamedEventDispatcher() {
		this(null);
	}

	public DefaultUnicastNamedEventDispatcher(@Nullable Matcher<K> matcher) {
		this(Selector.roundRobin(), matcher);
	}

	public DefaultUnicastNamedEventDispatcher(Selector<BatchEventListener<T>> eventListenerSelector,
			@Nullable Matcher<K> matcher) {
		super(Assert.requiredArgument(eventListenerSelector != null, "Unicast event selector cannot be empty",
				(name) -> new DefaultUnicastEventDispatcher<>(eventListenerSelector)), matcher);
	}

}
