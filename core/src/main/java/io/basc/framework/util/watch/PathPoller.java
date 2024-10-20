package io.basc.framework.util.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class PathPoller<T extends FileVariable> extends VariablePoller<T> {
	private static Logger logger = LogManager.getLogger(PathPoller.class);
	private volatile Path watchable;
	// 初始容量给小了，因为大部分情况下只会有一个WatchKey
	private final WatchKeyRegistry watchKeyRegistry = new WatchKeyRegistry(2);

	public PathPoller(@NonNull T variable, @NonNull Publisher<? super Elements<ChangeEvent<T>>> changeEventProducer) {
		super(variable, changeEventProducer);
	}

	private ChangeEvent<T> eventConvert(WatchEvent<Path> watchEvent) {
		ChangeType eventType = null;
		if (StandardWatchEventKinds.ENTRY_CREATE.equals(watchEvent.kind())) {
			eventType = ChangeType.CREATE;
		} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(watchEvent.kind())) {
			eventType = ChangeType.UPDATE;
		} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(watchEvent.kind())) {
			eventType = ChangeType.DELETE;
		}

		if (eventType == null) {
			// 忽略未知的事件类型
			return null;
		}

		File sourceFile;
		try {
			sourceFile = getVariable().getFile();
		} catch (IOException e) {
			// 获取文件异常
			logger.error(e, "Unable to retrieve the original file {}", getVariable());
			return null;
		}

		if (sourceFile == null) {
			// 不可能，因为watch需要知道path，所以一定存在
			return null;
		}

		Path path = watchEvent.context();
		if (sourceFile.isDirectory()) {
			// 如果是个目录，那么只要路径前缀一致就可以了
			if (path.startsWith(sourceFile.toPath())) {
				return new ChangeEvent<T>(getVariable(), eventType);
			}
		} else {
			// 如果是文件那么需要全匹配
			if (path.equals(sourceFile.toPath())) {
				return new ChangeEvent<>(getVariable(), eventType);
			}
		}
		return null;
	}

	/**
	 * 获取一个可观察的Path
	 * 
	 * @return 可能为空
	 */
	public Path getWatchable() {
		if (watchable == null) {
			File file = null;
			try {
				file = getVariable().getFile();
			} catch (IOException e) {
				logger.error(e, "{} Unable to obtain File", getVariable());
			}

			if (file == null) {
				return null;
			}

			synchronized (this) {
				if (watchable == null) {
					// 不存在的目录是无法进行watch的, watch只能监听直接子目录
					File watchDirectory = file.isDirectory() ? file : file.getParentFile();
					if (!watchDirectory.exists()) {
						return null;
					}

					this.watchable = watchDirectory.toPath();
				}
			}
		}
		return watchable;
	}

	public WatchKeyRegistry getWatchKeyRegistry() {
		return watchKeyRegistry;
	}

	protected WatchKey newWatchKey(WatchService watchService) {
		Path watchable = getWatchable();
		if (watchable != null) {
			// 只有已存在的目录才能使用watch
			try {
				return watchable.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			} catch (IOException e) {
				logger.error(e, "{} Registration for watch failed", getVariable());
				return null;
			}
		}
		return null;
	}

	private Receipt publishWatchEvents(Elements<WatchEvent<Path>> watchEvents) {
		Elements<ChangeEvent<T>> events = watchEvents.map(this::eventConvert).filter((e) -> e != null).toList();
		return getChangeEventProducer().publish(events);
	}

	public PayloadRegistration<WatchKey> registerTo(WatchService watchService) {
		WatchKey watchKey = newWatchKey(watchService);
		if (watchKey == null) {
			return PayloadRegistration.cancelled();
		}
		return watchKeyRegistry.register(watchKey);
	}

	@Override
	public void run() {
		try {
			run(watchKeyRegistry);
		} finally {
			super.run();
		}
	}

	public void run(Iterable<? extends WatchKey> watchKeys) {
		for (WatchKey watchKey : watchKeys) {
			WatchKeyPoller<Path> poller = new WatchKeyPoller<>(watchKey, Path.class, this::publishWatchEvents);
			poller.run();
		}
	}
}
