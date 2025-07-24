package run.soeasy.framework.core.exchange.container.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.AbstractContainer;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.ElementRegistrationWrapped;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 集合容器实现，提供基于集合的数据注册与管理功能。
 * <p>
 * 该容器继承自{@link AbstractContainer}并实现{@link Collection}接口，
 * 支持元素的注册、注销、查询等集合操作，同时具备生命周期管理和事件发布能力。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>集合操作支持：完全实现{@link Collection}接口的所有方法</li>
 *   <li>元素注册管理：支持单个元素和批量元素的注册与注销</li>
 *   <li>事件驱动：通过{@link Publisher}发布元素变更事件</li>
 *   <li>生命周期管理：注册元素具备启动、停止等生命周期控制</li>
 *   <li>线程安全：通过读写分离操作保证基本线程安全性</li>
 * </ul>
 *
 * @param <E> 注册元素的类型
 * @param <C> 存储注册元素的集合类型，需继承{@link Collection}&lt;{@link ElementRegistration}&lt;{@link E}&gt;&gt;
 * 
 * @author soeasy.run
 * @see AbstractContainer
 * @see ElementRegistration
 * @see Collection
 */
public class CollectionContainer<E, C extends Collection<ElementRegistration<E>>>
        extends AbstractContainer<C, E, ElementRegistration<E>> implements Collection<E> {
    
    /**
     * 批量注册处理器，用于管理批量注册的生命周期和事件
     */
    @RequiredArgsConstructor
    private class BatchRegistrations implements Registrations<ElementRegistration<E>> {
        private final Elements<UpdateableElementRegistration> registrations;

        /**
         * 取消所有批量注册的元素
         * <p>
         * 过滤未取消的注册并标记为限制状态，触发注销事件
         * 
         * @return 始终返回true（批量取消操作视为成功）
         */
        @Override
        public boolean cancel() {
            Elements<UpdateableElementRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
            elements.forEach((e) -> e.getLimiter().limited());
            batchDeregister(elements, publisher);
            return true;
        }

        /**
         * 获取批量注册的所有元素
         * 
         * @return 注册元素集合
         */
        @Override
        public Elements<ElementRegistration<E>> getElements() {
            return registrations.map((e) -> e);
        }
    }

    /**
     * 可更新的元素注册包装器，增强元素注册的可操作性
     */
    private class UpdateableElementRegistration extends ElementRegistrationWrapped<E, ElementRegistration<E>> {
        public UpdateableElementRegistration(ElementRegistration<E> source) {
            super(source, Elements.empty());
        }

        private UpdateableElementRegistration(ElementRegistrationWrapped<E, ElementRegistration<E>> combinableServiceRegistration) {
            super(combinableServiceRegistration);
        }

        /**
         * 组合当前注册与另一个注册
         * 
         * @param registration 要组合的注册对象
         * @return 新的可更新注册包装器
         */
        @Override
        public UpdateableElementRegistration and(@NonNull Registration registration) {
            return new UpdateableElementRegistration(super.and(registration));
        }

        /**
         * 取消注册并触发删除事件
         * 
         * @param cancel 取消条件提供者
         * @return 取消结果
         */
        @Override
        public boolean cancel(BooleanSupplier cancel) {
            return super.cancel(() -> {
                cleanup();
                publisher.publish(Elements.singleton(new ChangeEvent<E>(getPayload(), ChangeType.DELETE)));
                return true;
            });
        }

        /**
         * 设置元素载荷并触发更新事件
         * 
         * @param payload 新的载荷数据
         * @return 旧的载荷数据
         */
        @Override
        public E setPayload(E payload) {
            E oldValue = super.setPayload(payload);
            publisher.publish(Elements.singleton(new ChangeEvent<>(oldValue, payload)));
            return oldValue;
        }
    }

    private volatile Publisher<? super Elements<ChangeEvent<E>>> publisher = Publisher.ignore();

    /**
     * 构造函数，初始化集合容器
     * 
     * @param containerSource 集合源的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public CollectionContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }

    /**
     * 向容器中添加元素并注册
     * 
     * @param e 要添加的元素
     * @return true表示添加成功，false表示添加失败（通常因注册取消）
     */
    @Override
    public final boolean add(E e) {
        Registration registration = register(e);
        return !registration.isCancelled();
    }

    /**
     * 批量添加元素并注册
     * 
     * @param c 要添加的元素集合
     * @return true表示至少添加一个元素，false表示未添加任何元素
     */
    @Override
    public final boolean addAll(Collection<? extends E> c) {
        Registrations<ElementRegistration<E>> registrations = batchRegister(Elements.of(c), getPublisher());
        return !registrations.getElements().isEmpty();
    }

    /**
     * 批量注销元素并触发删除事件
     * 
     * @param registrations 要注销的注册集合
     * @param publisher 事件发布器
     * @return 注销操作回执
     */
    protected final Receipt batchDeregister(Elements<? extends ElementRegistration<E>> registrations,
            Publisher<? super Elements<ChangeEvent<E>>> publisher) {
        if (registrations.isEmpty()) {
            return Receipt.FAILURE;
        }

        registrations.forEach(Registration::cancel);
        cleanup();
        Elements<ChangeEvent<E>> events = registrations
                .map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.DELETE));
        return publisher.publish(events);
    }

    /**
     * 批量注册元素（使用默认事件发布器）
     * 
     * @param elements 要注册的元素集合
     * @return 批量注册句柄
     * @throws RegistrationException 注册过程中发生异常
     */
    public final Registrations<ElementRegistration<E>> batchRegister(Elements<? extends E> elements)
            throws RegistrationException {
        return batchRegister(elements, this.publisher);
    }

    /**
     * 批量注册元素并指定事件发布器
     * 
     * @param elements 要注册的元素集合
     * @param publisher 事件发布器
     * @return 批量注册句柄
     * @throws RegistrationException 注册过程中发生异常
     */
    public Registrations<ElementRegistration<E>> batchRegister(Elements<? extends E> elements,
            Publisher<? super Elements<ChangeEvent<E>>> publisher) throws RegistrationException {
        return writeRegistrations((collection) -> {
            Elements<ElementRegistration<E>> es = elements.map(this::newElementRegistration);
            for (ElementRegistration<E> registration : es) {
                if (!collection.add(registration)) {
                    registration.cancel();
                }
            }
            return es.toList();
        }, publisher);
    }

    /**
     * 清理注册表，移除已取消的注册
     */
    public void cleanup() {
        execute((members) -> {
            Iterator<ElementRegistration<E>> iterator = members.iterator();
            while (iterator.hasNext()) {
                ElementRegistration<E> registration = iterator.next();
                if (registration.isCancelled()) {
                    iterator.remove();
                }
            }
            return true;
        });
    }

    /**
     * 清除所有注册并触发删除事件
     */
    @Override
    public void clear() {
        getRegistrations().cancel();
    }

    /**
     * 检查容器是否包含指定元素
     * 
     * @param o 要检查的元素
     * @return true表示包含，false表示不包含
     */
    @Override
    public final boolean contains(Object o) {
        return readAsBoolean((collection) -> collection.contains(o));
    }

    /**
     * 检查容器是否包含所有指定元素
     * 
     * @param c 要检查的元素集合
     * @return true表示包含所有，false表示不包含
     */
    @Override
    public final boolean containsAll(Collection<?> c) {
        return readAsBoolean((collection) -> collection == null ? false : collection.containsAll(c));
    }

    /**
     * 注销指定元素并触发删除事件（使用默认发布器）
     * 
     * @param services 要注销的元素集合
     * @return 注销操作回执
     */
    @Override
    public Receipt deregisters(@NonNull Elements<? extends E> services) {
        return deregisters(services, publisher);
    }

    /**
     * 注销指定元素并触发删除事件（指定发布器）
     * 
     * @param services 要注销的元素集合
     * @param publisher 事件发布器
     * @return 注销操作回执
     */
    public Receipt deregisters(Elements<? extends E> services, Publisher<? super Elements<ChangeEvent<E>>> publisher) {
        Collection<? extends E> removes = services.toSet();
        Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
            if (collection == null) {
                return Elements.empty();
            }
            return Elements.of(collection).filter((e) -> removes.contains(e.getPayload()));
        });
        return batchDeregister(registrations, publisher);
    }

    /**
     * 对容器中每个元素执行指定操作
     * 
     * @param action 要执行的操作
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        read((collection) -> {
            if (collection == null) {
                return null;
            }
            collection.forEach((e) -> action.accept(e.getPayload()));
            return null;
        });
    }

    /**
     * 获取所有有效注册元素
     * 
     * @return 有效注册元素集合（过滤已取消的注册）
     */
    @Override
    public Elements<ElementRegistration<E>> getElements() {
        return getRegistrations().getElements().filter((e) -> !e.isCancelled());
    }

    /**
     * 通过函数获取注册载荷
     * 
     * @param getter 载荷获取函数
     * @return 注册载荷，若不存在则返回null
     */
    public final E getPayload(Function<? super C, ? extends PayloadRegistration<E>> getter) {
        return read((container) -> {
            if (container == null) {
                return null;
            }
            PayloadRegistration<E> registration = getter.apply(container);
            return registration == null ? null : registration.getPayload();
        });
    }

    /**
     * 通过函数获取注册对象
     * 
     * @param reader 注册获取函数
     * @return 可更新的注册对象，若不存在则返回null
     */
    public final ElementRegistration<E> getRegistration(Function<? super C, ? extends ElementRegistration<E>> reader) {
        ElementRegistration<E> elementRegistration = read((collection) -> reader.apply(collection));
        if (elementRegistration == null) {
            return null;
        }
        return new UpdateableElementRegistration(elementRegistration);
    }

    /**
     * 获取所有注册句柄
     * 
     * @return 注册句柄集合
     */
    public final Registrations<ElementRegistration<E>> getRegistrations() {
        return getRegistrations((collection) -> collection == null ? Elements.empty() : Elements.of(collection).toList());
    }

    /**
     * 通过自定义函数获取注册句柄
     * 
     * @param reader 注册获取函数
     * @return 注册句柄集合
     */
    public final Registrations<ElementRegistration<E>> getRegistrations(
            Function<? super C, ? extends Elements<ElementRegistration<E>>> reader) {
        Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> reader.apply(collection));
        if (registrations == null || registrations.isEmpty()) {
            return Registrations.empty();
        }
        Elements<UpdateableElementRegistration> updateableRegistrations = registrations
                .map((e) -> new UpdateableElementRegistration(e));
        return new BatchRegistrations(updateableRegistrations);
    }

    /**
     * 检查容器是否为空
     * 
     * @return true表示为空，false表示非空
     */
    @Override
    public boolean isEmpty() {
        return readAsBoolean((collection) -> collection == null ? true : collection.isEmpty());
    }

    /**
     * 获取容器元素迭代器
     * 
     * @return 元素迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return getElements().map((e) -> e.getPayload()).iterator();
    }

    /**
     * 创建新的元素注册实例
     * 
     * @param element 注册元素
     * @return 原子元素注册实例
     */
    protected AtomicElementRegistration<E> newElementRegistration(E element) {
        return new AtomicElementRegistration<>(element);
    }

    /**
     * 注册单个元素（使用默认发布器）
     * 
     * @param element 要注册的元素
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public ElementRegistration<E> register(E element) throws RegistrationException {
        return register(element, publisher);
    }

    /**
     * 注册单个元素并指定发布器
     * 
     * @param element 要注册的元素
     * @param publisher 事件发布器
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    public ElementRegistration<E> register(E element, Publisher<? super Elements<ChangeEvent<E>>> publisher)
            throws RegistrationException {
        return batchRegister(Elements.singleton(element), publisher).getElements().first();
    }

    /**
     * 批量注册元素（使用默认发布器）
     * 
     * @param elements 要注册的元素集合
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public Registration registers(@NonNull Elements<? extends E> elements) throws RegistrationException {
        return batchRegister(elements, publisher);
    }

    /**
     * 自定义批量注册逻辑
     * 
     * @param elements 要注册的元素集合
     * @param register 注册处理函数
     * @param publisher 事件发布器
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements,
            BiConsumer<? super C, ? super Elements<ElementRegistration<E>>> register,
            Publisher<? super Elements<ChangeEvent<E>>> publisher) throws RegistrationException {
        Elements<ElementRegistration<E>> es = Elements.of(elements).map(this::newElementRegistration);
        return writeRegistrations((collection) -> {
            register.accept(collection, es);
            return es.toList();
        }, publisher);
    }

    /**
     * 从容器中移除指定元素
     * 
     * @param o 要移除的元素
     * @return true表示移除成功，false表示未移除
     */
    @Override
    public final boolean remove(Object o) {
        Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
            if (collection == null) {
                return Elements.empty();
            }
            return Elements.of(collection).filter((e) -> ObjectUtils.equals(e.getPayload(), o));
        });
        return batchDeregister(registrations, getPublisher()).isSuccess();
    }

    /**
     * 从容器中移除所有指定元素
     * 
     * @param c 要移除的元素集合
     * @return true表示至少移除一个元素，false表示未移除
     */
    @Override
    public final boolean removeAll(Collection<?> c) {
        Elements<ElementRegistration<E>> registrations = readAsElements((collection) -> {
            if (collection == null) {
                return Elements.empty();
            }
            return Elements.of(collection).filter((e) -> c.contains(e.getPayload()));
        });
        return batchDeregister(registrations, getPublisher()).isSuccess();
    }

    /**
     * 保留容器中与指定集合共有的元素
     * 
     * @param c 保留的元素集合
     * @return true表示容器被修改，false表示未修改
     */
    @Override
    public final boolean retainAll(Collection<?> c) {
        return readAsBoolean((collection) -> collection == null ? false : collection.retainAll(c));
    }

    /**
     * 获取容器中元素数量
     * 
     * @return 元素数量
     */
    @Override
    public final int size() {
        return readAsInt((collection) -> collection == null ? 0 : collection.size());
    }

    /**
     * 获取元素流
     * 
     * @return 元素流
     */
    @Override
    public Stream<E> stream() {
        return getElements().map((e) -> e.getPayload()).stream();
    }

    /**
     * 将容器转换为对象数组
     * 
     * @return 对象数组
     */
    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    /**
     * 将容器转换为指定类型的数组
     * 
     * @param array 目标数组
     * @return 包含容器元素的数组
     */
    @Override
    public <T> T[] toArray(T[] array) {
        return super.toArray(array);
    }
    
    /**
     * 将容器转换为指定类型的数组（使用生成器函数）
     * 
     * @param generator 数组生成器函数
     * @return 包含容器元素的数组
     */
    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return super.toArray(generator);
    }

    /**
     * 执行写操作并返回注册句柄
     * 
     * @param writer 写操作函数
     * @param publisher 事件发布器
     * @return 注册句柄
     */
    private final Registrations<ElementRegistration<E>> writeRegistrations(
            Function<? super C, ? extends Elements<ElementRegistration<E>>> writer,
            Publisher<? super Elements<ChangeEvent<E>>> publisher) {
        Elements<ElementRegistration<E>> registrations = write(writer).filter((e) -> !e.isCancelled()).toList();
        registrations.forEach((e) -> e.start());
        Elements<ChangeEvent<E>> events = registrations
                .map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.CREATE));
        publisher.publish(events);
        return getRegistrations((collection) -> registrations);
    }
}