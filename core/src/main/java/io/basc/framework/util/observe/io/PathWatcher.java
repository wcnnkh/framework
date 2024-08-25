package io.basc.framework.util.observe.io;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.Poller;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathWatcher<T extends FileVariable> extends Poller implements Registry<T, Registration>, Lifecycle {
	@NonNull
	private final Registry<PathPoller<T>, ? extends Registration> registry;
	@NonNull
	private final EventPublishService<ChangeEvent<T>> eventPublishService;
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

		for (PathPoller<T> poller : registry.getServices()) {
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
	public Elements<T> getServices() {
		return registry.getServices().map((e) -> e.getVariable());
	}

	@Override
	public void reload() {
		registry.reload();
	}

	@Override
	public Registration register(T element) throws RegistrationException {
		PathPoller<T> pathPoller = new PathPoller<T>(element, eventPublishService);
		if (!isRunning()) {
			start();
		}
		return registry.register(pathPoller);
	}
}
