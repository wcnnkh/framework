package run.soeasy.framework.core.transform.property;

import java.util.Collection;
import java.util.function.BiPredicate;

import lombok.NonNull;
import run.soeasy.framework.core.transform.templates.MappingFilter;

/**
 * 属性映射过滤器接口，继承自{@link MappingFilter}，用于在属性映射过程中对源和目标属性进行过滤，
 * 支持通过谓词逻辑定义过滤规则，实现属性级别的映射控制。
 * <p>
 * 该接口定义了属性映射的过滤规范，适用于以下场景：
 * <ul>
 *   <li>忽略空值属性：避免映射值为null的属性</li>
 *   <li>按名称过滤属性：排除特定名称的属性映射</li>
 *   <li>自定义过滤逻辑：通过谓词表达式定义复杂过滤规则</li>
 * </ul>
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>空值过滤：内置{@link #IGNORE_NULL}过滤器忽略空值属性</li>
 *   <li>名称过滤：通过{@link #ignorePropertyNames(Collection)}按名称过滤属性</li>
 *   <li>谓词支持：通过{@link #predicate(BiPredicate)}支持自定义谓词过滤</li>
 *   <li>链式调用：可与其他过滤器组合使用，形成过滤链</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see MappingFilter
 * @see PropertyMappingPredicate
 */
public interface PropertyMappingFilter extends MappingFilter<Object, PropertyAccessor, TypedProperties> {
    
    /** 忽略空值属性的过滤器（源属性可读且值为null时过滤） */
    PropertyMappingFilter IGNORE_NULL = predicate((s, t) -> s.isReadable() && s.get() == null);

    /**
     * 创建按属性名称过滤的过滤器
     * <p>
     * 该过滤器会排除名称在指定集合中的属性，
     * 当源属性或目标属性的名称存在于集合中时，对应的映射会被过滤。
     * </p>
     * 
     * @param names 要忽略的属性名称集合，不可为null
     * @return 按名称过滤的属性映射过滤器
     * @throws NullPointerException 若names为null
     */
    static PropertyMappingFilter ignorePropertyNames(@NonNull Collection<String> names) {
        return predicate((s, t) -> !(names.contains(s.getName()) || names.contains(t.getName())));
    }

    /**
     * 根据谓词创建自定义属性映射过滤器
     * <p>
     * 该方法将BiPredicate转换为属性映射过滤器，
     * 当谓词返回true时允许映射，返回false时过滤映射。
     * </p>
     * 
     * @param predicate 自定义谓词，参数为源属性和目标属性，不可为null
     * @return 自定义属性映射过滤器
     * @throws NullPointerException 若predicate为null
     * @see PropertyMappingPredicate
     */
    static PropertyMappingFilter predicate(@NonNull BiPredicate<? super PropertyAccessor, ? super PropertyAccessor> predicate) {
        return new PropertyMappingPredicate(predicate);
    }
}