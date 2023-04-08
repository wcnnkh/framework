package io.basc.framework.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.lang.RequiredJavaVersion;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

/**
 * 使用WatchService实现resource监听 需要jdk7(包含)以上 事件可能会重复触发，这与操作系统的实现有关
 * 
 * @author wcnnkh
 *
 */
@RequiredJavaVersion(7)
public class WatchServiceResourceEventDispatcher extends SimpleResourceEventDispatcher {
	private static Logger logger = LoggerFactory.getLogger(WatchServiceResourceEventDispatcher.class);
	private static final WatchService WATCH_SERVICE;
	private static ConcurrentHashMap<Path, ResourceWatchKey> listenerMap;

	static {
		WatchService watchService = null;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			logger.error(e, "无法创建WatchService");
		}

		WATCH_SERVICE = watchService;
		if (WATCH_SERVICE != null) {
			listenerMap = new ConcurrentHashMap<Path, WatchServiceResourceEventDispatcher.ResourceWatchKey>();
			Thread thread = new Thread() {
				public void run() {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							WATCH_SERVICE.take();
							for (ResourceWatchKey key : listenerMap.values()) {
								key.run();
							}
						} catch (Throwable e) {
							// 如果出现异常就休眠一秒再轮询
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException e1) {
							}
						}
					}
				};
			};
			thread.setDaemon(true);
			thread.setName(WatchServiceResourceEventDispatcher.class.getSimpleName());
			thread.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						WATCH_SERVICE.close();
					} catch (Exception e) {
					}
					super.run();
				}
			});
		}
	}

	public WatchServiceResourceEventDispatcher(AbstractResource resource) {
		super(resource);
	}

	public WatchServiceResourceEventDispatcher(AbstractResource resource, long listenerPeriod) {
		super(resource, listenerPeriod);
	}

	private AtomicBoolean registred = new AtomicBoolean();

	private boolean watchServiceRegister() {
		if (WATCH_SERVICE == null) {
			return false;
		}

		if (!registred.get() && registred.compareAndSet(false, true)) {
			File file;
			try {
				file = getResource().getFile();
				if (file.isDirectory() || file.getParentFile() == null) {
					return false;
				}

				Path path = file.getParentFile().toPath();
				ResourceWatchKey resourceWatchKey = listenerMap.get(path);
				if (resourceWatchKey == null) {
					resourceWatchKey = new ResourceWatchKey();
					ResourceWatchKey old = listenerMap.putIfAbsent(path, resourceWatchKey);
					if (old != null) {
						resourceWatchKey = old;
					} else {
						WatchKey watchKey = path.register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_CREATE,
								StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
						resourceWatchKey.setWatchKey(watchKey);
					}
				}

				resourceWatchKey.register(file, getResource());
				return true;
			} catch (IOException e) {
				// 如果出现异常就使用默认的方式来实现监听
				registred.compareAndSet(true, false);
				return false;
			}
		}
		return false;
	}

	@Override
	public void publishEvent(ChangeEvent<Resource> event) {
		if (event.getChangeType() == ChangeType.CREATE) {
			// 如果资源创建了，那么尝试重新注册
			if (watchServiceRegister()) {
				cancelListener();
			}
		}
		super.publishEvent(event);
	}

	@Override
	protected void listener() {
		if (watchServiceRegister()) {
			return;
		}
		// 注册失败就使用默认的方式实现
		super.listener();
	}

	private static final class ResourceItem {
		private final String name;
		private final AbstractResource resource;

		public ResourceItem(String name, AbstractResource resource) {
			this.name = name;
			this.resource = resource;
		}

		public String getName() {
			return name;
		}

		public AbstractResource getResource() {
			return resource;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof ResourceItem) {
				return ((ResourceItem) obj).resource == resource;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return resource.hashCode();
		}
	}

	private static class ResourceWatchKey implements Runnable {
		private WatchKey watchKey;
		private final Set<ResourceItem> resources = new CopyOnWriteArraySet<ResourceItem>();

		public void register(File file, AbstractResource resource) {
			resources.add(new ResourceItem(file.getName(), resource));
		}

		public void setWatchKey(WatchKey watchKey) {
			this.watchKey = watchKey;
		}

		public void run() {
			if (watchKey == null || !watchKey.isValid()) {
				return;
			}

			List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
			try {
				for (WatchEvent<?> event : watchEvents) {
					Object context = event.context();
					if (context == null) {
						continue;
					}

					if (!(context instanceof Path)) {
						continue;
					}

					Path path = (Path) context;
					File file = path.toFile();
					if (file == null) {
						continue;
					}

					ChangeType eventType = null;
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
						eventType = ChangeType.CREATE;
					} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
						eventType = ChangeType.UPDATE;
					} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
						eventType = ChangeType.DELETE;
					}

					if (eventType == null) {
						continue;
					}

					for (ResourceItem item : resources) {
						if (file.getName().equals(item.getName())) {
							ObservableChangeEvent<Resource> resourceEvent = new ObservableChangeEvent<Resource>(
									eventType, item.getResource(), item.getResource());
							if (logger.isDebugEnabled()) {
								logger.debug(resourceEvent.toString());
							}

							try {
								item.getResource().publishEvent(resourceEvent);
							} catch (Throwable e) {
								logger.error(e, item.getResource().getDescription());
							}
						}
					}
					;
				}
			} catch (Throwable e) {
				logger.error(e, watchKey.toString());
			}
			watchKey.reset();
		}
	}
}
