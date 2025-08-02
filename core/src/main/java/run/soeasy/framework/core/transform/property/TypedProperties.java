package run.soeasy.framework.core.transform.property;

/**
 * 类型化属性接口，继承自{@link PropertyMapping}，用于表示具有类型信息的属性集合，
 * 支持将属性映射转换为Map或数组形式，并保持类型化操作能力。
 * <p>
 * 该接口扩展了属性映射的功能，提供类型化的属性访问能力，适用于需要精确控制
 * 属性类型的场景，如数据绑定、类型转换、泛型操作等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型化访问：继承{@link PropertyMapping}的所有功能，提供类型化属性操作</li>
 *   <li>结构转换：支持通过{@link #asMap(boolean)}和{@link #asArray(boolean)}方法转换为不同结构</li>
 *   <li>函数式接口：作为函数式接口可通过lambda表达式创建实例</li>
 *   <li>链式调用：转换方法返回{@link TypedProperties}类型，支持链式操作</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>泛型属性操作：需要精确控制属性类型的场景</li>
 *   <li>类型安全的数据绑定：确保属性值类型匹配的场景</li>
 *   <li>动态类型转换：根据类型信息进行属性值转换的场景</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see MapTypedProperties
 * @see ArrayTypedProperties
 */
@FunctionalInterface
public interface TypedProperties extends PropertyMapping<PropertyAccessor> {
    
    /**
     * 转换为Map形式的类型化属性
     * <p>
     * 创建新的{@link MapTypedProperties}实例，可选择键唯一性约束。
     * 转换后的Map形式支持通过键快速检索属性访问器，并保持类型化操作能力。
     * 
     * @param uniqueness 是否要求键唯一（true表示键不可重复）
     * @return Map形式的类型化属性实例
     */
    @Override
    default TypedProperties asMap(boolean uniqueness) {
        return new MapTypedProperties<>(this, uniqueness);
    }

    /**
     * 转换为数组形式的类型化属性
     * <p>
     * 创建新的{@link ArrayTypedProperties}实例，可选择键唯一性约束。
     * 转换后的数组形式保持属性的插入顺序，支持通过索引访问属性，并保持类型化操作能力。
     * 
     * @param uniqueness 是否要求键唯一（true表示键不可重复）
     * @return 数组形式的类型化属性实例
     */
    @Override
    default TypedProperties asArray(boolean uniqueness) {
        return new ArrayTypedProperties<>(this, uniqueness);
    }
}