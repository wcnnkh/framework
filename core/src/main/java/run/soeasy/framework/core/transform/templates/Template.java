package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.Dictionary;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 模板接口，定义基于键值对的模板结构，支持通过索引或键名访问元素，
 * 并可转换为不同的集合形式。模板中的元素必须实现{@link AccessibleDescriptor}接口，
 * 以提供类型化的值访问能力。
 * <p>
 * 该接口继承自{@link Dictionary}，提供了字典的基本操作，并扩展了基于索引的访问方式，
 * 允许通过数值索引快速定位元素。同时支持将模板转换为数组或Map形式，方便不同场景下的使用。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>混合访问模式：支持通过数值索引或键名访问元素</li>
 *   <li>集合转换：可将模板转换为数组或Map形式</li>
 *   <li>类型安全：元素必须实现{@link AccessibleDescriptor}，确保类型化访问</li>
 *   <li>唯一性约束：通过参数控制键的唯一性</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>类型转换异常：当键为数值类型但索引越界时，可能抛出{@link IndexOutOfBoundsException}</li>
 *   <li>非唯一元素：当键对应多个元素且调用{@code get()}时，抛出{@link NoUniqueElementException}</li>
 *   <li>空值处理：未对键不存在的情况做特殊处理，可能返回null</li>
 *   <li>性能考虑：基于索引的访问依赖于内部元素顺序，可能影响性能</li>
 * </ul>
 * </p>
 *
 * @param <E> 模板元素的类型，必须实现{@link AccessibleDescriptor}接口
 * 
 * @author soeasy.run
 * @see Dictionary
 * @see AccessibleDescriptor
 * @see KeyValue
 */
@FunctionalInterface
public interface Template<E extends AccessibleDescriptor> extends Dictionary<Object, E, KeyValue<Object, E>> {

    /**
     * 将模板转换为数组形式
     * <p>
     * 创建一个新的数组形式的模板，包装当前模板，并可选择是否强制键唯一性。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return 数组形式的模板实例
     */
    @Override
    default Template<E> asArray(boolean uniqueness) {
        return new ArrayTemplate<>(this, uniqueness);
    }

    /**
     * 将模板转换为Map形式
     * <p>
     * 创建一个新的Map形式的模板，包装当前模板，并可选择是否强制键唯一性。
     * 
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @return Map形式的模板实例
     */
    @Override
    default Template<E> asMap(boolean uniqueness) {
        return new MapTemplate<>(this, uniqueness);
    }

    /**
     * 根据键获取唯一元素
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
    default E get(Object key) throws NoUniqueElementException {
        if (key instanceof Number) {
            // 处理数值类型的键，将其作为索引使用
            KeyValue<Object, E> element = getElement(((Number) key).intValue());
            return element == null ? null : element.getValue();
        } else {
            // 处理其他类型的键，要求获取的元素集合是唯一的
            Elements<E> values = getValues(key);
            if (values == null) {
                return null;
            }
            return values.getUnique();
        }
    }
}