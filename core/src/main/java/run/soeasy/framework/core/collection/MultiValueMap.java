package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.KeyValueRegistry;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 多值映射接口，定义K→List&lt;V&gt;类型的多值映射能力，接口层保障操作的原子性。
 * <p>核心规则：
 * <ol>
 * <li>修改操作调用链：add→adds、set→setAll、setAll→put（put由子类实现）；</li>
 * <li>接口层强制使用CopyOnWriteArrayList封装值，保证add/remove操作的原子性；</li>
 * <li>只读操作返回不可变视图，具备天然的原子安全性；</li>
 * <li>空值交由底层容器原生处理，不手动干预；</li>
 * <li>子类需实现put方法，接口层不约束其原子性实现。</li>
 * </ol>
 *
 * @author soeasy.run
 * @param <K> 键类型
 * @param <V> 值类型
 * @see Map
 * @see List
 * @see CopyOnWriteArrayList
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>>, Mapping<K, V>, KeyValueRegistry<K, V> {
    // ========== 单值添加（原子化） ==========
    /**
     * 原子化添加单个值，复用adds方法的逻辑。
     *
     * @param key  键
     * @param value 值，不可为null
     * @throws IllegalArgumentException 当value为null时抛出
     */
    default void add(K key, V value) {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        adds(key, Collections.singletonList(value));
    }

    // ========== 批量添加其他Map（原子化） ==========
    /**
     * 原子化批量添加多值映射，无先查后改的非原子步骤。
     *
     * @param map 待添加的多值映射Map，不可为null
     */
    default void addAll(@NonNull Map<? extends K, ? extends Collection<V>> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach((key, values) -> {
            if (values == null || values.isEmpty()) {
                return;
            }
            // compute原子操作：创建/追加CopyOnWriteArrayList
            compute(key, (k, oldList) -> {
                List<V> newList = oldList == null ? new CopyOnWriteArrayList<>() : new CopyOnWriteArrayList<>(oldList);
                newList.addAll(values);
                return newList;
            });
        });
    }

    @Override
    boolean isEmpty();

    // ========== 单键多值添加（原子化） ==========
    /**
     * 原子化添加单个键对应的多个值，复用addAll方法的逻辑。
     *
     * @param key  键
     * @param values 值集合，不可为null且不能为空
     */
    default void adds(K key, Collection<V> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        addAll(Collections.singletonMap(key, values));
    }

    // ========== 单值覆盖（原子化） ==========
    /**
     * 原子化设置单个值（覆盖指定键原有值），复用setAll方法的逻辑。
     *
     * @param key  键
     * @param value 值，不可为null
     * @throws IllegalArgumentException 当value为null时抛出
     */
    default void set(K key, V value) {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        setAll(Collections.singletonMap(key, value));
    }

    // ========== 批量覆盖（原子化） ==========
    /**
     * 原子化批量设置值（覆盖指定键原有值），调用子类实现的put方法。
     *
     * @param map 待设置的键值对Map，不可为null
     */
    default void setAll(@NonNull Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            // 封装为原子性List后调用子类put
            List<V> newList = new CopyOnWriteArrayList<>();
            newList.add(value);
            put(key, newList);
        });
    }

    // ========== 只读方法 ==========
    @Override
    default Stream<KeyValue<K, V>> stream() {
        return entrySet().stream()
                .flatMap((kvs) -> kvs.getValue().stream().map((value) -> KeyValue.of(kvs.getKey(), value)));
    }

    /**
     * 获取指定键对应的第一个值。
     *
     * @param key 键
     * @return 该键对应的第一个值，无值时返回null
     */
    default V getFirst(K key) {
        List<V> values = get(key);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    @Override
    default Streamable<V> getValues(K key) {
        List<V> values = get(key);
        return (values == null || values.isEmpty()) ? Streamable.empty()
                : Streamable.of(Collections.unmodifiableList(values));
    }

    /**
     * 转换为单值映射Map（取每个键对应的第一个值）。
     *
     * @return 不可变的单值映射Map
     */
    default Map<K, V> toSingleValueMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }
        Map<K, V> singleValueMap = new LinkedHashMap<>(size());
        for (Entry<K, List<V>> entry : entrySet()) {
            List<V> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                continue;
            }
            singleValueMap.put(entry.getKey(), values.get(0));
        }
        return Collections.unmodifiableMap(singleValueMap);
    }

    // ========== 注册/注销（原子化） ==========
    @Override
    default Operation register(K key, V value) {
        try {
            add(key, value);
            BooleanSupplier rollbackLogic = () -> {
                try {
                    return deregister(key, value).isSuccess();
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Register rollback failed (key=%s, value=%s)", key, value), e);
                }
            };
            return Operation.success(rollbackLogic);
        } catch (Exception e) {
            return Operation.failure(new RuntimeException(String.format("Register failed (key=%s, value=%s)", key, value), e));
        }
    }

    @Override
    default Operation deregister(K key, V value) {
        try {
            boolean[] removeSuccess = { false };
            // compute原子操作：校验+移除值
            compute(key, (k, oldList) -> {
                if (oldList == null || oldList.isEmpty()) {
                    removeSuccess[0] = false;
                    return oldList;
                }
                if (!oldList.contains(value)) {
                    removeSuccess[0] = false;
                    return oldList;
                }
                boolean removed = oldList.remove(value);
                removeSuccess[0] = removed && !oldList.contains(value);
                return oldList;
            });

            if (removeSuccess[0]) {
                BooleanSupplier rollbackLogic = () -> {
                    try {
                        return register(key, value).isSuccess();
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Deregister rollback failed (key=%s, value=%s)", key, value), e);
                    }
                };
                return Operation.success(rollbackLogic);
            }
            return Operation.failure(new RuntimeException(String.format("Deregister failed: key not exist or value not match (key=%s, value=%s)", key, value)));
        } catch (Exception e) {
            return Operation.failure(new RuntimeException(String.format("Deregister failed (key=%s, value=%s)", key, value), e));
        }
    }

    @Override
    default Operation deregisterKey(K key) {
        try {
            boolean[] keyExisted = { false };
            // compute原子操作：校验+删除键
            compute(key, (k, oldList) -> {
                if (oldList == null) {
                    keyExisted[0] = false;
                    return null;
                }
                keyExisted[0] = true;
                return null;
            });

            if (keyExisted[0]) {
                return Operation.success();
            } else {
                return Operation.failure(new RuntimeException(String.format("Deregister key failed: key not exist (key=%s)", key)));
            }
        } catch (Exception e) {
            return Operation.failure(new RuntimeException(String.format("Deregister key failed (key=%s)", key), e));
        }
    }
}