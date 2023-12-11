package io.basc.framework.observe.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import io.basc.framework.observe.PollingObserver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class WatchKeysPollingObserver<T> extends PollingObserver<WatchEvent<T>> {
	private final Class<T> contextType;
	private final Elements<? extends WatchKey> watchKeys;

	public WatchKeysPollingObserver(Elements<? extends WatchKey> watchKeys, Class<T> contextType) {
		Assert.requiredArgument(watchKeys != null, "watchKeys");
		Assert.requiredArgument(contextType != null, "contextType");
		this.watchKeys = watchKeys;
		this.contextType = contextType;
	}

	public Elements<? extends WatchKey> getWatchKeys() {
		return watchKeys;
	}

	@Override
	public void run() {
		Elements<WatchEvent<T>> events = watchKeys.flatMap((e) -> {
			WatchKeyPollingObserver<T> watchKeyObserver = new WatchKeyPollingObserver<>(e, contextType);
			try {
				return watchKeyObserver.pollEvents();
			} finally {
				watchKeyObserver.reset();
			}
		});
		publishBatchEvent(events);
	}
}
