package run.soeasy.framework.core.exchange.container.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.AbstractContainer;
import run.soeasy.framework.core.exchange.container.AtomicEntryRegistration;
import run.soeasy.framework.core.exchange.container.EntryRegistration;
import run.soeasy.framework.core.exchange.container.KeyValueRegistry;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.EntryRegistrationWrapped;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 映射容器实现，基于Map数据结构提供键值对注册与管理功能。
 * <p>
 * 该容器继承自{@link AbstractContainer}，实现了{@link KeyValueRegistry}和{@link Map}接口，
 * 支持键值对的注册、注销、查询等操作，同时具备生命周期管理和事件发布能力。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>Map操作支持：完全实现{@link Map}接口的所有方法</li>
 *   <li>注册生命周期管理：自动处理键值对注册的启动、停止和取消</li>
 *   <li>事件驱动：键值对变更时自动发布{@link ChangeEvent}事件</li>
 *   <li>批量操作：支持批量注册、注销键值对</li>
 *   <li>类型安全：通过泛型确保键值对类型一致性</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <M> 存储注册的Map类型，需继承{@link Map}<{@link K}, {@link EntryRegistration}<{@link K}, {@link V}>>
 * 
 * @author soeasy.run
 * @see KeyValueRegistry
 * @see Map
 * @see AbstractContainer
 */
@Getter
public class MapContainer<K, V, M extends Map<K, EntryRegistration<K, V>>> extends
        AbstractContainer<M, KeyValue<K, V>, EntryRegistration<K, V>> implements KeyValueRegistry<K, V>, Map<K, V> {
    
    /**
     * 批量注册处理器，用于管理批量注册的生命周期和事件
     */
    @RequiredArgsConstructor
    private class BatchRegistrations implements Registrations<EntryRegistration<K, V>> {
        private final Elements<UpdateableEntryRegistration> registrations;

        /**
         * 取消所有批量注册的键值对
         * <p>
         * 过滤未取消的注册并标记为限制状态，触发注销事件
         * 
         * @return 始终返回true（批量取消操作视为成功）
         */
        @Override
        public boolean cancel() {
            Elements<UpdateableEntryRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
            elements.forEach((e) -> e.getLimiter().limited());
            batchDeregister(elements);
            return true;
        }

        /**
         * 获取批量注册的所有键值对注册
         * 
         * @return 注册集合
         */
        @Override
        public Elements<EntryRegistration<K, V>> getElements() {
            return registrations.map((e) -> e);
        }
    }

    /**
     * 变更信息封装类，用于记录变更事件和对应的注册
     */
    @RequiredArgsConstructor
    private static class ChangeInfo<K, V> {
        private final ChangeEvent<KeyValue<K, V>> event;
        private final EntryRegistration<K, V> registration;
    }

    /**
     * 值集合的内部实现类
     */
    private class InternalCollection extends AbstractCollection<V> {
        /**
         * 获取值的迭代器
         * 
         * @return 值的迭代器
         */
        @Override
        public Iterator<V> iterator() {
            return read((map) -> map == null ? Collections.emptyIterator()
                    : map.values().stream().map((e) -> e.getValue()).collect(Collectors.toList()).iterator());
        }

        /**
         * 获取值集合的大小
         * 
         * @return 值的数量
         */
        @Override
        public int size() {
            return MapContainer.this.size();
        }
    }

    /**
     * 键值对集合的内部实现类
     */
    private class InternalEntrySet extends AbstractSet<Entry<K, V>> {
        /**
         * 添加单个键值对
         * 
         * @param e 要添加的键值对
         * @return true表示添加成功
         */
        @Override
        public boolean add(Entry<K, V> e) {
            return addAll(Arrays.asList(e));
        }

        /**
         * 批量添加键值对
         * 
         * @param c 要添加的键值对集合
         * @return true表示至少添加一个
         */
        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            return MapContainer.this.addAll(c);
        }

        /**
         * 获取键值对迭代器
         * 
         * @return 键值对迭代器
         */
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return read((map) -> map == null ? Collections.emptyIterator()
                    : map.values().stream().map((e) -> (Entry<K, V>) e).collect(Collectors.toList()).iterator());
        }

        /**
         * 获取键值对集合的大小
         * 
         * @return 键值对数量
         */
        @Override
        public int size() {
            return MapContainer.this.size();
        }
    }

    /**
     * 键集合的内部实现类
     */
    private class InternalSet extends AbstractSet<K> {
        /**
         * 不支持添加键
         * 
         * @param e 要添加的键
         * @throws UnsupportedOperationException 始终抛出不支持操作异常
         */
        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException();
        }

        /**
         * 不支持批量添加键
         * 
         * @param c 要添加的键集合
         * @throws UnsupportedOperationException 始终抛出不支持操作异常
         */
        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        /**
         * 遍历键集合
         * 
         * @param action 遍历操作
         */
        @Override
        public void forEach(Consumer<? super K> action) {
            read((map) -> {
                if (map == null) {
                    return null;
                }
                map.keySet().forEach(action);
                return null;
            });
        }

        /**
         * 获取键的迭代器
         * 
         * @return 键的迭代器
         */
        @Override
        public Iterator<K> iterator() {
            return read((map) -> map == null ? Collections.emptyIterator()
                    : map.keySet().stream().collect(Collectors.toList()).iterator());
        }

        /**
         * 获取键集合的大小
         * 
         * @return 键的数量
         */
        @Override
        public int size() {
            return MapContainer.this.size();
        }
    }

    /**
     * 可更新的条目注册包装器，增强注册的可操作性
     */
    private class UpdateableEntryRegistration extends EntryRegistrationWrapped<K, V, EntryRegistration<K, V>> {
        /**
         * 构造函数，包装源注册对象
         * 
         * @param source 源注册对象
         */
        public UpdateableEntryRegistration(EntryRegistration<K, V> source) {
            super(source, Elements.empty());
        }

        private UpdateableEntryRegistration(EntryRegistrationWrapped<K, V, EntryRegistration<K, V>> combinableServiceRegistration) {
            super(combinableServiceRegistration);
        }

        /**
         * 组合当前注册与另一个注册
         * 
         * @param registration 要组合的注册
         * @return 新的可更新注册包装器
         */
        @Override
        public UpdateableEntryRegistration and(@NonNull Registration registration) {
            return new UpdateableEntryRegistration(super.and(registration));
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
                update((map) -> map == null ? null : map.remove(getKey(), getValue()));
                getPublisher().publish(Elements.singleton(new ChangeEvent<>(this, ChangeType.DELETE)));
                return true;
            });
        }

        /**
         * 更新值并触发更新事件
         * 
         * @param value 新值
         * @return 旧值
         */
        @Override
        public V setValue(V value) {
            V oldValue = super.setValue(value);
            KeyValue<K, V> oldKeyValue = oldValue == null ? null : KeyValue.of(getKey(), oldValue);
            getPublisher().publish(Elements.singleton(new ChangeEvent<>(oldKeyValue, KeyValue.of(getKey(), value))));
            return oldValue;
        }
    }

    /**
     * 构造函数，初始化映射容器
     * 
     * @param containerSource Map实例的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public MapContainer(@NonNull ThrowingSupplier<? extends M, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }

    /**
     * 批量添加键值对并注册
     * 
     * @param c 要添加的键值对集合
     * @return true表示至少添加一个键值对
     */
    public final boolean addAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
        return !registers(Elements.of(c).map((e) -> KeyValue.of(e.getKey(), e.getValue()))).isCancelled();
    }

    /**
     * 批量注销键值对并触发删除事件
     * 
     * @param registrations 要注销的注册集合
     * @return 注销操作回执
     */
    protected final Receipt batchDeregister(Elements<? extends EntryRegistration<K, V>> registrations) {
        if (registrations.isEmpty()) {
            return Receipt.FAILURE;
        }

        registrations.forEach(Registration::cancel);
        execute((map) -> {
            registrations.forEach((e) -> map.remove(e.getKey(), e.getValue()));
            return true;
        });
        Elements<ChangeEvent<KeyValue<K, V>>> events = registrations
                .map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
        return getPublisher().publish(events);
    }

    /**
     * 批量注册键值对
     * 
     * @param elements 要注册的键值对集合
     * @return 批量注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    public final Registrations<EntryRegistration<K, V>> batchRegister(Elements<? extends KeyValue<K, V>> elements)
            throws RegistrationException {
        Elements<ChangeInfo<K, V>> changes = write((map) -> {
            return elements.map((keyValue) -> {
                EntryRegistration<K, V> registration = newRegistration(keyValue);
                EntryRegistration<K, V> old = map.put(keyValue.getKey(), registration);
                ChangeEvent<KeyValue<K, V>> event;
                if (old == null) {
                    event = new ChangeEvent<KeyValue<K, V>>(keyValue, ChangeType.CREATE);
                } else {
                    event = new ChangeEvent<>(old, keyValue);
                }
                return new ChangeInfo<K, V>(event, registration);
            }).toList();
        });
        getPublisher().publish(changes.map((e) -> e.event).toList());
        return convertToBatchRegistration(changes.map((e) -> e.registration).toList());
    }

    /**
     * 清除所有注册
     */
    @Override
    public final void clear() {
        getRegistrations().cancel();
    }

    /**
     * 检查是否包含指定键
     * 
     * @param key 要检查的键
     * @return true表示包含
     */
    @Override
    public final boolean containsKey(Object key) {
        return readAsBoolean((map) -> map == null ? false : map.containsKey(key));
    }

    /**
     * 检查是否包含指定值
     * 
     * @param value 要检查的值
     * @return true表示包含
     */
    @Override
    public final boolean containsValue(Object value) {
        return readAsBoolean((map) -> map == null ? false : map.containsValue(value));
    }

    /**
     * 将注册集合转换为批量注册句柄
     * 
     * @param registrations 注册集合
     * @return 批量注册句柄
     */
    private final Registrations<EntryRegistration<K, V>> convertToBatchRegistration(
            Elements<EntryRegistration<K, V>> registrations) {
        if (registrations == null || registrations.isEmpty()) {
            return Registrations.empty();
        }
        Elements<UpdateableEntryRegistration> updateableRegistrations = registrations
                .map((e) -> new UpdateableEntryRegistration(e));
        return new BatchRegistrations(updateableRegistrations);
    }

    /**
     * 注销指定键的注册
     * 
     * @param keys 要注销的键集合
     * @return 注销操作回执
     */
    @Override
    public Receipt deregisterKeys(Iterable<? extends K> keys) {
        return update((map) -> {
            if (map == null) {
                return Receipt.FAILURE;
            }
            List<EntryRegistration<K, V>> removes = new ArrayList<>();
            for (K key : keys) {
                EntryRegistration<K, V> registration = map.remove(key);
                if (registration != null) {
                    removes.add(registration);
                }
            }
            if (removes.isEmpty()) {
                return Receipt.FAILURE;
            }
            return batchDeregister(Elements.of(removes));
        });
    }

    /**
     * 注销指定键值对的注册
     * 
     * @param services 要注销的键值对集合
     * @return 注销操作回执
     */
    @Override
    public Receipt deregisters(Elements<? extends KeyValue<K, V>> services) {
        return update((map) -> {
            if (map == null) {
                return Receipt.FAILURE;
            }
            List<EntryRegistration<K, V>> removes = new ArrayList<>();
            for (KeyValue<K, V> kv : services) {
                if (!map.containsKey(kv.getKey())) {
                    continue;
                }
                EntryRegistration<K, V> registration = map.get(kv.getKey());
                if (registration == null || !ObjectUtils.equals(kv.getValue(), registration.getValue())) {
                    continue;
                }
                registration = map.remove(kv.getKey());
                if (registration != null) {
                    removes.add(registration);
                }
            }
            if (removes.isEmpty()) {
                return Receipt.FAILURE;
            }
            return batchDeregister(Elements.of(removes));
        });
    }

    /**
     * 获取键值对集合
     * 
     * @return 键值对集合
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new InternalEntrySet();
    }

    /**
     * 遍历所有键值对
     * 
     * @param action 遍历操作
     */
    @Override
    public void forEach(Consumer<? super KeyValue<K, V>> action) {
        read((map) -> {
            if (map == null) {
                return null;
            }
            map.entrySet().stream().map((e) -> e.getValue()).forEach(action);
            return null;
        });
    }

    /**
     * 获取指定键对应的值
     * 
     * @param key 键
     * @return 对应的值，若不存在则返回null
     */
    @Override
    public V get(Object key) {
        return getValue((map) -> map.get(key));
    }

    /**
     * 获取所有有效注册
     * 
     * @return 有效注册集合
     */
    @Override
    public Elements<EntryRegistration<K, V>> getElements() {
        return getRegistrations().getElements();
    }

    /**
     * 通过函数获取键值对条目
     * 
     * @param getter 条目获取函数
     * @return 键值对条目
     */
    public final Entry<K, V> getEntry(
            @NonNull Function<? super M, ? extends Entry<K, EntryRegistration<K, V>>> getter) {
        return read((map) -> {
            if (map == null) {
                return null;
            }
            Entry<K, EntryRegistration<K, V>> entry = getter.apply(map);
            if (entry == null) {
                return null;
            }
            return entry.getValue();
        });
    }

    /**
     * 通过函数获取注册对象
     * 
     * @param reader 注册获取函数
     * @return 可更新的注册对象
     */
    public final EntryRegistration<K, V> getRegistration(
            @NonNull Function<? super M, ? extends EntryRegistration<K, V>> reader) {
        EntryRegistration<K, V> registration = read(reader);
        if (registration == null) {
            return null;
        }
        return new UpdateableEntryRegistration(registration);
    }

    /**
     * 获取所有注册句柄
     * 
     * @return 注册句柄集合
     */
    public Registrations<EntryRegistration<K, V>> getRegistrations() {
        return getRegistrations((map) -> Elements.of(map.values()));
    }

    /**
     * 通过自定义函数获取注册句柄
     * 
     * @param reader 注册获取函数
     * @return 注册句柄集合
     */
    public final Registrations<EntryRegistration<K, V>> getRegistrations(
            Function<? super M, ? extends Elements<EntryRegistration<K, V>>> reader) {
        Elements<EntryRegistration<K, V>> registrations = readAsElements(
                (collection) -> collection == null ? Elements.empty() : reader.apply(collection));
        return convertToBatchRegistration(registrations);
    }

    /**
     * 通过函数获取值
     * 
     * @param getter 值获取函数
     * @return 值
     */
    public final V getValue(Function<? super M, ? extends EntryRegistration<K, V>> getter) {
        return read((map) -> {
            if (map == null) {
                return null;
            }
            EntryRegistration<K, V> entryRegistration = getter.apply(map);
            return entryRegistration == null ? null : entryRegistration.getValue();
        });
    }

    /**
     * 检查是否包含指定键（别名方法）
     * 
     * @param key 键
     * @return true表示包含
     */
    @Override
    public boolean hasKey(K key) {
        return this.containsKey(key);
    }

    /**
     * 检查容器是否为空
     * 
     * @return true表示为空
     */
    @Override
    public boolean isEmpty() {
        return readAsBoolean((map) -> map == null ? true : map.isEmpty());
    }

    /**
     * 获取键值对迭代器
     * 
     * @return 键值对迭代器
     */
    @Override
    public final Iterator<KeyValue<K, V>> iterator() {
        return entries().iterator();
    }

    /**
     * 获取键集合
     * 
     * @return 键集合
     */
    @Override
    public final Set<K> keySet() {
        return new InternalSet();
    }

    /**
     * 创建新的条目注册实例
     * 
     * @param keyValue 键值对
     * @return 原子条目注册实例
     */
    protected final AtomicEntryRegistration<K, V> newRegistration(KeyValue<K, V> keyValue) {
        return new AtomicEntryRegistration<>(keyValue.getKey(), keyValue.getValue());
    }

    /**
     * 添加或更新键值对
     * 
     * @param key 键
     * @param value 值
     * @return 旧值，若不存在则返回null
     */
    @Override
    public final V put(K key, V value) {
        KeyValue<K, V> keyValue = KeyValue.of(key, value);
        EntryRegistration<K, V> registration = newRegistration(keyValue);
        return write((map) -> {
            EntryRegistration<K, V> old = map.put(key, registration);
            if (old == null) {
                getPublisher().publish(Elements.singleton(new ChangeEvent<>(keyValue, ChangeType.CREATE)));
            } else {
                getPublisher().publish(Elements.singleton(new ChangeEvent<>(old, keyValue)));
            }
            return old == null ? null : old.getValue();
        });
    }

    /**
     * 批量添加键值对
     * 
     * @param m 要添加的Map
     */
    @Override
    public final void putAll(Map<? extends K, ? extends V> m) {
        addAll(m.entrySet());
    }

    /**
     * 批量注册键值对
     * 
     * @param elements 要注册的键值对集合
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public Registration registers(Elements<? extends KeyValue<K, V>> elements) throws RegistrationException {
        return batchRegister(elements);
    }

    /**
     * 移除指定键的键值对
     * 
     * @param key 键
     * @return 移除的值，若不存在则返回null
     */
    @Override
    public final V remove(Object key) {
        return update((map) -> {
            if (map == null) {
                return null;
            }
            EntryRegistration<K, V> registration = map.remove(key);
            if (registration == null) {
                return null;
            }
            registration.cancel();
            getPublisher()
                    .publish(Elements.singleton(new ChangeEvent<KeyValue<K, V>>(registration, ChangeType.DELETE)));
            return registration.getValue();
        });
    }

    /**
     * 获取键值对数量
     * 
     * @return 键值对数量
     */
    @Override
    public final int size() {
        return readAsInt((map) -> map == null ? 0 : map.size());
    }

    /**
     * 获取键值对流
     * 
     * @return 键值对流
     */
    @Override
    public final Stream<KeyValue<K, V>> stream() {
        return entries().stream();
    }

    /**
     * 获取所有键值对
     * 
     * @return 键值对集合
     */
    public Elements<KeyValue<K, V>> entries() {
        return readAsElements((map) -> {
            if (map == null) {
                return Elements.empty();
            }
            return Elements.of(() -> map.entrySet().stream().map((e) -> e.getValue()));
        });
    }

    /**
     * 获取值集合
     * 
     * @return 值集合
     */
    @Override
    public final Collection<V> values() {
        return new InternalCollection();
    }
}