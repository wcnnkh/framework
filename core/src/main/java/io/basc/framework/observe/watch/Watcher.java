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
import io.basc.framework.observe.mode.ObserverMode;
import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.observe.register.Registry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

/**
 * 使用Watch实现观察者
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class Watcher<T> extends ObserverMode<WatchEvent<T>> {
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
	private final Registry<WatchKey> registry;

	private Elements<? extends WatchEvent.Kind<?>> watchEventKinds = getAllWatchEventKinds();

	private final WatchService watchService;

	public Watcher(Class<T> contextType) {
		this(getDefaultWatchService(), contextType);
	}

	public Watcher(Registry<WatchKey> registry, Class<T> contextType) {
		this(registry, getDefaultWatchService(), contextType);
	}

	public Watcher(Registry<WatchKey> registry, WatchService watchService, Class<T> contextType) {
		Assert.requiredArgument(registry != null, "registry");
		Assert.requiredArgument(watchService != null, "watchService");
		Assert.requiredArgument(contextType != null, "contextType");
		this.registry = registry;
		this.watchService = watchService;
		this.contextType = contextType;
	}

	public Watcher(WatchService watchService, Class<T> contextType) {
		this(new ElementRegistry<>(), watchService, contextType);
	}

	public Registry<WatchKey> getRegistry() {
		return registry;
	}

	public Elements<? extends WatchEvent.Kind<?>> getWatchEventKinds() {
		return watchEventKinds;
	}

	public Elements<WatchKey> getWatchKeys() {
		return registry.getServices().filter((e) -> e.isValid());
	}

	public final ElementRegistration<WatchKey> register(Watchable watchable) throws IOException {
		return register(watchable, getWatchEventKinds().toArray(WatchEvent.Kind<?>[]::new));
	}

	public final ElementRegistration<WatchKey> register(Watchable watchable, WatchEvent.Kind<?>... events)
			throws IOException {
		WatchKey watchKey = watchable.register(watchService, events);
		return register(watchKey);
	}

	public final ElementRegistration<WatchKey> register(Watchable watchable, WatchEvent.Kind<?>[] events,
			WatchEvent.Modifier... modifiers) throws IOException {
		WatchKey watchKey = watchable.register(watchService, events, modifiers);
		return register(watchKey);
	}

	public ElementRegistration<WatchKey> register(WatchKey watchKey) {
		return new ElementRegistration<WatchKey>(watchKey, registry.register(watchKey));
	}

	@Override
	public void run() {
		WatchKeyObserver<WatchKey, T> watchKeysObserver = new WatchKeyObserver<>(getWatchKeys(), contextType);
		watchKeysObserver.registerBatchListener((e) -> publishBatchEvent(e));
		watchKeysObserver.run();
	}

	public void setWatchEventKinds(Elements<? extends WatchEvent.Kind<?>> watchEventKinds) {
		Assert.requiredArgument(watchEventKinds != null, "watchEventKinds");
		this.watchEventKinds = watchEventKinds;
	}

	@Override
	public void await() throws InterruptedException {
		watchService.take();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return watchService.poll(timeout, unit) != null;
	}

}
