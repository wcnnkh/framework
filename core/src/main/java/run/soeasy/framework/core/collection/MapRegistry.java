package run.soeasy.framework.core.collection;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.KeyValueRegistry;
import run.soeasy.framework.core.exchange.Operation;

/**
 * 映射型注册中心接口（原子性保障 + 兼容Null值 + 无内部类）
 * <p>
 * 核心规则： 1. 所有操作基于Map.compute原子方法实现，全程无“查-改”分离的非原子步骤； 2.
 * 键（key）非空，值（value）可空，兼容Null值的注册/注销/回滚； 3. 用数组传递原子块内状态，无内部类，轻量级且线程安全； 4.
 * 操作结果通过Operation返回，回滚逻辑也保证原子性。
 * 
 * @author soeasy.run
 * @param <K> 键类型（非Null）
 * @param <V> 值类型（可Null）
 */
public interface MapRegistry<K, V> extends Map<K, V>, KeyValueRegistry<K, V> {

	@Override
	boolean isEmpty();

	/**
	 * 原子注册键值对（全程compute原子操作，兼容Null值，无内部类）
	 * 
	 * @param key   注册键（非Null）
	 * @param value 注册值（可Null）
	 * @return Operation 原子操作结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	default Operation register(K key, V value) {
		// 仅校验键非空（Map键通用语义）
		if (key == null) {
			return Operation.failure(new RuntimeException("Register failed: key is null"));
		}

		try {
			// 用数组传递原子块内状态：[0] = 旧值(V), [1] = 操作成功标识(Boolean)
			Object[] atomicState = new Object[2];
			// 核心：全程在compute原子块内完成“查旧值+设新值+标记结果”
			compute(key, (k, oldValue) -> {
				atomicState[0] = oldValue; // 原子块内保存旧值（无并发问题）
				atomicState[1] = Boolean.TRUE;// 标记原子操作成功
				return value; // 原子覆盖新值（兼容Null）
			});

			boolean success = atomicState[1] == Boolean.TRUE;
			if (success) {
				V oldValue = (V) atomicState[0];
				// 回滚逻辑：基于compute原子操作，用数组传递回滚结果
				return Operation.success(() -> {
					boolean[] rollbackSuccess = new boolean[1];
					compute(key, (k, currentValue) -> {
						// 原子块内判断：仅当当前值与注册值一致时恢复旧值
						if (Objects.equals(currentValue, value)) {
							rollbackSuccess[0] = true;
							return oldValue; // 恢复旧值（Null则删除键）
						}
						rollbackSuccess[0] = false;
						return currentValue; // 不匹配则不修改
					});
					return rollbackSuccess[0];
				});
			} else {
				return Operation.failure(new RuntimeException("Register failed: atomic compute error"));
			}
		} catch (Exception e) {
			return Operation.failure(e);
		}
	}

	/**
	 * 原子注销键值对（仅键值匹配时删除，全程compute原子操作，无内部类）
	 * 
	 * @param key   注销键（非Null）
	 * @param value 注销值（可Null）
	 * @return Operation 原子操作结果
	 */
	@Override
	default Operation deregister(K key, V value) {
		if (key == null) {
			return Operation.failure(new RuntimeException("Deregister failed: key is null"));
		}

		try {
			// 用数组传递原子块内匹配结果
			boolean[] matchResult = new boolean[1];
			// 核心：原子块内完成“匹配判断+删除+结果标记”
			compute(key, (k, currentValue) -> {
				if (Objects.equals(value, currentValue)) {
					matchResult[0] = true;
					return null; // 原子删除键
				} else {
					matchResult[0] = false;
					return currentValue; // 不匹配则保留
				}
			});

			if (matchResult[0]) {
				// 回滚逻辑：原子重新注册该键值对
				return Operation.success(() -> register(key, value).isSuccess());
			} else {
				return Operation.failure(new RuntimeException(String.format(
						"Deregister failed: key=%s value mismatch (target=%s, current=%s)", key, value, get(key))));
			}
		} catch (Exception e) {
			return Operation.failure(e);
		}
	}

	/**
	 * 原子重置（清空所有键值对，依赖Map.clear原子实现）
	 * 
	 * @return Operation 原子操作结果
	 */
	@Override
	default Operation reset() {
		try {
			// clear在ConcurrentHashMap/HashMap等实现中均为原子操作
			clear();
			return Operation.success();
		} catch (Exception e) {
			return Operation.failure(e);
		}
	}

	/**
	 * 原子注销指定键（无视值，全程compute原子操作，无内部类）
	 * 
	 * @param key 注销键（非Null）
	 * @return Operation 原子操作结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	default Operation deregisterKey(K key) {
		if (key == null) {
			return Operation.failure(new RuntimeException("DeregisterKey failed: key is null"));
		}

		try {
			// 用数组传递原子块内状态：[0] = 旧值(V), [1] = 操作成功标识(Boolean)
			Object[] atomicState = new Object[2];
			// 核心：原子块内完成“查旧值+删除+结果标记”
			compute(key, (k, oldValue) -> {
				atomicState[0] = oldValue;
				atomicState[1] = Boolean.valueOf(oldValue != null); // 键存在则标记成功
				return null; // 原子删除键
			});

			boolean success = atomicState[1] == Boolean.TRUE;
			if (success) {
				V oldValue = (V) atomicState[0];
				// 回滚逻辑：原子恢复旧值
				return Operation.success(() -> register(key, oldValue).isSuccess());
			} else {
				return Operation
						.failure(new RuntimeException(String.format("DeregisterKey failed: key=%s not found", key)));
			}
		} catch (Exception e) {
			return Operation.failure(e);
		}
	}

	@Override
	default Stream<KeyValue<K, V>> stream() {
		return entrySet().stream().map(KeyValue::wrap);
	}
}