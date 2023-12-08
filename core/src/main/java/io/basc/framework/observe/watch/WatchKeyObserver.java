package io.basc.framework.observe.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.basc.framework.observe.mode.ObserverMode;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;

public class WatchKeyObserver<W extends WatchKey, T> extends ObserverMode<WatchEvent<T>> {
	private final Elements<? extends W> watchKeys;
	private final Class<T> contextType;

	public WatchKeyObserver(Elements<? extends W> watchKeys, Class<T> contextType) {
		Assert.requiredArgument(watchKeys != null, "watchKeys");
		Assert.requiredArgument(contextType != null, "contextType");
		this.watchKeys = watchKeys;
		this.contextType = contextType;
	}

	@Override
	public void run() {
		watchKeys.forEach(this::pollEvents);
	}

	public void pollEvents(WatchKey watchKey) {
		if (!watchKey.isValid()) {
			return;
		}

		try {
			List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
			if (CollectionUtils.isEmpty(watchEvents)) {
				return;
			}

			@SuppressWarnings("unchecked")
			Elements<WatchEvent<T>> events = Elements.of(watchEvents).filter((e) -> contextType.isInstance(e.context()))
					.map((e) -> (WatchEvent<T>) e);
			publishBatchEvent(events);
		} finally {
			watchKey.reset();
		}
	}

	public Elements<? extends W> getWatchKeys() {
		return watchKeys;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return true;
	}
}
