package io.basc.framework.util.function.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.event.empty.EmptyEventDispatcher;
import io.basc.framework.util.function.Source;
import io.basc.framework.util.observe.event.ChangeEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CacheableSource<T, E extends Throwable, W extends Source<? extends T, ? extends E>>
		implements Source<T, E>, Wrapper<W>, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile T cached;
	@NonNull
	private final transient EventPublishService<ChangeEvent<T>> eventPublishService;
	/**
	 * 是否已加载
	 */
	private volatile boolean loaded = false;
	@NonNull
	private final transient ReadWriteLock readWriteLock;
	@NonNull
	private final transient W source;

	public CacheableSource(@NonNull EventPublishService<ChangeEvent<T>> eventPublishService, @NonNull W source) {
		this(eventPublishService, new ReentrantReadWriteLock(), source);
	}

	public CacheableSource(@NonNull W source) {
		this(EmptyEventDispatcher.empty(), source);
	}

	@Override
	public T get() throws E {
		if (!loaded) {
			reload(false);
		}
		return cached;
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.cached = (T) input.readObject();
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param force
	 * @return
	 * @throws E
	 */
	public boolean reload(boolean force) throws E {
		if (!loaded || force) {
			Lock lock = readWriteLock.writeLock();
			lock.lock();
			try {
				if (!loaded || force) {
					T cached = source.get();
					setLoaded(true);
					setCached(cached);
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	public T setCached(T cached) {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			T oldValue = this.cached;
			this.cached = cached;
			eventPublishService.publishEvent(new ChangeEvent<>(oldValue, this.cached));
			return oldValue;
		} finally {
			lock.unlock();
		}
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(getSource());
	}
}
