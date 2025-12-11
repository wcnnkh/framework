package run.soeasy.framework.io.watch;

import java.nio.file.WatchKey;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import run.soeasy.framework.core.exchange.CollectionContainer;

/**
 * WatchKey注册容器, 专门用于管理{@link WatchKey}的注册与生命周期，
 * 核心功能是自动清理无效的{@link WatchKey}，确保容器中仅保留有效的监控键，适用于多目录文件系统监控场景（如批量管理WatchService的监控键）。
 * 
 * <p>
 * 设计目的： - 作为{@link WatchKey}的集中管理组件，跟踪所有注册的监控键； -
 * 通过{@link #cleanup()}实现无效{@link WatchKey}的自动清理，避免无效资源占用； -
 * 
 * @author soeasy.run
 * @see WatchKey
 */
public class WatchKeyRegistry extends CollectionContainer<WatchKey, CopyOnWriteArrayList<WatchKey>> {

	/**
	 * 初始化WatchKey注册容器
	 */
	public WatchKeyRegistry() {
		super(new CopyOnWriteArrayList<>());
	}

	/**
	 * 清理方法，移除容器中所有无效的{@link WatchKey}
	 * 
	 */
	public void cleanup() {
		Iterator<WatchKey> iterator = getContainer().iterator();
		while (iterator.hasNext()) {
			WatchKey watchKey = iterator.next();
			if (!watchKey.isValid()) {
				iterator.remove();
			}
		}
	}
}