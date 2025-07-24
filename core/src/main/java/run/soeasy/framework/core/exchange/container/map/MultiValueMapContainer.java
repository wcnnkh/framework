package run.soeasy.framework.core.exchange.container.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.concurrent.AtomicEntry;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Receipts;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AbstractContainer;
import run.soeasy.framework.core.exchange.container.Container;
import run.soeasy.framework.core.exchange.container.KeyValueRegistration;
import run.soeasy.framework.core.exchange.container.KeyValueRegistry;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 多值映射容器实现，支持一个键对应多个值的注册管理。
 * <p>
 * 该容器继承自{@link AbstractContainer}，实现了{@link MultiValueMap}和{@link KeyValueRegistry}接口，
 * 允许通过键注册多个值，并提供生命周期管理和事件驱动能力。每个键对应一个子容器，用于管理该键的所有值注册。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>多值映射：支持一个键对应多个值的存储与管理</li>
 *   <li>注册生命周期：每个值注册具有独立的启动、停止和取消状态</li>
 *   <li>事件驱动：值的注册、注销操作会触发相应的变更事件</li>
 *   <li>批量操作：支持批量注册、注销键值对</li>
 *   <li>类型安全：通过泛型确保键值对及注册类型的一致性</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <R> 值注册的类型，需继承{@link PayloadRegistration}&lt;{@link V}&gt;
 * @param <VC> 值容器的类型，需继承{@link Container}&lt;{@link V}, {@link R}&gt;
 * @param <M> 底层映射的类型，需继承{@link Map}&lt;{@link K}, {@link VC}&gt;
 * 
 * @author soeasy.run
 * @see MultiValueMap
 * @see KeyValueRegistry
 * @see AbstractContainer
 */
public class MultiValueMapContainer<K, V, R extends PayloadRegistration<V>, VC extends Container<V, R>, M extends Map<K, VC>>
        extends AbstractContainer<M, KeyValue<K, V>, KeyValueRegistration<K, V>>
        implements MultiValueMap<K, V>, KeyValueRegistry<K, V> {
    
    /** 用于创建值容器的函数 */
    private final Function<? super K, ? extends VC> valuesCreator;

    /**
     * 构造函数，初始化多值映射容器
     * <p>
     * 通过供给函数创建底层映射，并指定值容器的创建方式。
     * 
     * @param containerSource 底层映射的供给函数，不可为null
     * @param valuesCreator 值容器的创建函数，不可为null
     * @throws NullPointerException 若参数为null
     */
    public MultiValueMapContainer(@NonNull ThrowingSupplier<? extends M, ? extends RuntimeException> containerSource,
            @NonNull Function<? super K, ? extends VC> valuesCreator) {
        super(containerSource);
        this.valuesCreator = valuesCreator;
    }

    /**
     * 创建新的值容器
     * 
     * @param key 键，用于创建对应的值容器
     * @return 新的值容器实例
     */
    protected VC newValues(K key) {
        return valuesCreator.apply(key);
    }

    /**
     * 向指定键添加多个值
     * <p>
     * 若键不存在则创建新的容器，然后批量注册值。
     * 
     * @param key 目标键
     * @param values 要添加的值列表
     */
    @Override
    public final void adds(K key, List<V> values) {
        write((map) -> {
            VC services = map.get(key);
            if (services == null) {
                services = newValues(key);
                map.put(key, services);
            }
            services.registers(Elements.of(values));
            return null;
        });
    }

    /**
     * 清除所有注册
     * <p>
     * 清空底层映射并重置所有值容器。
     */
    @Override
    public final void clear() {
        update((e) -> {
            if (e == null) {
                return null;
            }
            reset();
            e.clear();
            return null;
        });
    }

    /**
     * 检查是否包含指定键
     * 
     * @param key 要检查的键
     * @return true表示包含，false表示不包含
     */
    @Override
    public boolean containsKey(Object key) {
        return readAsBoolean((map) -> map == null ? false : map.containsKey(key));
    }

    /**
     * 检查是否包含指定值
     * <p>
     * 检查任意键对应的值容器中是否包含该值。
     * 
     * @param value 要检查的值
     * @return true表示包含，false表示不包含
     */
    @Override
    public final boolean containsValue(Object value) {
        return readAsBoolean((map) -> map == null ? false : map.containsValue(value));
    }

    /**
     * 注销指定键的值
     * 
     * @param key 键
     * @param value 值
     * @return 注销操作回执
     * @throws RegistrationException 注销过程中发生异常
     */
    public final Receipt deregister(K key, V value) throws RegistrationException {
        return update((map) -> {
            if (map == null) {
                return Receipt.FAILURE;
            }
            VC vc = map.get(key);
            if (vc == null) {
                return Receipt.FAILURE;
            }
            return vc.deregister(value);
        });
    }

    /**
     * 注销键值对
     * 
     * @param element 要注销的键值对
     * @return 注销操作回执
     * @throws RegistrationException 注销过程中发生异常
     */
    @Override
    public final Receipt deregister(KeyValue<K, V> element) {
        return deregister(element.getKey(), element.getValue());
    }

    /**
     * 注销指定键的所有值
     * 
     * @param key 要注销的键
     * @return 注销操作回执
     */
    @Override
    public final Receipt deregisterKey(K key) {
        return update((map) -> {
            if (map == null) {
                return Receipt.FAILURE;
            }
            VC vc = map.remove(key);
            if (vc == null) {
                return Receipt.FAILURE;
            }
            vc.reset();
            return Receipt.SUCCESS;
        });
    }

    /**
     * 批量注销多个键的所有值
     * 
     * @param keys 要注销的键集合
     * @return 批量注销操作回执
     */
    @Override
    public final Receipt deregisterKeys(Iterable<? extends K> keys) {
        return Receipts.of(Elements.of(keys).map((key) -> deregisterKey(key)).toList());
    }

    /**
     * 批量注销多个键值对
     * 
     * @param elements 要注销的键值对集合
     * @return 批量注销操作回执
     */
    @Override
    public final Receipt deregisters(Elements<? extends KeyValue<K, V>> elements) {
        return Receipts.of(elements.map((e) -> deregister(e)).toList());
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
            return Elements.of(() -> map.entrySet().stream()
                    .flatMap((entry) -> entry.getValue().stream().map((e) -> KeyValue.of(entry.getKey(), e))));
        });
    }

    /**
     * 获取键值对集合（Map.Entry形式）
     * <p>
     * 每个Entry的value为该键对应的所有值的列表。
     * 
     * @return 键值对集合
     */
    @Override
    public final Set<Entry<K, List<V>>> entrySet() {
        return readAsSet((map) -> {
            if (map == null) {
                return Collections.emptySet();
            }
            return map.entrySet().stream().map((e) -> {
                Entry<K, List<V>> entry = new AtomicEntry<>(e.getKey());
                entry.setValue(e.getValue().toList());
                return entry;
            }).collect(Collectors.toSet());
        });
    }

    /**
     * 获取指定键的所有值
     * 
     * @param key 键
     * @return 值列表，若键不存在则返回null
     */
    @Override
    public final List<V> get(Object key) {
        return readAsList((map) -> {
            VC vc = map.get(key);
            if (vc == null) {
                return null;
            }
            return vc.toList();
        });
    }

    /**
     * 获取所有有效注册
     * 
     * @return 键值对注册集合
     */
    @Override
    public final Elements<KeyValueRegistration<K, V>> getElements() {
        return entries().map((e) -> KeyValueRegistration.of(e.getKey(), e.getValue(), (key, value) -> {
            Receipt receipt = deregister(key, value);
            return receipt.isSuccess();
        }));
    }

    /**
     * 获取指定键的第一个值
     * 
     * @param key 键
     * @return 第一个值，若键不存在或无值则返回null
     */
    @Override
    public final V getFirst(Object key) {
        return read((map) -> {
            if (map == null) {
                return null;
            }
            VC vc = map.get(key);
            if (vc == null) {
                return null;
            }
            return vc.first();
        });
    }

    /**
     * 检查容器是否为空
     * 
     * @return true表示为空，false表示非空
     */
    @Override
    public boolean isEmpty() {
        return readAsBoolean((map) -> map == null ? false : map.isEmpty());
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
     * 获取所有键的集合
     * 
     * @return 键集合
     */
    @Override
    public final Set<K> keySet() {
        return readAsSet((e) -> e == null ? null : e.keySet());
    }

    /**
     * 设置指定键的所有值
     * <p>
     * 若键已存在则替换所有值，否则创建新容器并注册值。
     * 
     * @param key 键
     * @param value 值列表
     * @return 旧值列表，若键不存在则返回null
     */
    @Override
    public final List<V> put(K key, List<V> value) {
        return writeAsList((map) -> {
            VC services = map.get(key);
            if (services == null) {
                services = newValues(key);
                services.registers(Elements.of(value));
                map.put(key, services);
                return null;
            } else {
                List<V> oldList = services.toList();
                services.reset();
                services.registers(Elements.of(value));
                return oldList;
            }
        });
    }

    /**
     * 批量设置键值对
     * 
     * @param m 要设置的多值映射
     */
    @Override
    public final void putAll(Map<? extends K, ? extends List<V>> m) {
        for (Entry<? extends K, ? extends List<V>> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 注册键值对
     * 
     * @param key 键
     * @param value 值
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public final Registration register(K key, V value) throws RegistrationException {
        return write((map) -> {
            VC services = map.get(key);
            if (services == null) {
                services = newValues(key);
                map.put(key, services);
            }
            return services.register(value);
        });
    }

    /**
     * 注册键值对
     * 
     * @param element 要注册的键值对
     * @return 注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public final Registration register(KeyValue<K, V> element) throws RegistrationException {
        return register(element.getKey(), element.getValue());
    }

    /**
     * 批量注册键值对
     * 
     * @param elements 要注册的键值对集合
     * @return 批量注册句柄
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    public final Registration registers(@NonNull Elements<? extends KeyValue<K, V>> elements)
            throws RegistrationException {
        return Registration.registers(elements, this::register);
    }

    /**
     * 移除指定键的所有值
     * 
     * @param key 要移除的键
     * @return 移除的值列表，若键不存在则返回null
     */
    @Override
    public final List<V> remove(Object key) {
        return updateAsList((map) -> {
            if (map == null) {
                return null;
            }
            VC vc = map.remove(key);
            if (vc == null) {
                return null;
            }
            List<V> list = vc.collect(Collectors.toList());
            vc.reset();
            return list;
        });
    }

    /**
     * 重置所有值容器
     * <p>
     * 清空每个值容器中的注册，但保留键的映射关系。
     */
    @Override
    public void reset() {
        update((map) -> {
            if (map == null) {
                return null;
            }
            for (Entry<K, VC> entry : map.entrySet()) {
                entry.getValue().reset();
            }
            return null;
        });
    }

    /**
     * 设置指定键的单个值
     * <p>
     * 清空该键的现有值并注册新值。
     * 
     * @param key 键
     * @param value 值
     */
    @Override
    public final void set(K key, V value) {
        write((map) -> {
            VC services = map.get(key);
            if (services == null) {
                services = newValues(key);
                map.put(key, services);
            }
            services.reset();
            services.register(value);
            return null;
        });
    }

    /**
     * 获取键值对数量
     * 
     * @return 键的数量
     */
    @Override
    public int size() {
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
     * 获取所有值列表
     * <p>
     * 每个元素为对应键的所有值的列表。
     * 
     * @return 值列表的集合
     */
    @Override
    public final Collection<List<V>> values() {
        return readAsList((map) -> {
            if (map == null) {
                return Collections.emptyList();
            }
            return map.values().stream().map((e) -> e.toList()).collect(Collectors.toList());
        });
    }
}