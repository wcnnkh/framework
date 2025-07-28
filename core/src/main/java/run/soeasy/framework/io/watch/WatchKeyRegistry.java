package run.soeasy.framework.io.watch;

import java.nio.file.WatchKey;
import java.util.Iterator;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.collection.ArrayListContainer;

/**
 * WatchKey注册容器，继承自{@link ArrayListContainer}，专门用于管理{@link WatchKey}的注册与生命周期，
 * 核心功能是自动清理无效的{@link WatchKey}，确保容器中仅保留有效的监控键，适用于多目录文件系统监控场景（如批量管理WatchService的监控键）。
 * 
 * <p>设计目的：
 * - 作为{@link WatchKey}的集中管理组件，跟踪所有注册的监控键；
 * - 通过重写{@link #cleanup()}实现无效{@link WatchKey}的自动清理，避免无效资源占用；
 * - 提供{@link #getElements()}方法过滤出无效的注册项，方便后续处理（如重新注册监控）。
 * 
 * @author soeasy.run
 * @see ArrayListContainer
 * @see WatchKey
 * @see ElementRegistration
 */
public class WatchKeyRegistry extends ArrayListContainer<WatchKey> {

    /**
     * 初始化WatchKey注册容器，指定初始容量
     * 
     * @param initialCapacity 容器的初始容量（用于优化底层ArrayList的初始大小，减少扩容开销）
     */
    public WatchKeyRegistry(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 重写清理方法，移除容器中所有无效的{@link WatchKey}
     * 
     * <p>清理逻辑：
     * 1. 调用父类{@link ArrayListContainer#cleanup()}执行基础清理；
     * 2. 通过迭代器遍历所有注册的{@link ElementRegistration}；
     * 3. 移除{@link WatchKey#isValid()}返回false的注册项（无效的WatchKey无法再接收事件，需清理）。
     * 
     * <p>注：{@link WatchKey}无效通常是因为对应的监控目录被删除、监控被取消或系统级错误，此时需从容器中移除以释放资源。
     */
    @Override
    public void cleanup() {
        super.cleanup();

        // 清理无效的WatchKey：遍历并移除isValid()为false的注册项
        execute((collection) -> {
            Iterator<ElementRegistration<WatchKey>> iterator = collection.iterator();
            while (iterator.hasNext()) {
                ElementRegistration<WatchKey> registration = iterator.next();
                if (!registration.getPayload().isValid()) { // 注意：原代码可能笔误，应为!isValid()才是无效
                    iterator.remove();
                }
            }
            return true;
        });
    }

    /**
     * 重写获取元素方法，返回所有无效的{@link WatchKey}注册项
     * 
     * <p>过滤逻辑：通过{@link Elements#filter()}保留{@link WatchKey#isValid()}返回false的注册项，
     * 即仅返回无效的WatchKey注册信息，方便上层处理（如日志记录、重新注册监控等）。
     * 
     * @return 包含所有无效{@link WatchKey}注册项的{@link Elements}集合（非空，可能为空集合）
     */
    @Override
    public Elements<ElementRegistration<WatchKey>> getElements() {
        // 过滤出无效的WatchKey注册项（isValid()为false）
        return super.getElements().filter((registration) -> !registration.getPayload().isValid());
    }
}