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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.annotation.UseJavaVersion;
import scw.event.support.EventType;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.SystemPropertyFactory;

/**
 * 使用WatchService实现resource监听<br/>
 * 需要jdk7(包含)以上
 * 
 * @author shuchaowen
 *
 */
@UseJavaVersion(7)
public class WatchServiceResourceEventDispatcher extends DefaultResourceEventDispatcher {
	public static final boolean USE_WATCH_SERVICE = SystemPropertyFactory.getInstance().getValue("resource.watch.enable", boolean.class, true);
	private static Logger logger = LoggerUtils.getLogger(WatchServiceResourceEventDispatcher.class);
	private static final WatchService WATCH_SERVICE;
	private static ConcurrentHashMap<Path, ResourceWatchKey> listenerMap;

	static {
		WatchService watchService = null;
		if(USE_WATCH_SERVICE){
			try {
				watchService = FileSystems.getDefault().newWatchService();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		WATCH_SERVICE = watchService;

		if (WATCH_SERVICE != null) {
			listenerMap = new ConcurrentHashMap<Path, WatchServiceResourceEventDispatcher.ResourceWatchKey>();
			new Thread() {
				public void run() {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							WATCH_SERVICE.take();
							for (ResourceWatchKey key : listenerMap.values()) {
								key.run();
							}
						} catch (InterruptedException e) {
						}
					}
				};
			}.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						WATCH_SERVICE.close();
					} catch (IOException e) {
					}
				}
			});
		}
	}

	public WatchServiceResourceEventDispatcher(Resource resource) {
		super(resource);
	}

	@Override
	protected void listener() {
		if (WATCH_SERVICE == null) {
			super.listener();
			return;
		}

		File file;
		try {
			file = getResource().getFile();
			if (file.isDirectory()) {
				super.listener();
				return;
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
					logger.debug("register watch service path: {}", path);
				}
			}

			resourceWatchKey.register(file, getResource());
		} catch (IOException e) {
			logger.debug(e, "register watch service error resource: {}", getResource());
			// 如果出现异常就使用默认的方式来实现监听
			super.listener();
		}
	}

	private static class ResourceWatchKey implements Runnable {
		private WatchKey watchKey;
		private Map<String, Resource> resourceMap = new ConcurrentHashMap<String, Resource>();

		public void register(File file, Resource resource) {
			resourceMap.put(file.getName(), resource);
		}

		public void setWatchKey(WatchKey watchKey) {
			this.watchKey = watchKey;
		}

		public void run() {
			if (watchKey == null) {
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
				Resource resource = resourceMap.get(file.getName());
				if (resource == null) {
					return;
				}

				resource.getEventDispatcher().publishEvent(new ResourceEvent(eventType, resource));
			}
			watchKey.reset();
		}
	}
}
