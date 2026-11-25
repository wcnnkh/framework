package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;
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
	Signal register(K key, V value);

	Signal deregister(K key, V value);

	/**
	 * 注销指定键的键值对
	 * 
	 * @param key 待注销的键
	 * @return 注销操作的句柄（成功/失败状态+异常原因）
	 */
	Signal deregisterKey(K key);

	@Override
	default Signal deregister(@NonNull KeyValue<K, V> element) {
		return deregister(element.getKey(), element.getValue());
	}

	@Override
	default Signal register(@NonNull KeyValue<K, V> element) {
		return register(element.getKey(), element.getValue());
	}

	default Signal deregisterKeys(@NonNull Streamable<? extends K> keys) {
		return Signal.batch(keys.map(this::deregisterKey));
	}
}