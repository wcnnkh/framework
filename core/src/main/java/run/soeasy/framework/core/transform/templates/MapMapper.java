package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 映射集合的键值对映射器，实现{@link Mapper}接口，支持基于键的映射集合遍历与值转换。
 * <p>
 * 该映射器通过内部{@link Mapper}实例处理单个键值对的映射逻辑，
 * 遍历目标映射集合中的每个键，在源映射中查找匹配键的所有值并执行映射转换。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>键驱动映射：基于键匹配源映射与目标映射的元素</li>
 *   <li>多值处理：支持源映射中单个键对应多个值的批量转换</li>
 *   <li>嵌套上下文：通过{@link MappingContext}的current方法创建嵌套映射上下文</li>
 *   <li>可配置性：通过注入不同的{@link Mapper}实例定制值映射逻辑</li>
 * </ul>
 * </p>
 *
 * <p><b>映射流程：</b>
 * <ol>
 *   <li>验证源和目标上下文是否包含有效的映射集合</li>
 *   <li>遍历目标映射集合中的每个键值对</li>
 *   <li>在源映射中查找相同键的所有值</li>
 *   <li>对每个源值创建独立上下文并调用内部映射器</li>
 *   <li>累计映射成功次数并返回结果</li>
 * </ol>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see Mapper
 * @see MappingContext
 * @see KeyValue
 */
@RequiredArgsConstructor
@Getter
@Setter
public class MapMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
    
    /** 处理单个键值对映射的内部映射器，不可为null */
    @NonNull
    private Mapper<K, V, T> valueMapper;

    /**
     * 执行映射集合的键值对转换
     * <p>
     * 该方法遍历目标映射集合，对每个键在源映射中查找匹配的所有值，
     * 并使用内部映射器逐个执行值转换。映射成功的条件：
     * <ul>
     *   <li>源和目标上下文均包含映射集合</li>
     *   <li>内部映射器返回true</li>
     * </ul>
     * </p>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 至少有一个值映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        // 验证上下文必须包含映射集合且不能包含单键值对
        if (sourceContext.hasKeyValue()
                || targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
            return false;
        }

        int count = 0;
        // 遍历目标映射集合中的每个键值对
        for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
            // 获取源映射中相同键的所有值
            Elements<V> sourceElements = sourceContext.getMapping().getValues(target.getKey());
            // 对每个源值执行映射转换
            for (V value : sourceElements) {
                if (valueMapper.doMapping(
                        // 创建源上下文的当前键值对上下文
                        sourceContext.current(KeyValue.of(target.getKey(), value)),
                        // 创建目标上下文的当前键值对上下文
                        targetContext.current(target))) {
                    count++;
                }
            }
        }
        return count > 0;
    }

    /**
     * 设置自定义值映射器（支持链式调用）
     * 
     * @param valueMapper 自定义映射器，不可为null
     * @return 当前映射器实例
     */
    public MapMapper<K, V, T> withValueMapper(@NonNull Mapper<K, V, T> valueMapper) {
        this.valueMapper = valueMapper;
        return this;
    }
}