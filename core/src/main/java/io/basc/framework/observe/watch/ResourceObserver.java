package io.basc.framework.observe.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import io.basc.framework.io.Resource;
import io.basc.framework.observe.PollingObserver;
import io.basc.framework.observe.container.ObservableList;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.batch.BatchEventDispatcher;
import io.basc.framework.util.event.batch.BatchEventListener;
import lombok.NonNull;

public class ResourceObserver extends PollingObserver<ChangeEvent<Resource>> {
	private final ObservableList<ResourcePollingObserver> resourceList = new ObservableList<>();
	private volatile WatchService watchService;

	public ResourceObserver(@NonNull BatchEventDispatcher<ChangeEvent<Resource>> eventDispatcher) {
		super(eventDispatcher);
	}

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
		if (resourceList.size() == 0 || size() == 0) {
			stop();
			return;
		}

		if (resourceList.size() > 0 && size() > 0) {
			initWatchService();
			start();
			return;
		}
	}

	public PayloadRegistration<ResourcePollingObserver> register(Resource resource) {
		ResourcePollingObserver observer = new ResourcePollingObserver(getEventDispatcher(), resource);
		PayloadRegistration<ResourcePollingObserver> registration = resourceList.register(observer);
		refresh();
		if (watchService != null) {
			if (observer.register(watchService)) {
				WatchKey watchKey = observer.getWatchKey();
				registration = registration.and(() -> watchKey.cancel());
			}
		}
		return registration.and(() -> refresh());
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent<Resource>> batchEventListener)
			throws EventRegistrationException {
		Registration registration = super.registerBatchListener(batchEventListener);
		refresh();
		return registration.and(() -> refresh());
	}

	@Override
	public void run() {
		for (ResourcePollingObserver observer : resourceList.getServices()) {
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
