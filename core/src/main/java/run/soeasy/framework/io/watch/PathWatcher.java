package run.soeasy.framework.io.watch;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.Registry;
import run.soeasy.framework.core.exchange.event.ChangeEvent;

@RequiredArgsConstructor
public class PathWatcher<T extends FileVariable> extends Poller
		implements Registry<T>, Lifecycle, ElementsWrapper<T, Elements<T>> {
	@NonNull
	private final Registry<PathPoller<T>> registry;
	@NonNull
	private final Publisher<? super Elements<ChangeEvent<T>>> publisher;
	@NonNull
	private final ThreadFactory threadFactory;
	private WatchService watchService;
	private long timeout = 5;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	@Override
	public Elements<T> getSource() {
		return registry.map((e) -> e.getVariable());
	}

	@Override
	public void run() {
		WatchKey watchKey;
		try {
			watchKey = watchService.poll(timeout, timeUnit);
		} catch (InterruptedException e) {
			return;
		}

		for (PathPoller<T> poller : registry) {
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
	public Registration registers(Elements<? extends T> elements) throws RegistrationException {
		return Registration.registers(elements, (element) -> {
			PathPoller<T> pathPoller = new PathPoller<T>(element, publisher);
			if (!isRunning()) {
				start();
			}
			return registry.register(pathPoller);
		});
	}
}
