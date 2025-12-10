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
 * 基于Map的键值对注册表实现
 * <p>
 * 核心定位：KeyValueRegistry接口的标准实现，底层基于Map<K,
 * V>存储键值对，天然保证键唯一性，支持精准回滚（部分操作），兼顾线程安全与性能。
 * <p>
 * 核心规则（必读）： 1. 键唯一性：基于Map.putIfAbsent原子操作保证“仅当键不存在时注册”，避免覆盖已有值； 2.
 * 线程安全：默认推荐使用ConcurrentHashMap作为底层存储（原子操作原生支持）；自定义Map需保证线程安全； 3.
 * 原子性保障（仅依赖Map原生原子方法，无任何非原子中间步骤）： -
 * register(K,V)：Map.putIfAbsent原子操作，仅键不存在时注册成功； -
 * deregister(K,V)：Map.remove(key, value)原子方法，一次性完成值校验+删除； -
 * deregisterKey(K)：Map.remove(key)原子方法，纯原子删除，无任何额外校验； 4. 回滚语义： -
 * register(K,V)：支持回滚（删除对应键）； - deregister(K,V)：支持回滚（重新注册原键值对）； -
 * deregisterKey(K)：不支持回滚（纯原子删除，无回滚逻辑）； -
 * 批量注销回滚：仅支持单个键注销的回滚，批量操作整体无回滚（需业务层自行处理）； 5.
 * 空值规则：空值校验完全交由底层Map原生处理（如ConcurrentHashMap键/值均不允许null，HashMap键不允许null、值允许null）；
 * 6. 匹配规则： - deregister(K, V)：需键存在且值相等（equals匹配）才注销（原子校验）； -
 * deregisterKey(K)：仅按键注销，无需匹配值； - contains(KeyValue)：需键存在且值相等才返回true。
 *
 * @author soeasy.run
 * @param <K> 键类型（需正确实现equals/hashCode，空值规则由底层Map决定）
 * @param <V> 值类型（需正确实现equals，空值规则由底层Map决定）
 * @param <D> 底层Map实现类型（推荐ConcurrentHashMap，保证原子操作）
 * @see KeyValueRegistry 键值对注册表核心接口
 * @see java.util.Map 底层存储容器接口
 */
@RequiredArgsConstructor
@Getter
public class MapContainer<K, V, D extends Map<K, V>> implements KeyValueRegistry<K, V> {
	/**
	 * 底层存储容器：存储键值对，天然保证键唯一性
	 * <p>
	 * 默认推荐使用ConcurrentHashMap，自定义Map需保证线程安全和原子操作支持
	 */
	@NonNull
	private final D delegate;

	/**
	 * 注册单个键值对（仅当键不存在时注册，原子性保障+支持回滚）
	 * <p>
	 * 空值校验：交由底层Map原生处理，不做手动干预
	 * <p>
	 * 操作逻辑： 1. 原子校验键是否存在（Map.putIfAbsent），仅不存在时存入键值对； 2.
	 * 注册成功：绑定回滚逻辑（删除该键），返回支持回滚的成功句柄； 3. 注册失败：键已存在则返回失败句柄，异常时返回失败句柄。
	 *
	 * @param key   键（空值规则由底层Map决定）
	 * @param value 值（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（绑定回滚逻辑）；
	 *         <li>失败：Operation.failure（键已存在/异常，包含具体原因）。
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常（如ConcurrentHashMap不允许null值/键）
	 */
	@Override
	public Operation register(K key, V value) {
		try {
			// 纯原子操作：仅当键不存在时存入，返回null表示注册成功
			V oldValue = delegate.putIfAbsent(key, value);
			if (oldValue == null) {
				// 注册成功，绑定回滚逻辑（原子删除该键）
				return Operation.success(() -> {
					try {
						return deregisterKey(key).sync().isSuccess();
					} catch (Exception e) {
						throw new RuntimeException(
								String.format("Register rollback failed (key=%s, value=%s)", key, value), e);
					}
				});
			}
			// 键已存在，返回失败（提示具体key）
			return Operation
					.failure(new RuntimeException(String.format("Register failed: key already exists (key=%s)", key)));
		} catch (Exception e) {
			// 捕获底层Map异常（如空值、并发异常等）
			return Operation
					.failure(new RuntimeException(String.format("Register failed (key=%s, value=%s)", key, value), e));
		}
	}

	/**
	 * 注销指定键+值的键值对（精准注销，原子性保障+支持回滚）
	 * <p>
	 * 空值校验：交由底层Map原生处理，不做手动干预
	 * <p>
	 * 操作逻辑（仅原子步骤）： 1. 原子校验键值是否匹配并删除（Map.remove(key, value)）； 2.
	 * 注销成功：绑定回滚逻辑（重新注册原键值对），返回支持回滚的成功句柄； 3. 注销失败：返回统一失败提示（原子校验失败），异常时返回失败句柄。
	 *
	 * @param key   键（空值规则由底层Map决定）
	 * @param value 值（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（绑定回滚逻辑）；
	 *         <li>失败：Operation.failure（原子校验失败/异常，包含具体原因）。
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常（如ConcurrentHashMap不允许null值/键）
	 */
	@Override
	public Operation deregister(K key, V value) {
		try {
			// 纯原子操作：校验并删除匹配的键值对
			boolean removeSuccess = delegate.remove(key, value);
			if (removeSuccess) {
				// 注销成功，绑定回滚逻辑（原子重新注册）
				return Operation.success(() -> {
					try {
						return register(key, value).sync().isSuccess();
					} catch (Exception e) {
						throw new RuntimeException(
								String.format("Deregister rollback failed (key=%s, value=%s)", key, value), e);
					}
				});
			}

			// 原子校验失败，返回统一提示（原子性优先，不区分具体原因）
			return Operation.failure(new RuntimeException(String
					.format("Deregister failed: key not exist or value not match (key=%s, value=%s)", key, value)));
		} catch (Exception e) {
			// 捕获底层Map异常（如空值、并发异常等）
			return Operation.failure(
					new RuntimeException(String.format("Deregister failed (key=%s, value=%s)", key, value), e));
		}
	}

	/**
	 * 注销指定键的键值对（仅按键注销，极致原子性+不支持回滚）
	 * <p>
	 * 空值校验：交由底层Map原生处理，不做手动干预
	 * <p>
	 * 操作逻辑（纯原子步骤，无任何非原子校验）： 1. 原子删除该键（Map.remove(key)），无任何额外校验； 2.
	 * 原子操作执行完成即返回成功（兼容值为null场景，原子性优先）； 3. 异常时返回失败句柄。
	 * <p>
	 * 注意：对于值为null的键值对，无法区分“键不存在”和“值为null”，但保证原子性。
	 *
	 * @param key 键（空值规则由底层Map决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（无回滚逻辑，原子操作执行完成）；
	 *         <li>失败：Operation.failure（仅异常场景返回）。
	 *         </ul>
	 * @throws RuntimeException 底层Map抛出的异常（如ConcurrentHashMap不允许null键）
	 */
	@Override
	public Operation deregisterKey(K key) {
		try {
			// 极致原子操作：仅执行删除，无任何后续校验
			delegate.remove(key);
			// 原子操作执行完成即返回成功（原子性优先，放弃键不存在的精准判断）
			return Operation.success();
		} catch (Exception e) {
			return Operation.failure(new RuntimeException(String.format("Deregister key failed (key=%s)", key), e));
		}
	}

	/**
	 * 获取所有键的只读流
	 *
	 * @return 键的只读Streamable（不支持修改操作）
	 */
	@Override
	public Streamable<K> keys() {
		return Streamable.of(delegate.keySet());
	}

	/**
	 * 判断注册表是否为空
	 *
	 * @return true=无任何键值对，false=包含至少一个键值对
	 */
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/**
	 * 判断是否包含指定的KeyValue元素
	 * <p>
	 * 空值校验：交由底层Map原生处理，不做手动干预
	 * <p>
	 * 需键存在且值相等（equals匹配）才返回true，兼容值为null的场景
	 *
	 * @param element KeyValue元素（空值规则由底层Map决定）
	 * @return true=包含，false=不包含
	 * @throws RuntimeException 底层Map抛出的异常（如空值、并发异常等）
	 */
	@Override
	public boolean contains(Object element) {
		if (!(element instanceof KeyValue)) {
			return false;
		}
		KeyValue<?, ?> keyValue = (KeyValue<?, ?>) element;
		Object key = keyValue.getKey();
		V storedValue = delegate.get(key);
		Object elementValue = keyValue.getValue();

		// 兼容值为null的合法场景（由底层Map决定是否允许null值）
		if (storedValue == null) {
			return elementValue == null && delegate.containsKey(key);
		}
		return storedValue.equals(elementValue);
	}

	@Override
	public long count() {
		return delegate.size();
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return delegate.entrySet().stream().map(e -> KeyValue.of(e.getKey(), e.getValue()));
	}

	@Override
	public boolean hasKey(K key) {
		return delegate.containsKey(key);
	}

	@Override
	public Operation reset() {
		// 原子清空（ConcurrentHashMap.clear()为原子操作）
		delegate.clear();
		return Operation.success();
	}

	@Override
	public Streamable<V> getValues(K key) {
		if (!delegate.containsKey(key)) {
			return Streamable.empty();
		}
		V value = delegate.get(key);
		return Streamable.singleton(value);
	}

	@Override
	public String toString() {
		return delegate.toString();
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