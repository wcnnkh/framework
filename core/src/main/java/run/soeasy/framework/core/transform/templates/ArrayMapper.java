package run.soeasy.framework.core.transform.templates;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 数组映射器，通过迭代方式实现两个映射集合间的元素级映射转换，
 * 实现{@link Mapper}接口，支持按顺序处理映射集合中的元素。
 * <p>
 * 该映射器采用"键匹配"策略，遍历目标集合中的每个元素，
 * 在源集合中查找键相等的元素并使用内部值映射器执行转换，
 * 已成功映射的源元素将被移除，避免重复处理。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>顺序处理：按目标集合的迭代顺序依次处理元素</li>
 *   <li>键匹配机制：基于键的相等性查找匹配元素</li>
 *   <li>一次性映射：每个源元素最多被映射一次</li>
 *   <li>嵌套上下文：通过创建嵌套上下文支持复杂结构映射</li>
 * </ul>
 *
 * <p><b>映射流程：</b>
 * <ol>
 *   <li>验证源和目标上下文是否包含有效的映射集合</li>
 *   <li>将源集合转换为列表以便移除已处理元素</li>
 *   <li>遍历目标集合中的每个元素</li>
 *   <li>对每个目标元素，在源列表中查找键匹配的元素</li>
 *   <li>使用内部映射器执行元素转换，成功后从源列表移除</li>
 *   <li>统计成功映射的元素数量并返回结果</li>
 * </ol>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping}
 * 
 * @author soeasy.run
 * @see Mapper
 * @see MappingContext
 * @see TypedValueAccessor
 */
@RequiredArgsConstructor
@Getter
@Setter
public class ArrayMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
    
    /** 用于处理单个元素映射的内部映射器，不可为null */
    @NonNull
    private Mapper<K, V, T> valueMapper;

    /**
     * 执行数组元素的映射转换
     * <p>
     * 该方法遍历目标映射集合，为每个元素在源集合中查找匹配键的元素，
     * 并使用内部映射器执行具体的映射操作。映射成功的元素将从源集合中移除。
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 至少有一个元素映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        // 验证上下文条件：必须包含映射集合且不能同时包含单键值对
        if (sourceContext.hasKeyValue()
                || targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
            return false;
        }

        // 获取源映射集合元素列表（转换为可修改列表以便移除元素）
        List<KeyValue<K, V>> sourceList = sourceContext.getMapping().getElements().collect(Collectors.toList());
        if (sourceList.isEmpty()) {
            return false;
        }

        // 记录成功映射的元素数量
        int count = 0;
        
        // 遍历目标映射集合
        for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
            Iterator<KeyValue<K, V>> sourceIterator = sourceList.iterator();
            
            // 在源集合中查找匹配键的元素
            while (sourceIterator.hasNext()) {
                KeyValue<K, V> source = sourceIterator.next();
                
                // 键不匹配则跳过
                if (!ObjectUtils.equals(source.getKey(), target.getKey())) {
                    continue;
                }
                
                // 使用内部映射器处理元素映射
                if (valueMapper.doMapping(sourceContext.current(source), targetContext.nested(target))) {
                    // 映射成功后从源集合移除，避免重复处理
                    sourceIterator.remove();
                    count++;
                }
            }
        }
        
        return count > 0;
    }

    /**
     * 设置自定义值映射器
     * 
     * @param valueMapper 自定义值映射器，不可为null
     * @return 当前映射器实例，支持链式调用
     */
    public ArrayMapper<K, V, T> withValueMapper(@NonNull Mapper<K, V, T> valueMapper) {
        this.valueMapper = valueMapper;
        return this;
    }
}