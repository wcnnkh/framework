package run.soeasy.framework.core.exchange.container.collection;

import java.util.NoSuchElementException;
import java.util.Queue;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 队列容器实现，基于队列数据结构提供元素注册与管理功能。
 * <p>
 * 该容器继承自{@link CollectionContainer}并实现{@link Queue}接口，
 * 支持元素的队列式操作（如先进先出），同时具备注册生命周期管理和事件发布能力。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>队列操作支持：完全实现{@link Queue}接口的所有方法</li>
 *   <li>注册生命周期管理：自动处理元素注册的启动、停止和取消</li>
 *   <li>事件驱动：元素出队时自动触发注销事件</li>
 *   <li>无效元素过滤：自动跳过已取消的注册元素</li>
 * </ul>
 *
 * @param <E> 注册元素的类型
 * @param <Q> 存储注册元素的队列类型，需继承{@link Queue}&lt;{@link ElementRegistration}&lt;{@link E}&gt;&gt;
 * 
 * @author soeasy.run
 * @see CollectionContainer
 * @see Queue
 */
public class QueueContainer<E, Q extends Queue<ElementRegistration<E>>> extends CollectionContainer<E, Q>
        implements Queue<E> {

    /**
     * 构造函数，初始化队列容器
     * <p>
     * 通过供给函数获取队列实例，确保容器的存储结构。
     * 
     * @param containerSource 队列实例的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public QueueContainer(@NonNull ThrowingSupplier<? extends Q, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }

    /**
     * 将元素添加到队列尾部并注册
     * <p>
     * 若队列已满或注册失败，元素将被取消注册并返回false。
     * 成功注册的元素会触发创建事件。
     * 
     * @param e 要添加的元素
     * @return true表示添加成功，false表示添加失败
     */
    @Override
    public boolean offer(E e) {
        return registers(Elements.singleton(e), (q, rs) -> {
            rs.forEach((r) -> {
                if (!q.offer(r)) {
                    r.cancel();
                }
            });
        }, getPublisher()).isCancelled();
    }

    /**
     * 移除并返回队列头部的元素
     * <p>
     * 若队列为空或所有头部元素已取消，抛出{@link NoSuchElementException}。
     * 元素出队时会触发注销事件。
     * 
     * @return 队列头部的元素
     * @throws NoSuchElementException 若队列为空或所有头部元素已取消
     */
    @Override
    public E remove() {
        return update((q) -> {
            if (q == null) {
                throw new NoSuchElementException();
            }

            ElementRegistration<E> registration = q.remove();
            while (registration.isCancelled()) {
                registration = q.remove();
            }
            batchDeregister(Elements.singleton(registration), getPublisher());
            return registration.getPayload();
        });
    }

    /**
     * 移除并返回队列头部的元素，队列为空时返回null
     * <p>
     * 自动跳过已取消的头部元素，若所有头部元素已取消则返回null。
     * 元素出队时会触发注销事件。
     * 
     * @return 队列头部的元素，若为空则返回null
     */
    @Override
    public E poll() {
        return update((q) -> {
            if (q == null) {
                return null;
            }

            ElementRegistration<E> registration = q.poll();
            while (registration != null && registration.isCancelled()) {
                registration = q.poll();
            }

            if (registration == null) {
                return null;
            }

            batchDeregister(Elements.singleton(registration), getPublisher());
            return registration.getPayload();
        });
    }

    /**
     * 返回队列头部的元素但不移除
     * <p>
     * 若队列为空或所有头部元素已取消，抛出{@link NoSuchElementException}。
     * 
     * @return 队列头部的元素
     * @throws NoSuchElementException 若队列为空或所有头部元素已取消
     */
    @Override
    public E element() {
        return read((q) -> {
            if (q == null) {
                throw new NoSuchElementException();
            }

            ElementRegistration<E> registration = q.element();
            while (registration.isCancelled()) {
                registration = q.element();
            }
            return registration.getPayload();
        });
    }

    /**
     * 返回队列头部的元素但不移除，队列为空时返回null
     * <p>
     * 自动跳过已取消的头部元素，若所有头部元素已取消则返回null。
     * 
     * @return 队列头部的元素，若为空则返回null
     */
    @Override
    public E peek() {
        return update((q) -> {
            if (q == null) {
                return null;
            }

            ElementRegistration<E> registration = q.peek();
            while (registration != null && registration.isCancelled()) {
                registration = q.peek();
            }

            if (registration == null) {
                return null;
            }

            return registration.getPayload();
        });
    }
    
    /**
     * 将容器转换为指定类型的数组
     * <p>
     * 继承自{@link CollectionContainer}的数组转换实现。
     * 
     * @param array 目标数组
     * @return 包含容器元素的数组
     */
    @Override
    public <T> T[] toArray(T[] array) {
        return super.toArray(array);
    }
}