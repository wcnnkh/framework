package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 类型映射类
 * 表示从源类型到目标类型的映射关系，支持类型转换判断、反向映射和比较
 * 
 * 实现接口：
 * - KeyValue&lt;Class&lt;?&gt;, Class&lt;?&gt;&gt;: 表示键值对（键为源类型，值为目标类型）
 * - Convertable: 支持类型转换判断
 * - Comparable&lt;TypeMapping&gt;: 支持类型映射的比较排序
 */
@RequiredArgsConstructor
@Getter
@ToString
public class TypeMapping implements KeyValue<Class<?>, Class<?>>, Convertable, Comparable<TypeMapping> {
    @NonNull
    private final Class<?> key;       // 源类型（映射的键）
    @NonNull
    private final Class<?> value;     // 目标类型（映射的值）

    /**
     * 获取反向映射（目标类型-&gt;源类型）
     * 
     * @return 反向的TypeMapping实例，value和key互换
     */
    @Override
    public TypeMapping reversed() {
        return new TypeMapping(value, key);
    }

    /**
     * 判断是否支持类型转换
     * 
     * @param sourceTypeDescriptor 源类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return true表示源类型可转换为key，目标类型可转换为value
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
                             @NonNull TypeDescriptor targetTypeDescriptor) {
        // 检查源类型是否是key的子类或实现类
        // 检查目标类型是否是value的子类或实现类
        return ClassUtils.isAssignable(this.key, sourceTypeDescriptor.getType())
                && ClassUtils.isAssignable(this.value, targetTypeDescriptor.getType());
    }

    /**
     * 比较两个类型映射的顺序
     * 
     * 比较规则：
     * 1. 先比较源类型（key）的继承关系
     * 2. 源类型相同时再比较目标类型（value）的继承关系
     * 3. 使用TypeComparator.DEFAULT定义的规则进行比较
     * 
     * @param o 待比较的类型映射
     * @return 负整数表示当前实例小于o，零表示相等，正整数表示大于o
     */
    @Override
    public int compareTo(TypeMapping o) {
        int keyCompare = TypeComparator.DEFAULT.compare(this.key, o.key);
        if (keyCompare != 0) {
            return keyCompare; // 源类型比较结果非零时直接返回
        }
        return TypeComparator.DEFAULT.compare(this.value, o.value); // 源类型相同时比较目标类型
    }
}