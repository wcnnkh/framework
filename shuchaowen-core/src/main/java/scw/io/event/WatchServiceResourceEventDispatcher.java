package scw.io.event;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import scw.core.annotation.UseJavaVersion;
import scw.event.support.EventType;
import scw.io.Resource;
import scw.util.KeyValuePair;

/**
 * 使用WatchService实现resource监听<br/>
 * 需要jdk7(包含)以上<br/>
 * 事件可能会重复触发，这与操作系统的实现有关
 * @author shuchaowen
 *
 */
@UseJavaVersion(7)
public class WatchServiceResourceEventDispatcher extends DefaultResourceEventDispatcher {
	private static final WatchService WATCH_SERVICE;
	private static ConcurrentHashMap<Path, ResourceWatchKey> listenerMap;

	static {
		WatchService watchService = null;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
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
						} catch (Exception e) {
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

	public WatchServiceResourceEventDispatcher(Resource resource) {
		super(resource);
	}

	public WatchServiceResourceEventDispatcher(Resource resource, long listenerPeriod) {
		super(resource, listenerPeriod);
	}

	private volatile boolean isRegisterWatchService = false;

	private boolean watchServiceRegister() {
		if (WATCH_SERVICE == null) {
			return false;
		}

		if (isRegisterWatchService) {
			return false;
		}

		synchronized (this) {
			if (isRegisterWatchService) {
				return false;
			}

			File file;
			try {
				file = getResource().getFile();
				if (file.isDirectory()) {
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
				isRegisterWatchService = true;
				return true;
			} catch (IOException e) {
				// 如果出现异常就使用默认的方式来实现监听
				return false;
			}
		}
	}

	@Override
	protected void onChange(ResourceEvent resourceEvent) {
		if (resourceEvent.getEventType() == EventType.CREATE) {
			if (watchServiceRegister()) {
				cancelListener();
			}
		}
		super.onChange(resourceEvent);
	}

	@Override
	protected void listener() {
		if (watchServiceRegister()) {
			return;
		}
		super.listener();
	}

	private static class ResourceWatchKey implements Runnable {
		private WatchKey watchKey;
		private CopyOnWriteArrayList<KeyValuePair<String, Resource>> resourceList = new CopyOnWriteArrayList<KeyValuePair<String, Resource>>();

		public void register(File file, Resource resource) {
			resourceList.add(new KeyValuePair<String, Resource>(file.getName(), resource));
		}

		public void setWatchKey(WatchKey watchKey) {
			this.watchKey = watchKey;
		}

		public void run() {
			if (watchKey == null || !watchKey.isValid()) {
				return;
			}

			List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
			for (WatchEvent<?> event : watchEvents) {
				Object context = event.context();
				if (!(context instanceof Path)) {
					continue;
				}

				Path path = (Path) context;
				EventType eventType = null;
				if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					eventType = EventType.CREATE;
				} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					eventType = EventType.UPDATE;
				} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
					eventType = EventType.DELETE;
				}
				if (eventType == null) {
					return;
				}

				File file = path.toFile();
				for (KeyValuePair<String, Resource> keyValuePair : resourceList) {
					if (file.getName().equals(keyValuePair.getKey())) {
						keyValuePair.getValue().getEventDispatcher()
								.publishEvent(new ResourceEvent(eventType, keyValuePair.getValue()));
					}
				}
				;
			}

			watchKey.reset();
		}
	}
}
