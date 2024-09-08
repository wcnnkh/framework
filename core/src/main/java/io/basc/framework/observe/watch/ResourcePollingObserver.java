package io.basc.framework.observe.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import io.basc.framework.io.Resource;
import io.basc.framework.observe.PollingObserver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.event.batch.BatchEventDispatcher;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import lombok.NonNull;

/**
 * 一个文件可能不存在或是不受Watch支持的，那么降级使用轮询时间
 * 
 * @author shuchaowen
 *
 */
public class ResourcePollingObserver extends PollingObserver<ChangeEvent<Resource>> {
	private static Logger logger = LoggerFactory.getLogger(ResourcePollingObserver.class);
	private volatile Long lastModified;
	private final Resource resource;

	public ResourcePollingObserver(@NonNull BatchEventDispatcher<ChangeEvent<Resource>> eventDispatcher,
			@NonNull Resource resource) {
		super(eventDispatcher);
		Assert.requiredArgument(resource != null, "resource");
		this.resource = resource;
		try {
			this.lastModified = resource.exists() ? resource.lastModified() : null;
		} catch (IOException e) {
			// 忽略，不做空处理，因为已经判断过资源是否存在
			logger.trace(e, "Initialize acquisition lastModified");
		}
	}

	public Resource getResource() {
		return resource;
	}

	private volatile File watchDirectory;
	private volatile File resourceFile;

	public File getResourceFile() {
		if (resourceFile == null) {
			synchronized (this) {
				if (resourceFile == null) {
					try {
						resourceFile = resource.getFile();
					} catch (IOException e) {
					}
				}
			}
		}
		return resourceFile;
	}

	public File getWatchDirectory() {
		if (watchDirectory == null) {
			File file = getResourceFile();
			if (file == null) {
				return null;
			}

			synchronized (this) {
				if (watchDirectory == null) {
					// 不存在的目录是无法进行watch的, watch只能监听直接子目录
					File watchDirectory = file.isDirectory() ? file : file.getParentFile();
					if (!watchDirectory.exists()) {
						return null;
					}

					this.watchDirectory = watchDirectory;
				}
			}
		}
		return watchDirectory;
	}

	private volatile WatchKey watchKey;

	public WatchKey getWatchKey() {
		return watchKey;
	}

	public void setWatchKey(WatchKey watchKey) {
		this.watchKey = watchKey;
	}

	public boolean register(WatchService watchService) {
		if (watchKey == null) {
			synchronized (this) {
				if (watchKey == null) {
					// 没注册过
					File watchDirectory = getWatchDirectory();
					if (watchDirectory != null && watchDirectory.exists()) {
						// 只有已存在的目录才能使用watch
						try {
							watchKey = watchDirectory.toPath().register(watchService,
									StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
									StandardWatchEventKinds.ENTRY_MODIFY);
							return true;
						} catch (IOException e) {
							logger.error(e, "Registration for watch failed");
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public synchronized void run() {
		synchronized (this) {
			if (this.watchKey == null) {
				// 无法注册watch，使用兜底方案
				Long lastModified;
				try {
					lastModified = resource.exists() ? resource.lastModified() : null;
				} catch (IOException e) {
					// 忽略，不做空处理，因为已经判断过资源是否存在
					logger.trace(e, "Unable to obtain lastModified");
					return;
				}

				this.lastModified = lastModified;
				getEventDispatcher().publishEvent(
						new ChangeEvent<>(resource, ChangeType.getChangeType(this.lastModified, lastModified)));
			} else {
				WatchKeyPollingObserver<Path> watchKeyObserver = new WatchKeyPollingObserver<>(watchKey, Path.class);
				watchKeyObserver.registerListener(this::publishWatchEvent);
				watchKeyObserver.run();
			}
		}
	}

	public void publishWatchEvent(WatchEvent<Path> watchEvent) {
		ChangeType eventType = null;
		if (StandardWatchEventKinds.ENTRY_CREATE.equals(watchEvent.kind())) {
			eventType = ChangeType.CREATE;
		} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(watchEvent.kind())) {
			eventType = ChangeType.UPDATE;
		} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(watchEvent.kind())) {
			eventType = ChangeType.DELETE;
		}

		if (eventType == null) {
			return;
		}

		File resourceFile = getResourceFile();
		if (resourceFile == null) {
			// 不可能，因为watch需要知道path，所以一定存在
			return;
		}

		Path path = watchEvent.context();
		if (resourceFile.isDirectory()) {
			// 如果是个目录，那么只要路径前缀一致就可以了
			if (path.startsWith(resourceFile.toPath())) {
				getEventDispatcher().publishEvent(new ChangeEvent<Resource>(resource, eventType));
			}
		} else {
			// 如果是文件那么需要全匹配
			if (path.equals(resourceFile.toPath())) {
				getEventDispatcher().publishEvent(new ChangeEvent<>(resource, eventType));
			}
		}
	}
}
