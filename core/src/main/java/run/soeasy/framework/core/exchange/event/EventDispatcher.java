package run.soeasy.framework.core.exchange.event;

import java.util.EventListener;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.function.Filter;
import run.soeasy.framework.core.exchange.AbstractChannel;
import run.soeasy.framework.core.exchange.BatchListenableChannel;
import run.soeasy.framework.core.exchange.FakeBatchListenableChannel;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.Registry;
import run.soeasy.framework.core.exchange.container.collection.ArrayListContainer;

/**
 * 事件分发器实现，提供事件的发布和监听器管理功能。
 * <p>
 * 该类继承自{@link AbstractChannel}并实现{@link ListenableChannel}接口，
 * 支持事件的同步发布、监听器注册与过滤，是事件驱动架构中的核心组件。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>事件发布：支持同步发布事件到所有匹配的监听器</li>
 *   <li>监听器管理：通过{@link Registry}注册、注销监听器</li>
 *   <li>事件过滤：通过{@link Filter}实现监听器的动态筛选</li>
 *   <li>批量操作：通过{@link BatchListenableChannel}支持批量事件处理</li>
 * </ul>
 *
 * @param <T> 事件类型
 * 
 * @author soeasy.run
 * @see ListenableChannel
 * @see EventListener
 * @see Registry
 */
@RequiredArgsConstructor
@Getter
@Setter
public class EventDispatcher<T> extends AbstractChannel<T> implements ListenableChannel<T> {
    
    /** 批量事件处理通道（伪实现，实际使用当前实例） */
    private final FakeBatchListenableChannel<T, ListenableChannel<T>> batch = () -> this;
    
    /** 监听器注册表，用于存储和管理事件监听器 */
    @NonNull
    private final Registry<Listener<? super T>> registry;
    
    /** 监听器过滤器，用于筛选符合条件的监听器 */
    @NonNull
    private Filter<Listener<? super T>> filter;

    /**
     * 无参构造函数，使用默认配置
     * <p>
     * 初始化时使用{@link ArrayListContainer}作为监听器注册表，
     * 并使用{@link Filter#identity()}作为默认过滤器。
     */
    public EventDispatcher() {
        this(new ArrayListContainer<>(), Filter.identity());
    }

    /**
     * 获取批量事件处理通道
     * <p>
     * 返回一个伪批量通道，实际操作会委派给当前实例，
     * 适用于需要批量处理接口的场景。
     * 
     * @return 批量事件处理通道
     */
    @Override
    public BatchListenableChannel<T> batch() {
        return batch;
    }

    /**
     * 注册事件监听器
     * <p>
     * 将监听器添加到注册表中，并返回可用于注销的注册句柄。
     * 监听器会在事件发布时根据过滤器条件被调用。
     * 
     * @param listener 要注册的事件监听器，不可为null
     * @return 注册句柄，用于后续注销监听器
     * @throws NullPointerException 若listener为null
     */
    @Override
    public Registration registerListener(Listener<T> listener) {
        return registry.register(listener);
    }

    /**
     * 同步发布事件到所有匹配的监听器
     * <p>
     * 该方法会阻塞当前线程直到所有监听器处理完毕，
     * 适用于事件处理耗时短、需要立即获取反馈的场景。
     * 
     * @param resource 要发布的事件对象，不可为null
     * @throws NullPointerException 若resource为null
     */
    public void syncPublish(T resource) {
        Elements<Listener<? super T>> elements = filter.apply(registry);
        CollectionUtils.acceptAll(elements, (e) -> e.accept(resource));
    }
}