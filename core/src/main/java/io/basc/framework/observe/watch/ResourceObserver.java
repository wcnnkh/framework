package io.basc.framework.observe.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.io.Resource;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.PayloadChangeEvent;
import io.basc.framework.observe.PollingObserver;
import io.basc.framework.observe.register.ObservableList;
import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.register.Registration;

public class ResourceObserver extends PollingObserver<PayloadChangeEvent<ResourcePollingObserver>> {
	private ObservableList<ResourcePollingObserver> registry = new ObservableList<>();
	private volatile WatchService watchService;

	@Override
	public void await() throws InterruptedException {
		if (watchService != null) {
			watchService.take();
			return;
		}
		super.await();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		if (watchService != null) {
			return watchService.poll(timeout, unit) != null;
		}
		return super.await(timeout, unit);
	}

	private PayloadChangeEvent<ResourcePollingObserver> convertEvent(ChangeEvent changeEvent,
			ResourcePollingObserver observer) {
		return new PayloadChangeEvent<ResourcePollingObserver>(this, changeEvent.getType(), observer);
	}

	private void initWatchService() {
		if (watchService == null) {
			synchronized (this) {
				if (watchService == null) {
					try {
						watchService = newWatchService();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	protected WatchService newWatchService() throws IOException {
		return FileSystems.getDefault().newWatchService();
	}

	public void refresh() {
		if (registry.size() == 0 || getListenerCount() == 0) {
			stop();
			return;
		}

		if (registry.size() > 0 && getListenerCount() > 0) {
			initWatchService();
			start();
			return;
		}
	}

	public PayloadRegistration<ResourcePollingObserver> register(Resource resource) {
		ResourcePollingObserver observer = new ResourcePollingObserver(resource);
		PayloadRegistration<ResourcePollingObserver> registration = registry.register(observer);
		refresh();
		if (watchService != null) {
			if (observer.register(watchService)) {
				WatchKey watchKey = observer.getWatchKey();
				registration = registration.and(() -> watchKey.cancel());
			}
		}
		registration = registration.and(observer.registerBatchListener((events) -> {
			publishBatchEvent(events.map((event) -> convertEvent(event, observer)));
		}));
		return registration.and(() -> refresh());
	}

	@Override
	public Registration registerBatchListener(
			BatchEventListener<PayloadChangeEvent<ResourcePollingObserver>> batchEventListener)
			throws EventRegistrationException {
		Registration registration = super.registerBatchListener(batchEventListener);
		refresh();
		return registration.and(() -> refresh());
	}

	@Override
	public void run() {
		for (ResourcePollingObserver observer : registry.getServices()) {
			observer.run();
			if (watchService != null && observer.getWatchKey() == null) {
				observer.register(watchService);
			}
		}
	}

	public void start() {
		startTimerTask();
		if (watchService != null) {
			startEndlessLoop();
		}
	}

	public void stop() {
		stopTimerTask();
		stopEndlessLoop();
	}
}
