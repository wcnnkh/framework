package io.basc.framework.observe.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;

import io.basc.framework.observe.PollingObserver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.collect.CollectionUtils;

public class WatchKeyPollingObserver<T> extends PollingObserver<WatchEvent<T>> {
	@SuppressWarnings("unchecked")
	public static <T> Elements<WatchEvent<T>> pollEvents(WatchKey watchKey, Class<T> contextType) {
		if (!watchKey.isValid()) {
			return Elements.empty();
		}

		List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
		if (CollectionUtils.isEmpty(watchEvents)) {
			return Elements.empty();
		}

		return Elements.of(watchEvents).filter((e) -> contextType.isInstance(e.context()))
				.map((e) -> (WatchEvent<T>) e);
	}

	private final Class<T> contextType;

	private final WatchKey watchKey;

	public WatchKeyPollingObserver(WatchKey watchKey, Class<T> contextType) {
		Assert.requiredArgument(watchKey != null, "watchKey");
		Assert.requiredArgument(contextType != null, "contextType");
		this.watchKey = watchKey;
		this.contextType = contextType;
	}

	public WatchKey getWatchKey() {
		return watchKey;
	}

	public Elements<WatchEvent<T>> pollEvents() {
		return pollEvents(watchKey, contextType);
	}

	public void reset() {
		watchKey.reset();
	}

	@Override
	public void run() {
		Elements<WatchEvent<T>> events = pollEvents();
		try {
			publishBatchEvent(events);
		} finally {
			reset();
		}
	}
}
