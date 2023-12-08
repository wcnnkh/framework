package io.basc.framework.event.support;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.match.Matcher;

/**
 * 广播
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public class DefaultBroadcastNamedEventDispatcher<K, T> extends DefaultNamedEventDispatcher<K, T> {

	public DefaultBroadcastNamedEventDispatcher() {
		this(null);
	}

	public DefaultBroadcastNamedEventDispatcher(@Nullable Matcher<K> matcher) {
		super((name) -> new DefaultBroadcastEventDispatcher<>(), matcher);
	}

}
