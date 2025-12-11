package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 键值对注册表接口 提供键值对的注册、注销和查询功能 继承Registry复用注册/注销能力
 * 
 * @author soeasy.run
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>>, Mapping<K, V> {

	/**
	 * 注册单个键值对（简化版，无需手动构建KeyValue）
	 * 
	 * @param key   键
	 * @param value 值
	 * @return 注册操作的句柄（成功/失败状态+异常原因）
	 */
	Operation register(K key, V value);

	Operation deregister(K key, V value);

	/**
	 * 注销指定键的键值对
	 * 
	 * @param key 待注销的键
	 * @return 注销操作的句柄（成功/失败状态+异常原因）
	 */
	Operation deregisterKey(K key);

	@Override
	default Operation deregister(@NonNull KeyValue<K, V> element) {
		return deregister(element.getKey(), element.getValue());
	}

	@Override
	default Operation register(@NonNull KeyValue<K, V> element) {
		return register(element.getKey(), element.getValue());
	}

	/**
	 * 批量注销多个键的键值对（默认OR模式：任意键注销成功即整体成功） 容错模式：单个键注销失败不中断整体流程，异常自动转为失败句柄
	 * 
	 * @param keys 待注销的键集合（非null）
	 * @return 批量注销复合句柄（OR模式）
	 */
	default Operation deregisterKeys(@NonNull Streamable<? extends K> keys) {
		return deregisterKeys(keys, Mode.OR);
	}

	/**
	 * 批量注销多个键的键值对（支持自定义组合判定模式） 容错模式：单个键注销失败不中断整体流程，异常自动转为失败句柄
	 * 
	 * @param keys 待注销的键集合（非null）
	 * @param mode 批量结果的组合判定模式（AND：所有成功才成功；OR：任意成功即成功）
	 * @return 批量注销复合句柄（成功判定规则由mode决定）
	 */
	default Operation deregisterKeys(@NonNull Streamable<? extends K> keys, @NonNull Mode mode) {
		return Operation.batch(keys, mode, this::deregisterKey);
	}
}