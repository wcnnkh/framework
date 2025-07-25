package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayDictionary;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 数组形式的模板包装器，继承自{@link ArrayDictionary}并实现{@link TemplateWrapper}接口，
 * 用于将模板转换为数组形式的表示，并可选择强制键的唯一性。
 * <p>
 * 该类通过包装源模板实例，将其转换为基于数组的实现，保持元素的顺序性。
 * 当启用唯一性约束时，插入重复键将覆盖原有值；否则允许存在重复键。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>数组结构：基于数组实现，保持元素的插入顺序</li>
 *   <li>唯一性控制：通过构造参数控制键的唯一性</li>
 *   <li>包装器模式：保留对源模板的引用，支持链式操作</li>
 *   <li>集合转换：可根据需要转换为其他集合形式</li>
 * </ul>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>性能损耗：键查找为O(n)复杂度，不适合高频查找场景</li>
 *   <li>数组操作开销：插入/删除元素需移动后续元素，影响性能</li>
 *   <li>线程安全：未实现同步机制，多线程环境需外部同步</li>
 *   <li>空值风险：允许存储null值，取值时需额外校验</li>
 * </ul>
 *
 * @param <E> 模板元素类型，需实现{@link AccessibleDescriptor}
 * @param <W> 源模板类型，需实现{@link Template}
 * 
 * @author soeasy.run
 * @see ArrayDictionary
 * @see TemplateWrapper
 */
public class ArrayTemplate<E extends AccessibleDescriptor, W extends Template<E>>
        extends ArrayDictionary<Object, E, KeyValue<Object, E>, W> implements TemplateWrapper<E, W> {

    /**
     * 构造数组形式的模板包装器
     * 
     * @param source 源模板实例，不可为null
     * @param uniqueness 是否启用键唯一性约束
     * @throws NullPointerException 若source为null
     */
    public ArrayTemplate(@NonNull W source, boolean uniqueness) {
        super(source, uniqueness);
    }

    /**
     * 获取数组形式的模板实例
     * <p>
     * 若当前实例唯一性配置与参数一致则返回自身，否则委托源模板创建新实例
     * 
     * @param uniqueness 目标唯一性配置
     * @return 数组形式的模板实例
     */
    @Override
    public Template<E> asArray(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
    }
}