package run.soeasy.framework.core.exchange.container.collection;

import java.util.ArrayList;
import java.util.RandomAccess;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 基于ArrayList的容器实现，提供随机访问和动态扩容能力。
 * <p>
 * 该容器继承自{@link ListContainer}，使用{@link ArrayList}作为底层存储结构，
 * 支持元素的快速随机访问，并实现了{@link RandomAccess}接口以标识此特性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>随机访问：支持O(1)时间复杂度的元素随机访问</li>
 *   <li>动态扩容：自动处理容量不足时的扩容操作</li>
 *   <li>注册管理：继承列表容器的元素注册与生命周期管理能力</li>
 *   <li>事件驱动：元素变更时自动触发相应事件</li>
 * </ul>
 *
 * @param <E> 注册元素的类型
 * 
 * @author soeasy.run
 * @see ListContainer
 * @see ArrayList
 * @see RandomAccess
 */
public class ArrayListContainer<E> extends ListContainer<E, ArrayList<ElementRegistration<E>>> implements RandomAccess {
    
    /**
     * 默认构造函数，使用默认容量初始化ArrayList
     */
    public ArrayListContainer() {
        this(ArrayList::new);
    }

    /**
     * 指定初始容量的构造函数
     * <p>
     * 初始化具有指定初始容量的ArrayList容器，减少扩容操作。
     * 
     * @param initialCapacity 初始容量
     */
    public ArrayListContainer(int initialCapacity) {
        this(() -> new ArrayList<>(initialCapacity));
    }

    /**
     * 自定义容器源的构造函数
     * <p>
     * 使用提供的Supplier创建ArrayList实例，支持自定义初始化逻辑。
     * 
     * @param containerSource ArrayList实例的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public ArrayListContainer(
            @NonNull ThrowingSupplier<? extends ArrayList<ElementRegistration<E>>, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }
}