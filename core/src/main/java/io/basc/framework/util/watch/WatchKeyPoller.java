package io.basc.framework.util.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WatchKeyPoller<T> extends Pollable {
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

	@NonNull
	private final WatchKey watchKey;
	@NonNull
	private final Class<T> contextType;
	@NonNull
	private final Publisher<? super Elements<WatchEvent<T>>> watchEventProducer;

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
			watchEventProducer.publish(events);
		} finally {
			reset();
		}
	}
}
