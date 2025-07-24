package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.Dictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 键值映射接口，定义类型安全的键值对映射操作，继承自{@link Dictionary}接口。
 * <p>
 * 该接口用于表示具有类型约束的键值映射关系，其中值类型{@code V}必须实现{@link TypedValueAccessor}接口，
 * 支持类型安全的值访问和转换。适用于数据转换、对象映射等需要类型化键值操作的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型约束：值类型必须为{@link TypedValueAccessor}的实现类，支持类型化值操作</li>
 *   <li>集合转换：通过{@link #asMap(boolean)}和{@link #asArray(boolean)}方法转换为不同集合形式</li>
 *   <li>唯一性控制：转换方法支持通过{@code uniqueness}参数控制键的唯一性</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>泛型约束限制：值类型{@code V}被强制约束为{@link TypedValueAccessor}，可能限制使用场景</li>
 *   <li>默认方法实现依赖：{@link #asMap(boolean)}和{@link #asArray(boolean)}依赖未公开的实现类</li>
 *   <li>唯一性逻辑不明确：{@code uniqueness}参数的具体语义未在接口中定义，依赖实现类解释</li>
 *   <li>函数式接口设计：作为函数式接口但未定义抽象方法（依赖父接口），可能导致设计混淆</li>
 * </ul>
 * </p>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型，必须实现{@link TypedValueAccessor}接口
 * 
 * @author soeasy.run
 * @see Dictionary
 * @see TypedValueAccessor
 * @see KeyValue
 */
@FunctionalInterface
public interface Mapping<K, V extends TypedValueAccessor> extends Dictionary<K, V, KeyValue<K, V>> {

    /**
     * 将当前映射转换为Map形式的映射
     * <p>
     * 创建一个新的{@link MapMapping}实例，包装当前映射，并支持键唯一性控制。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return Map形式的映射实例
     */
    @Override
    default Mapping<K, V> asMap(boolean uniqueness) {
        return new MapMapping<>(this, true, uniqueness);
    }

    /**
     * 将当前映射转换为数组形式的映射
     * <p>
     * 创建一个新的{@link ArrayMapping}实例，包装当前映射，并支持键唯一性控制。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return 数组形式的映射实例
     */
    @Override
    default Mapping<K, V> asArray(boolean uniqueness) {
        return new ArrayMapping<>(this, uniqueness);
    }
}