package run.soeasy.framework.core.exchange.event;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.function.Filter;
import run.soeasy.framework.core.exchange.BatchListenableChannel;
import run.soeasy.framework.core.exchange.FakeSingleListenableChannel;
import run.soeasy.framework.core.exchange.ListenableChannel;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.container.Registry;
import run.soeasy.framework.core.exchange.container.collection.ArrayListContainer;

/**
 * 批量事件分发器，支持一次性处理和分发多个事件对象。
 * <p>
 * 该类继承自{@link EventDispatcher}，专门用于处理批量事件（{@link Elements&lt;{@link T}&gt;}），
 * 实现了{@link BatchListenableChannel}接口以提供批量事件的发布和监听能力，
 * 适用于需要批量处理事件的高性能场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>批量事件处理：支持一次性发布和处理{@link Elements&lt;{@link T}&gt;}类型的批量事件</li>
 *   <li>单批量转换：通过{@link #single()}方法支持批量与单事件通道的转换</li>
 *   <li>高效分发：批量事件分发可减少事件调度开销，提升高并发场景下的性能</li>
 *   <li>监听器管理：继承自父类的监听器注册、过滤和生命周期管理能力</li>
 * </ul>
 *
 * @param <T> 事件元素的类型，批量事件为{@link Elements&lt;T&gt;}
 * 
 * @author soeasy.run
 * @see EventDispatcher
 * @see BatchListenableChannel
 * @see Elements
 */
public class BatchEventDispatcher<T> extends EventDispatcher<Elements<T>> implements BatchListenableChannel<T> {
    
    /** 单事件通道适配器，用于将批量事件转换为单事件处理 */
    private final FakeSingleListenableChannel<T, ListenableChannel<Elements<T>>> single = () -> this;

    /**
     * 无参构造函数，使用默认配置
     * <p>
     * 初始化时使用{@link ArrayListContainer}作为监听器注册表，
     * 并使用{@link Filter#identity()}作为默认过滤器。
     */
    public BatchEventDispatcher() {
        this(new ArrayListContainer<>(), Filter.identity());
    }

    /**
     * 自定义配置构造函数
     * 
     * @param registry 监听器注册表，不可为null
     * @param filter 监听器过滤器，不可为null
     * @throws NullPointerException 若registry或filter为null
     */
    public BatchEventDispatcher(@NonNull Registry<Listener<? super Elements<T>>> registry,
            @NonNull Filter<Listener<? super Elements<T>>> filter) {
        super(registry, filter);
    }

    /**
     * 获取单事件通道适配器
     * <p>
     * 返回一个适配器，允许通过单事件接口处理批量事件，
     * 适用于需要兼容单事件处理逻辑的场景。
     * 
     * @return 单事件通道适配器
     */
    @Override
    public ListenableChannel<T> single() {
        return single;
    }
}