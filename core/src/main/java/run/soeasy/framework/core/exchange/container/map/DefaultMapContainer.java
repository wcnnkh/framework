package run.soeasy.framework.core.exchange.container.map;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.container.EntryRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 默认映射容器实现，基于HashMap提供键值对注册与管理功能。
 * <p>
 * 该容器继承自{@link MapContainer}，使用HashMap作为底层存储结构，
 * 提供标准的映射操作能力，并支持键值对注册的生命周期管理和事件驱动机制。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>HashMap底层实现：提供平均O(1)复杂度的查找、插入和删除操作</li>
 *   <li>注册生命周期管理：支持键值对注册的启动、停止和取消</li>
 *   <li>事件驱动：键值对变更时自动发布{@link ChangeEvent}事件</li>
 *   <li>线程非安全：需在外层自行保证多线程环境下的同步控制</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * 
 * @author soeasy.run
 * @see MapContainer
 * @see HashMap
 */
public class DefaultMapContainer<K, V> extends MapContainer<K, V, Map<K, EntryRegistration<K, V>>> {

    /**
     * 默认构造函数，使用HashMap作为底层存储
     * <p>
     * 初始化一个空的映射容器，底层使用HashMap实现，
     * 适用于大多数常规映射场景。
     */
    public DefaultMapContainer() {
        this(HashMap::new);
    }

    /**
     * 自定义映射源的构造函数
     * <p>
     * 使用提供的Supplier创建底层映射实例，支持自定义初始化逻辑。
     * 
     * @param containerSource 映射实例的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public DefaultMapContainer(
            @NonNull ThrowingSupplier<? extends Map<K, EntryRegistration<K, V>>, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }
}