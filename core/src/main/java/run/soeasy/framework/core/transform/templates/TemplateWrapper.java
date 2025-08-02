package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.DictionaryWrapper;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 模板包装器接口，继承自{@link Template}和{@link DictionaryWrapper}，
 * 用于包装基础模板实例并添加额外功能或修改行为，遵循包装器设计模式。
 * <p>
 * 该接口允许将一个模板实例包装为具有相同接口的新实例，从而在不修改原模板实现的前提下，
 * 实现功能增强（如日志记录、权限控制、数据验证等）。包装器会将所有操作委托给源模板实例，
 * 并可在委托前后添加自定义逻辑。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>包装器模式：通过{@link #getSource()}获取被包装的源模板实例</li>
 *   <li>透明代理：对外暴露与源模板一致的接口，客户端无感知</li>
 *   <li>功能增强：可在委托操作前后添加额外逻辑（如验证、转换等）</li>
 *   <li>集合转换：继承{@link Template}的集合转换方法，委托给源模板处理</li>
 * </ul>
 *
 * @param <E> 模板元素的类型，必须实现{@link AccessibleDescriptor}接口
 * @param <W> 包装器自身的类型，需实现{@link Template}
 * 
 * @author soeasy.run
 * @see Template
 * @see DictionaryWrapper
 */
@FunctionalInterface
public interface TemplateWrapper<E extends AccessibleDescriptor, W extends Template<E>>
        extends Template<E>, DictionaryWrapper<Object, E, KeyValue<Object, E>, W> {

    /**
     * 将模板转换为Map形式（委托给源模板）
     * <p>
     * 该实现直接调用源模板的{@link Template#asMap(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的模板实例
     * @see Template#asMap(boolean)
     */
    @Override
    default Template<E> asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 将模板转换为数组形式（委托给源模板）
     * <p>
     * 该实现直接调用源模板的{@link Template#asArray(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的模板实例
     * @see Template#asArray(boolean)
     */
    @Override
    default Template<E> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }

    /**
     * 根据键获取元素（委托给源模板）
     * <p>
     * 该实现直接调用源模板的{@link Template#get(Object)}方法，
     * 包装器不做额外处理。
     * 
     * @param key 键对象，可以是数值类型或其他类型
     * @return 对应的唯一元素，若键不存在则返回null
     * @see Template#get(Object)
     */
    @Override
    default E get(Object key) {
        return getSource().get(key);
    }
}