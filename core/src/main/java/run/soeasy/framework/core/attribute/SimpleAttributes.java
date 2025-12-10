package run.soeasy.framework.core.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.util.Elements;

import lombok.Data;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 简单属性实现类，基于LinkedHashMap实现可编辑属性功能。
 * 该类维护一个有序的键值对映射，支持属性的添加、查询、修改和删除操作，
 * 并保留属性的插入顺序。
 *
 * <p>核心特性：
 * <ul>
 *   <li>基于LinkedHashMap实现，保持属性的插入顺序</li>
 *   <li>延迟初始化：首次使用时才创建内部Map</li>
 *   <li>线程不安全：适合单线程环境使用，多线程环境需外部同步</li>
 *   <li>支持null值：允许属性值为null</li>
 *   <li>提供clearAttributes方法清空所有属性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>简单配置信息的存储和管理</li>
 *   <li>需要保持属性插入顺序的场景</li>
 *   <li>单线程环境下的属性操作</li>
 *   <li>测试环境或轻量级应用中的属性管理</li>
 * </ul>
 *
 * @param <K> 属性键的类型
 * @param <V> 属性值的类型
 * @see EditableAttributes
 * @see Elements
 */
@Data
public class SimpleAttributes<K, V> implements EditableAttributes<K, V> {
    /**
     * 存储属性的映射表，使用LinkedHashMap保持插入顺序。
     * 延迟初始化（首次使用时创建），初始容量为8。
     */
    private Map<K, V> attributeMap;

    /**
     * 创建空的SimpleAttributes实例。
     * 内部映射表将在首次使用时延迟初始化。
     */
    public SimpleAttributes() {
    }

    /**
     * 使用指定的映射表创建SimpleAttributes实例。
     * 注意：直接使用传入的映射表，不进行防御性复制。
     *
     * @param attributeMap 初始属性映射表，可为null
     */
    public SimpleAttributes(Map<K, V> attributeMap) {
        this.attributeMap = attributeMap;
    }

    /**
     * 获取指定名称的属性值。
     *
     * @param name 属性名称
     * @return 属性值，若属性不存在或映射表未初始化则返回null
     */
    @Override
    public V getAttribute(K name) {
        return attributeMap == null ? null : attributeMap.get(name);
    }

    /**
     * 获取所有属性名称的集合。
     *
     * @return 属性名称的集合，若映射表未初始化则返回空集合
     */
    @Override
    public Streamable<K> getAttributeNames() {
        return attributeMap == null ? Streamable.empty() : Streamable.of(attributeMap.keySet());
    }

    /**
     * 设置属性值。
     * 若映射表未初始化，则会创建一个新的LinkedHashMap。
     *
     * @param name  属性名称
     * @param value 属性值，可为null
     */
    @Override
    public void setAttribute(K name, V value) {
        if (attributeMap == null) {
            attributeMap = new LinkedHashMap<>(8);
        }
        attributeMap.put(name, value);
    }

    /**
     * 移除指定名称的属性。
     * 若映射表未初始化，则直接返回。
     *
     * @param name 要移除的属性名称
     */
    @Override
    public void removeAttribute(K name) {
        if (attributeMap == null) {
            return;
        }
        attributeMap.remove(name);
    }

    /**
     * 清空所有属性。
     * 若映射表未初始化，则直接返回。
     */
    public void clearAttributes() {
        if (attributeMap != null) {
            attributeMap.clear();
        }
    }
}