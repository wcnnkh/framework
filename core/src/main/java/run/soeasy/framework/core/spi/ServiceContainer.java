package run.soeasy.framework.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.collection.Reloadable;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.LimitableRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.collection.CollectionContainer;
import run.soeasy.framework.core.exchange.container.map.TreeSetContainer;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

/**
 * 服务容器实现，用于管理和注册服务实例，支持服务的自动发现、加载和生命周期管理。
 * <p>
 * 该容器继承自{@link TreeSetContainer}，基于树状集合实现服务的有序存储，并通过比较器确保服务实例的唯一性。
 * 实现了{@link Provider<E>}接口，提供服务实例的访问和管理能力，适用于SPI（Service Provider Interface）场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>服务注册与发现：支持批量注册服务实例，并提供迭代访问接口</li>
 *   <li>动态重新加载：支持服务实例的动态更新和重新加载，保持服务状态一致性</li>
 *   <li>生命周期监听：自动监听服务实例的生命周期事件，触发服务变更通知</li>
 *   <li>线程安全：通过锁机制和原子操作确保多线程环境下的操作安全性</li>
 * </ul>
 *
 * @param <E> 服务实例的类型
 * 
 * @author soeasy.run
 * @see Provider
 * @see TreeSetContainer
 * @see Include
 */
public class ServiceContainer<E> extends TreeSetContainer<E> implements Provider<E> {

    /**
     * 构造函数，初始化服务容器，使用默认服务比较器
     * <p>
     * 默认比较器为{@link ServiceComparator#defaultServiceComparator()}，
     * 确保服务实例按约定顺序排列并去重。
     */
    public ServiceContainer() {
        setComparator(ServiceComparator.defaultServiceComparator());
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    private class InternalInclude extends LimitableRegistration implements Include<E> {
        /** 初始化状态标记，确保初始化操作仅执行一次 */
        private AtomicBoolean initialized = new AtomicBoolean();
        /** 要包含的服务实例迭代器 */
        private final Iterable<? extends E> iterable;
        /** 可重新加载的服务集合，用于动态更新 */
        private volatile Elements<ElementRegistration<E>> registrations;
        /** 重新加载策略提供者 */
        private final Reloadable reloadable;
        /** 注册句柄，用于取消注册 */
        private Registration registration;

        /**
         * 取消包含的服务注册，支持自定义取消条件
         * 
         * @param cancel 取消条件判断函数
         * @return 是否取消成功
         */
        @Override
        public boolean cancel(BooleanSupplier cancel) {
            return super.cancel(() -> {
                Lock lock = writeLock();
                lock.lock();
                try {
                    if (registrations != null) {
                        deregisters(registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()));
                        registrations = null;
                    }

                    if (registration != null) {
                        registration.cancel();
                    }
                } finally {
                    lock.unlock();
                }
                return true;
            });
        }

        /**
         * 重新加载服务实例（强制更新）
         */
        @Override
        public void reload() {
            reload(true);
        }

        /**
         * 重新加载服务实例，支持强制更新
         * 
         * @param force 是否强制重新加载
         */
        public void reload(boolean force) {
            Lock lock = writeLock();
            lock.lock();
            try {
                if (initialized.compareAndSet(false, true) || force) {
                    update();
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * 更新服务实例集合
         * <p>
         * 先触发重新加载策略，再同步服务实例的增删改操作。
         */
        private void update() {
            if (reloadable != null) {
                reloadable.reload();
            }

            List<E> rightList = new ArrayList<>();
            iterable.forEach(rightList::add);

            if (registrations == null) {
                registrations = batchRegister(Elements.of(rightList)).getElements();
            } else {
                List<E> leftList = new ArrayList<>();
                registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).forEach(leftList::add);
                Elements<ElementRegistration<E>> append = ServiceContainer.this.reload(leftList, rightList);
                this.registrations = this.registrations.concat(append).filter((e) -> !e.isCancelled()).toList();
            }
        }

        /**
         * 获取有效服务实例的迭代器
         * 
         * @return 服务实例迭代器（过滤已取消的注册）
         */
        @Override
        public Iterator<E> iterator() {
            return registrations == null ? Collections.emptyIterator()
                    : registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).iterator();
        }
    }

    /** 包含的服务注册集合，用于管理多个Include实例 */
    private CollectionContainer<Include<E>, Collection<ElementRegistration<Include<E>>>> includes = new CollectionContainer<>(
            LinkedHashSet::new);

    /**
     * 比较两个服务实例是否相等
     * <p>
     * 优先使用容器的比较器，若比较器为null则使用对象等值比较。
     * 
     * @param left 左服务实例
     * @param right 右服务实例
     * @return 是否相等
     */
    protected boolean equals(E left, E right) {
        Comparator<? super E> comparator = getComparator();
        if (comparator == null) {
            return ObjectUtils.equals(left, right);
        } else {
            return comparator.compare(left, right) == 0;
        }
    }

    @RequiredArgsConstructor
    private class InternalObserver implements Listener<Lifecycle> {
        /** 被观察的服务实例 */
        private final E element;
        /** 可监听的对象（服务实例） */
        private final Listenable<?> observable;
        /** 注册句柄，用于取消监听 */
        private volatile Registration registration;

        /**
         * 生命周期事件处理
         * <p>
         * 当服务实例启动时注册变更监听，停止时取消监听。
         * 
         * @param source 生命周期事件源
         */
        @Override
        public void accept(Lifecycle source) {
            Lock lock = writeLock();
            lock.lock();
            try {
                if (source.isRunning()) {
                    if (registration != null) {
                        registration.cancel();
                        registration = null;
                    }

                    registration = observable.registerListener((e) -> getPublisher()
                            .publish(Elements.singleton(new ChangeEvent<>(element, ChangeType.UPDATE))));
                } else {
                    if (registration != null) {
                        registration.cancel();
                        registration = null;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 创建新的元素注册实例
     * <p>
     * 若服务实例实现了{@link Listenable}接口，自动注册生命周期观察者。
     * 
     * @param element 服务实例
     * @return 元素注册实例
     */
    @Override
    protected AtomicElementRegistration<E> newElementRegistration(E element) {
        AtomicElementRegistration<E> registration = super.newElementRegistration(element);
        if (element instanceof Listenable) {
            registration.registerListener(new InternalObserver(element, (Listenable<?>) element));
        }
        return registration;
    }

    /**
     * 批量注册服务实例
     * <p>
     * 支持可重新加载的服务集合，返回包含注册信息的Include实例。
     * 
     * @param elements 服务实例集合
     * @return 包含注册信息的Include实例
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public Include<E> registers(Elements<? extends E> elements) throws RegistrationException {
        Reloadable reloadable = null;
        if (elements instanceof Reloadable) {
            reloadable = (Reloadable) elements;
        }
        InternalInclude include = new InternalInclude(elements, reloadable);
        Registration registration = includes.register(include);
        // 初始化一下
        include.reload(false);
        include.setRegistration(registration);
        return include;
    }

    /**
     * 重新加载所有包含的服务实例
     * <p>
     * 触发所有Include实例的重新加载逻辑，保持服务实例最新状态。
     */
    @Override
    public void reload() {
        includes.forEach((e) -> e.reload());
    }

    /**
     * 重新加载服务实例集合
     * <p>
     * 比较新旧服务实例集合，移除过时实例，添加新实例。
     * 
     * @param leftList 已注册的服务实例
     * @param rightList 新服务实例
     * @return 新注册的元素注册集合
     */
    private Elements<ElementRegistration<E>> reload(List<E> leftList, List<E> rightList) {
        Iterator<E> leftIterator = leftList.iterator();
        while (leftIterator.hasNext()) {
            E left = leftIterator.next();
            Iterator<E> rightIterator = rightList.iterator();
            while (rightIterator.hasNext()) {
                E right = rightIterator.next();
                if (ServiceContainer.this.equals(left, right)) {
                    leftIterator.remove();
                    rightIterator.remove();
                }
            }
        }

        // 左边剩下的说明被删除了
        deregisters(Elements.of(leftList));
        return batchRegister(Elements.of(rightList)).getElements();
    }
}