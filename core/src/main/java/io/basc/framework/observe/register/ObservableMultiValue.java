package io.basc.framework.observe.register;

import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.Elements;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ObservableMultiValue<K, V> extends ObservableList<V> {
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	@NonNull
	private final K key;
	private PublishService<RegistryEvent<Entry<K, V>>> entryEventPublishService;

	protected void pushEntryEvents(Elements<RegistryEvent<V>> events) {
		Elements<RegistryEvent<Entry<K, V>>> entryEvents = events
				.map((e) -> new RegistryEvent<>(this, e.getType(), new ObservableEntry<>(key, e.getPayload())));
		entryEventPublishService.publishBatchEvent(entryEvents);
	}

	public BatchRegistration<PayloadRegistration<Entry<K, V>>> getEntryRegistrations() {
		return getRegistrations().map((e) -> new PayloadRegistration<>(e, new ObservableEntry<>(key, e.getPayload())));
	}

	public final void setEntryEventPublishService(PublishService<RegistryEvent<Entry<K, V>>> entryEventPublishService) {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			this.entryEventPublishService = entryEventPublishService;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void publishBatchEvent(Elements<RegistryEvent<V>> events) throws EventPushException {
		try {
			super.publishBatchEvent(events);
		} finally {
			Lock lock = readWriteLock.readLock();
			lock.lock();
			try {
				if (entryEventPublishService != null) {
					pushEntryEvents(events);
				}
			} finally {
				lock.unlock();
			}
		}
	}
}
