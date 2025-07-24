package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * Map形式的模板包装器，继承自{@link MapDictionary}并实现{@link TemplateWrapper}接口，
 * 用于将模板转换为Map形式的表示，并可选择强制键的唯一性。
 * <p>
 * 该类通过包装源模板实例，将其转换为基于Map的实现，提供快速的键值访问能力。
 * 当启用唯一性约束时，插入重复键将覆盖原有值；否则允许存在重复键。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>Map结构：基于Map实现，提供O(1)时间复杂度的键值访问</li>
 *   <li>唯一性约束：通过构造参数控制键的唯一性</li>
 *   <li>包装器模式：保留对源模板的引用，支持链式操作</li>
 *   <li>集合转换：可根据需要转换回其他集合形式</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>顺序丢失：Map不保证元素顺序，可能导致与源模板顺序不一致</li>
 *   <li>哈希冲突：若键的哈希函数不合理，可能影响性能</li>
 *   <li>线程安全：未实现线程安全机制，多线程环境下需外部同步</li>
 *   <li>空值处理：允许存储null值，可能导致空指针异常</li>
 * </ul>
 * </p>
 *
 * @param <E> 模板元素的类型，必须实现{@link AccessibleDescriptor}接口
 * @param <W> 源模板的类型，需实现{@link Template<E>}
 * 
 * @author soeasy.run
 * @see MapDictionary
 * @see TemplateWrapper
 * @see AccessibleDescriptor
 */
public class MapTemplate<E extends AccessibleDescriptor, W extends Template<E>>
        extends MapDictionary<Object, E, KeyValue<Object, E>, W> implements TemplateWrapper<E, W> {

    /**
     * 构造一个Map形式的模板包装器
     * 
     * @param source 源模板实例，不可为null
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @throws NullPointerException 若源模板为null
     */
    public MapTemplate(W source, boolean uniqueness) {
        super(source, false, uniqueness);
    }

    /**
     * 将模板转换为Map形式
     * <p>
     * 若当前实例已满足唯一性要求（根据构造时的参数），则返回自身；
     * 否则委托给源模板创建新的Map形式实例。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的模板实例
     */
    @Override
    public Template<E> asMap(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
    }

    /**
     * 重写toString方法，提供更友好的字符串表示
     * 
     * @return 包含模板元素信息的字符串
     */
    @Override
    public String toString() {
        return "MapTemplate{" +
                "size=" + size() +
                ", uniqueness=" + isUniqueness() +
                ", source=" + (getSource() != null ? getSource().getClass().getSimpleName() : "null") +
                '}';
    }
}