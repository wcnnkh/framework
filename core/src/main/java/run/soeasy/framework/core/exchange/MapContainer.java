package run.soeasy.framework.core.exchange;

import java.util.Map;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 键值对注册表的Map实现类，底层基于Map&lt;K, V&gt;存储键值对，实现{@link KeyValueRegistry}接口定义的注册/注销能力。
 * <p>核心规则：
 * <ol>
 * <li>键唯一性：通过Map.putIfAbsent原子操作保证仅当键不存在时注册，避免覆盖已有值；</li>
 * <li>线程安全：依赖底层Map实现，推荐使用ConcurrentHashMap以支持原生原子操作；</li>
 * <li>原子性保障：所有修改操作仅依赖Map原生原子方法，无任何非原子中间步骤：
 *   <ul>
 *   <li>register(K,V)：基于Map.putIfAbsent实现原子注册；</li>
 *   <li>deregister(K,V)：基于Map.remove(key, value)实现原子校验+删除；</li>
 *   <li>deregisterKey(K)：基于Map.remove(key)实现原子删除；</li>
 *   </ul>
 * </li>
 * <li>回滚规则：
 *   <ul>
 *   <li>register(K,V)：支持回滚（删除对应键）；</li>
 *   <li>deregister(K,V)：支持回滚（重新注册原键值对）；</li>
 *   <li>deregisterKey(K)：不支持回滚；</li>
 *   <li>批量注销：仅单个键注销支持回滚，批量操作整体无回滚；</li>
 *   </ul>
 * </li>
 * <li>空值规则：空值校验交由底层Map原生处理，不手动干预；</li>
 * <li>匹配规则：
 *   <ul>
 *   <li>deregister(K,V)：键存在且值相等（equals匹配）时注销；</li>
 *   <li>deregisterKey(K)：仅按键注销，无需匹配值；</li>
 *   <li>contains(KeyValue)：键存在且值相等时返回true；</li>
 *   </ul>
 * </li>
 * </ol>
 *
 * @author soeasy.run
 * @param <K> 键类型（需实现equals/hashCode，空值规则由底层Map决定）
 * @param <V> 值类型（需实现equals，空值规则由底层Map决定）
 * @param <D> 底层Map实现类型（推荐ConcurrentHashMap）
 * @see KeyValueRegistry
 * @see Map
 */
@RequiredArgsConstructor
@Getter
public class MapContainer<K, V, D extends Map<K, V>> implements KeyValueRegistry<K, V> {
	/**
	 * 底层存储容器，用于存储键值对，保证键唯一性
	 */
	@NonNull
	private final D container;

	/**
	 * 注册单个键值对，仅当键不存在时注册成功，支持回滚。
	 * <p>操作逻辑：
	 * 1. 调用Map.putIfAbsent原子操作，键不存在时存入键值对；
	 * 2. 注册成功则绑定回滚逻辑（删除该键），返回成功句柄；
	 * 3. 键已存在或异常时返回失败句柄。
	 *
	 * @param key  键（空值规则由底层Map决定）
	 * @param value 值（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（绑定回滚逻辑）；</li>
	 *         <li>失败：Operation.failure（包含失败原因）。</li>
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常
	 */
	@Override
	public Operation register(K key, V value) {
		try {
			V oldValue = container.putIfAbsent(key, value);
			if (oldValue == null) {
				return Operation.success(() -> {
					try {
						return deregisterKey(key).sync().isSuccess();
					} catch (Exception e) {
						throw new RuntimeException(
								String.format("Register rollback failed (key=%s, value=%s)", key, value), e);
					}
				});
			}
			return Operation
					.failure(new RuntimeException(String.format("Register failed: key already exists (key=%s)", key)));
		} catch (Exception e) {
			return Operation
					.failure(new RuntimeException(String.format("Register failed (key=%s, value=%s)", key, value), e));
		}
	}

	/**
	 * 注销指定键值对，需键存在且值匹配才注销成功，支持回滚。
	 * <p>操作逻辑：
	 * 1. 调用Map.remove(key, value)原子操作，校验并删除匹配的键值对；
	 * 2. 注销成功则绑定回滚逻辑（重新注册原键值对），返回成功句柄；
	 * 3. 校验失败或异常时返回失败句柄。
	 *
	 * @param key  键（空值规则由底层Map决定）
	 * @param value 值（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（绑定回滚逻辑）；</li>
	 *         <li>失败：Operation.failure（包含失败原因）。</li>
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常
	 */
	@Override
	public Operation deregister(K key, V value) {
		try {
			boolean removeSuccess = container.remove(key, value);
			if (removeSuccess) {
				return Operation.success(() -> {
					try {
						return register(key, value).sync().isSuccess();
					} catch (Exception e) {
						throw new RuntimeException(
								String.format("Deregister rollback failed (key=%s, value=%s)", key, value), e);
					}
				});
			}

			return Operation.failure(new RuntimeException(String
					.format("Deregister failed: key not exist or value not match (key=%s, value=%s)", key, value)));
		} catch (Exception e) {
			return Operation.failure(
					new RuntimeException(String.format("Deregister failed (key=%s, value=%s)", key, value), e));
		}
	}

	/**
	 * 注销指定键的键值对，仅按键注销，不支持回滚。
	 * <p>操作逻辑：
	 * 1. 调用Map.remove(key)原子操作删除键；
	 * 2. 原子操作执行完成即返回成功句柄；
	 * 3. 异常时返回失败句柄。
	 *
	 * @param key 键（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（无回滚逻辑）；</li>
	 *         <li>失败：Operation.failure（包含失败原因）。</li>
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常
	 */
	@Override
	public Operation deregisterKey(K key) {
		try {
			container.remove(key);
			return Operation.success();
		} catch (Exception e) {
			return Operation.failure(new RuntimeException(String.format("Deregister key failed (key=%s)", key), e));
		}
	}

	/**
	 * 获取所有键的只读流
	 *
	 * @return 键的只读Streamable
	 */
	@Override
	public Streamable<K> keys() {
		return Streamable.of(container.keySet());
	}

	/**
	 * 判断注册表是否为空
	 *
	 * @return true=无键值对，false=包含至少一个键值对
	 */
	@Override
	public boolean isEmpty() {
		return container.isEmpty();
	}

	/**
	 * 判断是否包含指定的KeyValue元素
	 * <p>需键存在且值相等（equals匹配）才返回true，兼容值为null的场景。
	 *
	 * @param element KeyValue元素
	 * @return true=包含，false=不包含
	 * @throws RuntimeException 底层Map抛出的异常
	 */
	@Override
	public boolean contains(Object element) {
		if (!(element instanceof KeyValue)) {
			return false;
		}
		KeyValue<?, ?> keyValue = (KeyValue<?, ?>) element;
		Object key = keyValue.getKey();
		V storedValue = container.get(key);
		Object elementValue = keyValue.getValue();

		if (storedValue == null) {
			return elementValue == null && container.containsKey(key);
		}
		return storedValue.equals(elementValue);
	}

	@Override
	public long count() {
		return container.size();
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return container.entrySet().stream().map(e -> KeyValue.of(e.getKey(), e.getValue()));
	}

	@Override
	public boolean hasKey(K key) {
		return container.containsKey(key);
	}

	@Override
	public Operation reset() {
		container.clear();
		return Operation.success();
	}

	@Override
	public Streamable<V> getValues(K key) {
		if (!container.containsKey(key)) {
			return Streamable.empty();
		}
		V value = container.get(key);
		return Streamable.singleton(value);
	}

	@Override
	public String toString() {
		return container.toString();
	}

	@Override
	public final Operation deregister(@NonNull KeyValue<K, V> element) {
		return KeyValueRegistry.super.deregister(element);
	}

	@Override
	public final Operation deregisterAll(@NonNull Streamable<? extends KeyValue<K, V>> elements) {
		return KeyValueRegistry.super.deregisterAll(elements);
	}

	@Override
	public final Operation deregisterAll(@NonNull Streamable<? extends KeyValue<K, V>> elements, @NonNull Mode mode) {
		return KeyValueRegistry.super.deregisterAll(elements, mode);
	}

	@Override
	public final Operation deregisterKeys(@NonNull Streamable<? extends K> keys) {
		return KeyValueRegistry.super.deregisterKeys(keys);
	}

	@Override
	public final Operation deregisterKeys(@NonNull Streamable<? extends K> keys, @NonNull Mode mode) {
		return KeyValueRegistry.super.deregisterKeys(keys, mode);
	}

	@Override
	public final Operation register(@NonNull KeyValue<K, V> element) {
		return KeyValueRegistry.super.register(element);
	}

	@Override
	public final Operation registerAll(@NonNull Streamable<? extends KeyValue<K, V>> elements) {
		return KeyValueRegistry.super.registerAll(elements);
	}

	@Override
	public final Operation registerAll(@NonNull Streamable<? extends KeyValue<K, V>> elements, @NonNull Mode mode) {
		return KeyValueRegistry.super.registerAll(elements, mode);
	}

	@Override
	public boolean isMapped() {
		return true;
	}
}