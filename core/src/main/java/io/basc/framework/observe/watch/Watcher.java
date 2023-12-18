package io.basc.framework.observe.watch;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.observe.PollingObserver;
import io.basc.framework.observe.PollingService;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class Watcher<T> extends PollingObserver<WatchEvent<T>> implements PollingService<Elements<WatchEvent<T>>> {
	private static volatile WatchService defaultWatchService;
	private static Logger logger = LoggerFactory.getLogger(Watcher.class);
	private static volatile boolean newDefaultWatchServieError = false;

	/**
	 * 获取全部的{@link WatchEvent.Kind}
	 * 
	 * @return
	 */
	public static Elements<WatchEvent.Kind<?>> getAllWatchEventKinds() {
		Field[] fields = StandardWatchEventKinds.class.getFields();
		if (fields == null || fields.length == 0) {
			return Elements.empty();
		}

		List<WatchEvent.Kind<?>> list = new ArrayList<>(fields.length);
		try {
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (!WatchEvent.Kind.class.isAssignableFrom(field.getType())) {
					continue;
				}

				WatchEvent.Kind<?> kind = (Kind<?>) field.get(null);
				list.add(kind);
			}
		} catch (IllegalAccessException e) {
			logger.trace(e, "getAllWatchEventKinds");
		}
		return Elements.of(list);
	}

	/**
	 * 获取默认的WatchService
	 * 
	 * @return 为空说明无法获取，出现异常
	 */
	@Nullable
	public static WatchService getDefaultWatchService() {
		if (newDefaultWatchServieError) {
			return null;
		}

		if (defaultWatchService == null) {
			synchronized (Watcher.class) {
				if (defaultWatchService == null) {
					try {
						defaultWatchService = FileSystems.getDefault().newWatchService();
						Runtime.getRuntime().addShutdownHook(new Thread(() -> {
							try {
								defaultWatchService.close();
							} catch (IOException e) {
								logger.error(e, "Closing defaultWatchService exception");
							}
						}));
					} catch (IOException e) {
						newDefaultWatchServieError = true;
						logger.error(e, "Unable to obtain default WatchService");
					}
				}
			}
		}
		return defaultWatchService;
	}

	private final Class<T> contextType;
	private final WatchService watchService;

	public Watcher(Class<T> contextType) {
		this(getDefaultWatchService(), contextType);
	}

	public Watcher(WatchService watchService, Class<T> contextType) {
		Assert.requiredArgument(watchService != null, "watchService");
		Assert.requiredArgument(contextType != null, "contextType");
		this.watchService = watchService;
		this.contextType = contextType;
	}

	@Override
	public void await() throws InterruptedException {
		take();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		WatchKey watchKey = watchService.poll(timeout, unit);
		if (watchKey == null) {
			return false;
		}

		publishWatchKey(watchKey);
		return true;
	}

	public WatchKey register(Watchable watchable, WatchEvent.Kind<?>... events) throws IOException {
		return watchable.register(watchService, events);
	}

	public WatchKey register(Watchable watchable, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers)
			throws IOException {
		return watchable.register(watchService, events, modifiers);
	}

	@Override
	public void run() {
		poll();
	}

	@Override
	public Elements<WatchEvent<T>> poll() {
		WatchKey watchKey = watchService.poll();
		return publishWatchKey(watchKey);
	}

	public Elements<WatchEvent<T>> publishWatchKey(WatchKey watchKey) {
		if (watchKey == null) {
			return Elements.empty();
		}

		WatchKeyPollingObserver<T> watchKeyObserver = new WatchKeyPollingObserver<>(watchKey, contextType);
		try {
			Elements<WatchEvent<T>> elements = watchKeyObserver.pollEvents();
			publishBatchEvent(elements);
			return elements;
		} finally {
			watchKeyObserver.reset();
		}
	}

	@Override
	public Elements<WatchEvent<T>> poll(long timeout, TimeUnit unit) throws InterruptedException {
		WatchKey watchKey = watchService.poll(timeout, unit);
		return publishWatchKey(watchKey);
	}

	@Override
	public Elements<WatchEvent<T>> take() throws InterruptedException {
		WatchKey watchKey = watchService.take();
		return publishWatchKey(watchKey);
	}

}
