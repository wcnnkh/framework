package run.soeasy.framework.core.attribute;

import run.soeasy.framework.core.collection.Elements;

/**
 * 属性访问接口，定义了属性的基本操作契约。
 * 该接口提供了获取属性值和属性名称集合的方法，
 * 适用于需要统一管理键值对属性的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>定义属性的基本访问接口，支持通过键获取值</li>
 *   <li>提供属性名称的集合视图，便于遍历和管理</li>
 *   <li>不强制要求属性的可修改性，实现可以是只读的</li>
 *   <li>属性名称和值的类型由泛型参数灵活定义</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>配置信息的访问和管理</li>
 *   <li>上下文环境变量的存储和获取</li>
 *   <li>组件元数据的定义和查询</li>
 *   <li>请求/响应头信息的处理</li>
 * </ul>
 *
 * @param <K> 属性键的类型
 * @param <V> 属性值的类型
 * @see Elements
 * @see AttributesWrapper
 */
public interface Attributes<K, V> {

    /**
     * 获取指定名称的属性值。
     * 若属性不存在，通常返回null，但具体实现可能有不同行为。
     * 建议实现类在文档中明确说明属性不存在时的返回策略。
     *
     * @param name 属性名称，不可为null
     * @return 属性值，若不存在则返回null或根据实现约定返回特定值
     */
    V getAttribute(K name);

    /**
     * 获取所有属性名称的集合。
     * 返回的Elements集合支持流式操作和集合转换，
     * 可以用于遍历所有属性名称或进行批量操作。
     *
     * @return 属性名称的集合，不会返回null
     */
    Elements<K> getAttributeNames();
}