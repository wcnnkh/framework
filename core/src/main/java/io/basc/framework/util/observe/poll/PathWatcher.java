package io.basc.framework.util.observe.poll;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.RegistrationException;
import io.basc.framework.util.observe.Registry;
import io.basc.framework.util.observe.event.ChangeEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathWatcher<T extends FileVariable> extends Pollable implements Registry<T>, Lifecycle {
	@NonNull
	private final Registry<PathPoller<T>> registry;
	@NonNull
	private final Publisher<? super Elements<ChangeEvent<T>>> changeEventProducer;
	@NonNull
	private final ThreadFactory threadFactory;
	private WatchService watchService;
	private long timeout = 5;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	@Override
	public void run() {
		WatchKey watchKey;
		try {
			watchKey = watchService.poll(timeout, timeUnit);
		} catch (InterruptedException e) {
			return;
		}

		for (PathPoller<T> poller : registry.getElements()) {
			try {
				poller.run(Elements.singleton(watchKey));
			} finally {
				poller.run();
			}
		}
	}

	public void start() {
		startEndlessLoop(0, TimeUnit.SECONDS, threadFactory);
	}

	@Override
	public Elements<T> getElements() {
		return registry.getElements().map((e) -> e.getVariable());
	}

	@Override
	public Registration register(T element) throws RegistrationException {
		PathPoller<T> pathPoller = new PathPoller<T>(element, changeEventProducer);
		if (!isRunning()) {
			start();
		}
		return registry.register(pathPoller);
	}
}
