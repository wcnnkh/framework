package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 模板映射接口，继承自{@link Mapping}和{@link Template}，
 * 统一了映射和模板的操作接口，支持通过键或索引访问元素，
 * 并可转换为不同的集合形式。模板映射中的元素必须实现{@link TypedValueAccessor}接口，
 * 以提供类型化的值访问能力。
 * <p>
 * 该接口通过组合映射和模板的功能，允许在同一数据结构上使用两种访问模式：
 * <ul>
 *   <li>映射模式：通过键访问元素，支持键的唯一性约束</li>
 *   <li>模板模式：通过索引或键访问元素，支持混合访问模式</li>
 * </ul>
 * 同时提供集合转换功能，可将模板映射转换为数组或Map形式。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>混合访问模式：支持通过索引或键名访问元素</li>
 *   <li>集合转换：可将模板映射转换为数组或Map形式</li>
 *   <li>类型安全：元素必须实现{@link TypedValueAccessor}，确保类型化访问</li>
 *   <li>唯一性约束：通过参数控制键的唯一性</li>
 * </ul>
 *
 * @param <E> 模板映射元素的类型，必须实现{@link TypedValueAccessor}接口
 * 
 * @author soeasy.run
 * @see Mapping
 * @see Template
 * @see TypedValueAccessor
 */
@FunctionalInterface
public interface TemplateMapping<E extends TypedValueAccessor>
        extends Mapping<Object, E>, Template<E> {

    /**
     * 将模板映射转换为Map形式
     * <p>
     * 创建一个新的Map形式的模板映射，包装当前模板映射，并可选择是否强制键唯一性。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return Map形式的模板映射实例
     */
    @Override
    default TemplateMapping<E> asMap(boolean uniqueness) {
        // 注意：此处原代码实现可能存在类名混淆问题
        // 实际应返回Map形式的实现，而非ArrayTemplateMapping
        // 建议修改为MapTemplateMapping或其他合适的实现类
        return new MapTemplateMapping<>(this, uniqueness);
    }

    /**
     * 将模板映射转换为数组形式
     * <p>
     * 创建一个新的数组形式的模板映射，包装当前模板映射，并可选择是否强制键唯一性。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return 数组形式的模板映射实例
     */
    @Override
    default TemplateMapping<E> asArray(boolean uniqueness) {
        // 注意：此处原代码实现可能存在类名混淆问题
        // 实际应返回数组形式的实现，而非MapTemplateMapping
        // 建议修改为ArrayTemplateMapping或其他合适的实现类
        return new ArrayTemplateMapping<>(this, uniqueness);
    }

    /**
     * 根据键获取唯一元素（委托给Template接口实现）
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>若键为数值类型，将其转换为整数索引，通过索引获取元素</li>
     *   <li>若键为其他类型，通过键获取元素集合，并要求集合中元素唯一</li>
     * </ol>
     * 
     * @param key 键对象，可以是数值类型或其他类型
     * @return 对应的唯一元素，若键不存在则返回null
     * @throws NoUniqueElementException 当键对应多个元素时抛出
     * @throws IndexOutOfBoundsException 当数值键对应的索引超出范围时抛出
     */
    @Override
    default E get(Object key) throws NoUniqueElementException {
        // 委托给Template接口的默认实现
        return Template.super.get(key);
    }
}