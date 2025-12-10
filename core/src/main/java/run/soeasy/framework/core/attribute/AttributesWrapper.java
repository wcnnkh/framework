package run.soeasy.framework.core.attribute;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 属性包装器接口，用于对Attributes实例进行统一封装和操作委托。
 * 该接口继承自Attributes和Wrapper，提供了对底层Attributes的透明包装，
 * 所有操作默认委托给被包装的源Attributes对象，支持属性的获取和名称遍历。
 *
 * <p>设计特点：
 * <ul>
 *   <li>函数式接口设计：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>包装模式实现：通过getSource()获取被包装的源Attributes实例</li>
 *   <li>操作委托机制：所有方法默认委派给源Attributes的对应实现</li>
 *   <li>类型安全的泛型设计：确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要为Attributes添加额外功能（如日志记录、权限校验）</li>
 *   <li>需要统一处理不同类型的Attributes实现</li>
 *   <li>需要通过装饰器模式增强Attributes功能</li>
 *   <li>需要在不修改原始实现的情况下扩展Attributes功能</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的Attributes类型，必须实现Attributes接口
 * @see Attributes
 * @see Wrapper
 */
public interface AttributesWrapper<K, V, W extends Attributes<K, V>> extends Attributes<K, V>, Wrapper<W> {

    /**
     * 获取指定名称的属性值，委托给源Attributes的getAttribute方法。
     *
     * @param name 属性名称
     * @return 属性值，若不存在则返回null
     */
    @Override
    default V getAttribute(K name) {
        return getSource().getAttribute(name);
    }

    /**
     * 获取所有属性名称的集合，委托给源Attributes的getAttributeNames方法。
     *
     * @return 属性名称的Elements集合
     */
    @Override
    default Streamable<K> getAttributeNames() {
        return getSource().getAttributeNames();
    }
}