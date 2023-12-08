package io.basc.framework.event.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.function.Function;

import io.basc.framework.event.batch.BatchEventDispatcher;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.batch.NamedBatchEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.match.Matcher;

public class DefaultNamedEventDispatcher<K, T> implements NamedBatchEventDispatcher<K, T> {
	private volatile Map<K, BatchEventDispatcher<T>> dispatcherMap;
	@Nullable
	private final Matcher<K> matcher;
	private final Function<? super K, ? extends BatchEventDispatcher<T>> creator;
	@Nullable
	private Executor publishEventExecutor;

	public DefaultNamedEventDispatcher(Function<? super K, ? extends BatchEventDispatcher<T>> creator,
			@Nullable Matcher<K> matcher) {
		Assert.requiredArgument(creator != null, "creator");
		this.creator = creator;
		this.matcher = matcher;
	}

	public Matcher<K> getMatcher() {
		return matcher;
	}

	public Registration registerBatchListener(K name, BatchEventListener<T> eventListener) {
		if (dispatcherMap == null) {
			synchronized (this) {
				if (dispatcherMap == null) {
					dispatcherMap = matcher == null ? new HashMap<>(8) : new TreeMap<>(matcher);
				}
			}
		}

		synchronized (this) {
			BatchEventDispatcher<T> eventDispatcher = dispatcherMap.get(name);
			if (eventDispatcher == null) {
				eventDispatcher = creator.apply(name);
				dispatcherMap.put(name, eventDispatcher);
			}
			return eventDispatcher.registerBatchListener(eventListener);
		}
	}

	@Override
	public void publishBatchEvent(K name, Elements<T> events) {
		if (publishEventExecutor == null) {
			syncPublishBatchEvent(name, events);
		} else {
			publishEventExecutor.execute(() -> syncPublishBatchEvent(name, events));
		}
	}

	public final <X extends Throwable> void consume(K name,
			ConsumeProcessor<BatchEventDispatcher<T>, ? extends X> consumer) throws X {
		if (dispatcherMap != null) {
			synchronized (this) {
				if (dispatcherMap != null) {
					if (matcher == null) {
						BatchEventDispatcher<T> dispatcher = dispatcherMap.get(name);
						if (dispatcher == null) {
							return;
						}

						consumer.process(dispatcher);
					} else {
						for (Entry<K, BatchEventDispatcher<T>> entry : dispatcherMap.entrySet()) {
							if (matcher.match(entry.getKey(), name) || matcher.match(name, entry.getKey())) {
								consumer.process(entry.getValue());
							}
						}
					}
				}
			}
		}
	}

	public void syncPublishBatchEvent(K name, Elements<T> events) {
		consume(name, (e) -> e.publishBatchEvent(events));
	}
}
