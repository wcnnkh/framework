package io.basc.framework.event.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.function.Function;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.Registration;

public class StandardNamedEventDispatcher<K, T> implements NamedEventDispatcher<K, T> {
	private volatile Map<K, EventDispatcher<T>> dispatcherMap;
	@Nullable
	private final Matcher<K> matcher;
	private final Function<? super K, ? extends EventDispatcher<T>> creator;
	private final Executor executor;

	public StandardNamedEventDispatcher(Function<? super K, ? extends EventDispatcher<T>> creator,
			@Nullable Matcher<K> matcher, @Nullable Executor executor) {
		Assert.requiredArgument(creator != null, "creator");
		this.creator = creator;
		this.matcher = matcher;
		this.executor = executor;
	}

	public Matcher<K> getMatcher() {
		return matcher;
	}

	public Registration registerListener(K name, EventListener<T> eventListener) {
		if (dispatcherMap == null) {
			synchronized (this) {
				if (dispatcherMap == null) {
					dispatcherMap = matcher == null ? new HashMap<>(8) : new TreeMap<>(matcher);
				}
			}
		}

		synchronized (this) {
			EventDispatcher<T> eventDispatcher = dispatcherMap.get(name);
			if (eventDispatcher == null) {
				eventDispatcher = creator.apply(name);
				dispatcherMap.put(name, eventDispatcher);
			}
			return eventDispatcher.registerListener(eventListener);
		}
	}

	@Override
	public void publishEvent(K name, T event) {
		if (executor == null) {
			syncPublishEvent(name, event);
		} else {
			executor.execute(() -> syncPublishEvent(name, event));
		}
	}

	public final <X extends Throwable> void consume(K name, ConsumeProcessor<EventDispatcher<T>, ? extends X> consumer)
			throws X {
		if (dispatcherMap != null) {
			synchronized (this) {
				if (dispatcherMap != null) {
					if (matcher == null) {
						EventDispatcher<T> dispatcher = dispatcherMap.get(name);
						if (dispatcher == null) {
							return;
						}

						consumer.process(dispatcher);
					} else {
						for (Entry<K, EventDispatcher<T>> entry : dispatcherMap.entrySet()) {
							if (matcher.match(entry.getKey(), name) || matcher.match(name, entry.getKey())) {
								consumer.process(entry.getValue());
							}
						}
					}
				}
			}
		}
	}

	public void syncPublishEvent(K name, T event) {
		consume(name, (e) -> e.publishEvent(event));
	}
}
