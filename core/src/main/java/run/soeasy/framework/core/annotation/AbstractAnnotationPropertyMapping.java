package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.ArrayUtils;

/**
 * 注解属性映射抽象基类，实现{@link AnnotationProperties}接口，
 * 提供注解属性映射的基础实现，包括hashCode和equals方法的默认实现，
 * 是框架中构建具体注解属性映射器的抽象基类。
 * <p>
 * 该类基于注解类型和属性元素实现了hashCode和equals方法，确保相同注解类型和属性值的实例相等，
 * 并提供了合理的哈希值计算方式，适用于需要统一处理注解属性映射的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>标准化相等性：基于注解类型和属性值实现equals方法</li>
 *   <li>一致哈希值：基于注解类型和属性值计算hashCode</li>
 *   <li>抽象模板：定义了注解属性映射的基本框架，子类只需实现具体属性获取逻辑</li>
 * </ul>
 * </p>
 *
 * <p><b>实现说明：</b>
 * <ul>
 *   <li>equals方法：比较注解类型和所有属性值的相等性</li>
 *   <li>hashCode方法：结合注解类型和属性值计算哈希值</li>
 *   <li>属性比较：使用ObjectUtils.equals进行深层值比较</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AnnotationProperties
 * @see Annotation
 */
public abstract class AbstractAnnotationPropertyMapping<A extends Annotation> implements AnnotationProperties<A> {

    /**
     * 计算注解属性映射的哈希值
     * <p>
     * 哈希值计算基于：
     * <ol>
     *   <li>注解类型的哈希值</li>
     *   <li>所有属性元素的哈希值（通过ArrayUtils.hashCode计算）</li>
     * </ol>
     * </p>
     * 
     * @return 注解属性映射的哈希值
     */
    @Override
    public int hashCode() {
        return getType().hashCode() + ArrayUtils.hashCode(getElements().toList());
    }

    /**
     * 判断与其他对象的相等性
     * <p>
     * 相等性判断基于：
     * <ol>
     *   <li>对象引用相等</li>
     *   <li>注解类型相同</li>
     *   <li>所有属性值相同（使用ObjectUtils.equals进行深层比较）</li>
     * </ol>
     * </p>
     * 
     * @param obj 待比较的对象
     * @return 如果对象相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof AnnotationProperties) {
            AnnotationProperties<?> annotationProperties = (AnnotationProperties<?>) obj;
            return getType() == annotationProperties.getType() && getElements()
                    .equals(annotationProperties.getElements(), (a, b) -> ObjectUtils.equals(a.getKey(), b.getKey())
                            && ObjectUtils.equals(a.getValue().get(), b.getValue().get()));
        }
        return false;
    }
}