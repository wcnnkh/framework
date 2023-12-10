package io.basc.framework.observe.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.Changed;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.PayloadChangeEvent;
import io.basc.framework.observe.PollingObserver;
import io.basc.framework.util.Assert;

/**
 * 一个文件可能不存在或是不受Watch支持的，那么降级使用轮询时间
 * 
 * @author shuchaowen
 *
 */
public class ResourceObserver extends PollingObserver<ChangeEvent> {
	private static Logger logger = LoggerFactory.getLogger(ResourceObserver.class);
	private volatile WatchService watchService;
	private volatile Long lastModified;
	private final Resource resource;

	public ResourceObserver(Resource resource) {
		Assert.requiredArgument(resource != null, "resource");
		this.resource = resource;
		this.lastModified = resource.exists() ? resource.lastModified() : null;
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

	/**
	 * 获取或注册Watch
	 * 
	 * @return 如果为空说明注册失败
	 */
	public WatchKey getWatchKey() {
		if (watchKey == null && watchService != null) {
			synchronized (this) {
				if (watchKey == null && watchService != null) {
					// 没注册过
					File watchDirectory = getWatchDirectory();
					if (watchDirectory != null && watchDirectory.exists()) {
						// 只有已存在的目录才能使用watch
						try {
							watchKey = watchDirectory.toPath().register(watchService,
									StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
									StandardWatchEventKinds.ENTRY_MODIFY);
						} catch (IOException e) {
							logger.error(e, "Registration for watch failed");
						}
					}
				}
			}
		}
		return watchKey;
	}

	@Override
	public synchronized void run() {
		WatchKey watchKey = getWatchKey();
		if (watchKey != null) {
			PollingWatchKeyObserver<Path> watchKeyObserver = new PollingWatchKeyObserver<>(watchKey, Path.class);
			watchKeyObserver.registerListener(this::onWatchEvent);
			watchKeyObserver.run();
		} else {
			// 无法注册watch，使用兜底方案
			Long lastModified = resource.exists() ? resource.lastModified() : null;
			Changed<Long> changed = new Changed<Long>(this.lastModified, lastModified);
			this.lastModified = lastModified;
			publishEvent(new ObservableEvent<>(this, changed));
		}
	}

	@Override
	public void await() throws InterruptedException {
		if (watchService != null) {
			synchronized (this) {
				if (watchService != null) {
					watchService.take();
					return;
				}
			}
		}
		super.await();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		if (watchService != null) {
			synchronized (this) {
				if (watchService != null) {
					WatchKey watchKey = watchService.poll(timeout, unit);
					return watchKey != null;
				}
			}
		}
		return super.await(timeout, unit);
	}

	public boolean start() {
		WatchKey watchKey = getWatchKey();
		if (watchKey == null) {
			// 无法注册或不支持，使用兜底模式
			return startTimerTask();
		} else {
			return startEndlessLoop();
		}
	}

	public boolean stop() {
		WatchKey watchKey = getWatchKey();
		if (watchKey == null) {
			return stopTimerTask();
		} else {
			watchKey.cancel();
			return stopEndlessLoop();
		}
	}

	private void onWatchEvent(WatchEvent<Path> watchEvent) {
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
				publishEvent(new PayloadChangeEvent<>(this, eventType, watchEvent));
			}
		} else {
			// 如果是文件那么需要全匹配
			if (path.equals(resourceFile.toPath())) {
				publishEvent(new PayloadChangeEvent<>(this, eventType, watchEvent));
			}
		}
	}
}
