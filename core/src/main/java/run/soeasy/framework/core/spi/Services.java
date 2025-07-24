package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.locks.Lock;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.MergedElements;
import run.soeasy.framework.core.collection.ReloadableElementsWrapper;
import run.soeasy.framework.core.comparator.OrderWrapped;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Receipts;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.Container;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

/**
 * 服务管理容器，提供服务的注册、发现、排序和注入功能。
 * <p>
 * 该容器实现了{@link Container}、{@link ReloadableElementsWrapper}和{@link ServiceInjector}接口，
 * 支持服务实例的动态管理、按顺序排序和依赖注入，适用于SPI（Service Provider Interface）场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>服务注册与发现：支持按顺序注册服务实例，提供统一的服务访问入口</li>
 *   <li>动态排序：通过{@link OrderWrapped}为服务实例添加顺序优先级</li>
 *   <li>依赖注入：集成服务注入器，支持服务实例的自动依赖注入</li>
 *   <li>事件通知：服务变更时发布{@link ChangeEvent}事件</li>
 *   <li>可重新加载：支持服务实例的动态重新加载和更新</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see ServiceContainer
 * @see ServiceInjector
 * @see OrderWrapped
 */
public class Services<S>
        implements Container<S, PayloadRegistration<S>>, ReloadableElementsWrapper<S, Elements<S>>, ServiceInjector<S> {
    
    @RequiredArgsConstructor
    private class InjectListener implements Listener<Lifecycle> {
        /** 带顺序的服务包装器 */
        private final OrderWrapped<S> holder;
        /** 注册句柄，用于取消监听 */
        private volatile Registration registration;

        /**
         * 生命周期事件处理
         * <p>
         * 当服务实例启动时执行注入，停止时取消注入。
         * 
         * @param source 生命周期事件源
         */
        @Override
        public void accept(Lifecycle source) {
            Lock lock = container.writeLock();
            lock.lock();
            try {
                if (source.isRunning()) {
                    if (registration != null) {
                        registration.cancel();
                    }
                    registration = inject(holder.getSource());
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

    /** 实际存储服务的容器，存储带顺序的服务包装器 */
    private final ServiceContainer<OrderWrapped<S>> container;
    
    /** 默认服务顺序优先级 */
    private int defaultOrder = 0;
    
    /** 第一个服务实例（优先级最高） */
    private volatile S first;
    
    /** 服务注入器集合，管理服务的依赖注入 */
    private final ServiceInjectors<S> injectors = new ServiceInjectors<>();
    
    /** 最后一个服务实例（优先级最低） */
    private volatile S last;
    
    /** 服务变更事件发布者 */
    private volatile Publisher<? super Elements<ChangeEvent<S>>> publisher = Publisher.ignore();

    /**
     * 构造函数，初始化服务容器
     * <p>
     * 初始化时创建{@link ServiceContainer}实例，并设置服务注册的事件监听。
     */
    public Services() {
        container = new ServiceContainer<OrderWrapped<S>>() {
            @Override
            protected AtomicElementRegistration<OrderWrapped<S>> newElementRegistration(OrderWrapped<S> element) {
                AtomicElementRegistration<OrderWrapped<S>> registration = super.newElementRegistration(element);
                registration.registerListener(new InjectListener(element));
                return registration;
            }
        };
        container.setPublisher((events) -> publisher.publish(events.map((e) -> e.convert((s) -> s.getSource()))));
        injectors.setPublisher(this::onServiceInjectorEvents);
    }

    /**
     * 注销服务实例
     * 
     * @param elements 要注销的服务实例集合
     * @return 注销结果收据
     */
    @Override
    public Receipt deregisters(Elements<? extends S> elements) {
        Elements<Receipt> receipts = container.getElements()
                .filter((e) -> elements.contains(e.getPayload().getSource()))
                .map((e) -> e.cancel() ? Receipt.SUCCESS : Receipt.FAILURE).toList();
        return Receipts.of(receipts);
    }

    /**
     * 获取默认服务顺序优先级
     * 
     * @return 默认顺序优先级
     */
    public int getDefaultOrder() {
        return defaultOrder;
    }

    /**
     * 获取所有服务的注册信息
     * 
     * @return 服务注册信息集合
     */
    @Override
    public Elements<PayloadRegistration<S>> getElements() {
        return container.getElements().map((e) -> e.map((h) -> h.getSource()));
    }

    /**
     * 获取第一个服务实例（优先级最高）
     * 
     * @return 第一个服务实例，若无则返回null
     */
    public S getFirst() {
        return first;
    }

    /**
     * 获取服务注入器集合
     * 
     * @return 服务注入器集合
     */
    public ServiceInjectors<S> getInjectors() {
        return injectors;
    }

    /**
     * 获取最后一个服务实例（优先级最低）
     * 
     * @return 最后一个服务实例，若无则返回null
     */
    public S getLast() {
        return last;
    }

    /**
     * 获取服务变更事件发布者
     * 
     * @return 事件发布者
     */
    public Publisher<? super Elements<ChangeEvent<S>>> getPublisher() {
        Lock lock = container.readLock();
        lock.lock();
        try {
            return publisher;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取实际存储服务的容器
     * 
     * @return 服务容器实例
     */
    public ServiceContainer<OrderWrapped<S>> getContainer() {
        return container;
    }

    /**
     * 获取所有服务实例的合并集合（包括首尾服务）
     * 
     * @return 服务实例集合（包含first、中间服务、last）
     */
    @Override
    public Elements<S> getSource() {
        Lock lock = container.readLock();
        lock.lock();
        try {
            Elements<S> firstSingletonElements = first == null ? Elements.empty() : Elements.singleton(first);
            Elements<S> lastSingletonElements = last == null ? Elements.empty() : Elements.singleton(last);
            return new MergedElements<>(firstSingletonElements, container.map((e) -> e.getSource()),
                    lastSingletonElements);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 注入服务实例
     * 
     * @param service 要注入的服务实例
     * @return 注册句柄
     */
    @Override
    public Registration inject(S service) {
        return injectors.inject(service);
    }

    /**
     * 判断容器是否为空
     * 
     * @return true表示容器为空，false表示非空
     */
    @Override
    public boolean isEmpty() {
        return getSource().isEmpty();
    }

    /**
     * 处理服务注入器事件
     * 
     * @param events 服务注入器变更事件集合
     * @return 处理结果收据
     */
    protected Receipt onServiceInjectorEvents(Elements<ChangeEvent<ServiceInjector<? super S>>> events) {
        for (ChangeEvent<ServiceInjector<? super S>> event : events) {
            forEach((service) -> {
                if (event.getChangeType() != ChangeType.CREATE) {
                    return;
                }
                event.getSource().inject(service);
            });
        }
        return Receipt.SUCCESS;
    }

    /**
     * 按指定顺序注册服务实例
     * 
     * @param order 服务顺序优先级
     * @param element 服务实例
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    public Registration register(int order, S element) throws RegistrationException {
        return container.register(new OrderWrapped<>(element, order));
    }

    /**
     * 按默认顺序注册服务实例
     * 
     * @param element 服务实例
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public Registration register(S element) throws RegistrationException {
        return register(getDefaultOrder(), element);
    }

    /**
     * 按默认顺序批量注册服务实例
     * 
     * @param elements 服务实例集合
     * @return 包含注册信息的Include实例
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public Include<OrderWrapped<S>> registers(Elements<? extends S> elements) throws RegistrationException {
        return registers(getDefaultOrder(), elements);
    }

    /**
     * 按指定顺序批量注册服务实例
     * 
     * @param order 服务顺序优先级
     * @param elements 服务实例集合
     * @return 包含注册信息的Include实例
     */
    public Include<OrderWrapped<S>> registers(int order, Elements<? extends S> elements) {
        Elements<OrderWrapped<S>> holders = elements.map((e) -> new OrderWrapped<>(e, order));
        return container.registers(holders);
    }

    /**
     * 重新加载所有服务实例
     * <p>
     * 触发服务容器的重新加载逻辑，更新服务实例状态。
     */
    @Override
    public void reload() {
        container.reload();
    }

    /**
     * 设置服务比较器
     * 
     * @param comparator 服务比较器
     */
    public void setComparator(Comparator<? super S> comparator) {
        container.setComparator((h1, h2) -> {
            int v = Integer.compare(h1.getOrder(), h2.getOrder());
            if (v == 0) {
                v = comparator.compare(h1.getSource(), h2.getSource());
                if (v == 0) {
                    if (!ObjectUtils.equals(h1.getSource(), h2.getSource())) {
                        v = 1;
                    }
                }
            }
            return v;
        });
    }

    /**
     * 设置默认服务顺序优先级
     * 
     * @param defaultOrder 默认顺序优先级
     */
    public void setDefaultOrder(int defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    /**
     * 设置第一个服务实例（优先级最高）
     * 
     * @param first 第一个服务实例
     */
    public void setFirst(S first) {
        Lock lock = container.writeLock();
        lock.lock();
        try {
            ChangeEvent<S> event = new ChangeEvent<>(this.first, first);
            this.first = first;
            publisher.publish(Elements.singleton(event));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置最后一个服务实例（优先级最低）
     * 
     * @param last 最后一个服务实例
     */
    public void setLast(S last) {
        Lock lock = container.writeLock();
        lock.lock();
        try {
            ChangeEvent<S> event = new ChangeEvent<>(this.last, last);
            this.last = last;
            publisher.publish(Elements.singleton(event));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置服务变更事件发布者
     * 
     * @param publisher 事件发布者，null时使用忽略发布者
     */
    public void setPublisher(Publisher<? super Elements<ChangeEvent<S>>> publisher) {
        Lock lock = container.writeLock();
        lock.lock();
        try {
            this.publisher = publisher == null ? Publisher.ignore() : publisher;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 返回服务集合的字符串表示
     * 
     * @return 服务集合的字符串表示
     */
    @Override
    public String toString() {
        return getSource().toString();
    }
}