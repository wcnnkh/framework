package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateMapping;

/**
 * 属性映射接口，作为函数式接口继承自{@link PropertyTemplate}和{@link TemplateMapping}，
 * 整合属性模板的结构化访问能力与模板映射的类型转换能力，用于定义属性级别的映射规则。
 * <p>
 * 该接口适用于对象属性的映射转换场景，支持通过键或索引访问属性描述符，
 * 并可将属性模板转换为Map或数组形式以适应不同的映射策略。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双重能力整合：兼具{@link PropertyTemplate}的属性检索能力和{@link TemplateMapping}的映射转换能力</li>
 *   <li>多形式转换：支持将属性映射转换为Map或数组形式（通过{@link #asMap(boolean)}和{@link #asArray(boolean)}）</li>
 *   <li>函数式支持：作为函数式接口可通过lambda表达式创建实例，简化映射规则定义</li>
 *   <li>类型安全：通过泛型确保属性访问器类型的一致性（{@code V extends PropertyAccessor}）</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code V}：属性访问器类型，需实现{@link PropertyAccessor}接口，用于属性值的类型化访问</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>对象属性的映射转换（如DTO与Entity之间的属性映射）</li>
 *   <li>动态属性访问规则定义（基于键或索引的属性检索）</li>
 *   <li>属性级别的类型转换逻辑封装（结合{@link TemplateMapping}的转换能力）</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see PropertyTemplate
 * @see TemplateMapping
 * @see PropertyAccessor
 * @see MapPropertyMapping
 * @see ArrayPropertyMapping
 */
@FunctionalInterface
public interface PropertyMapping<V extends PropertyAccessor> extends PropertyTemplate<V>, TemplateMapping<V> {
    
    /**
     * 根据键获取属性访问器
     * <p>
     * 该方法优先使用{@link PropertyTemplate}的检索逻辑，支持通过数字索引、
     * 字符串名称或其他类型键检索属性访问器。
     * </p>
     * 
     * @param key 检索键，支持数字、字符串或其他类型
     * @return 匹配的属性访问器
     * @throws NoUniqueElementException 当键对应多个属性时抛出
     */
    @Override
    default V get(Object key) throws NoUniqueElementException {
        return PropertyTemplate.super.get(key);
    }

    /**
     * 转换为Map形式的属性映射
     * <p>
     * 创建新的{@link MapPropertyMapping}实例，可选择键唯一性约束。
     * 转换后的Map形式支持通过键快速检索属性访问器。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一（true表示键不可重复）
     * @return Map形式的属性映射实例
     */
    @Override
    default PropertyMapping<V> asMap(boolean uniqueness) {
        return new MapPropertyMapping<>(this, uniqueness);
    }

    /**
     * 转换为数组形式的属性映射
     * <p>
     * 创建新的{@link ArrayPropertyMapping}实例，可选择键唯一性约束。
     * 转换后的数组形式保持属性的插入顺序，适用于有序属性访问场景。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一（true表示键不可重复）
     * @return 数组形式的属性映射实例
     */
    @Override
    default PropertyMapping<V> asArray(boolean uniqueness) {
        return new ArrayPropertyMapping<>(this, uniqueness);
    }
}