package run.soeasy.framework.core.exchange.container;

import java.util.Arrays;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValues;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 键值对注册表接口
 * 提供键值对的注册、注销和查询功能，继承自Registry和KeyValues
 * 
 * @author soeasy.run
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>>, KeyValues<K, V> {

    /**
     * 注册单个键值对
     * 
     * @param key 键
     * @param value 值
     * @return 注册操作的句柄
     * @throws RegistrationException 注册失败时抛出
     */
    default Registration register(K key, V value) throws RegistrationException {
        return register(KeyValue.of(key, value));
    }

    /**
     * 注销指定键的键值对
     * 
     * @param key 待注销的键
     * @return 注销操作的回执
     */
    default Receipt deregisterKey(K key) {
        return deregisterKeys(Arrays.asList(key));
    }

    /**
     * 注销多个键的键值对
     * 
     * @param keys 待注销的键集合
     * @return 注销操作的回执（只要有一个成功即视为成功）
     */
    Receipt deregisterKeys(Iterable<? extends K> keys);

    @Override
    default Elements<K> keys() {
        return map((e) -> e.getKey());
    }

    @Override
    default Elements<V> getValues(K key) {
        return filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
    }
}