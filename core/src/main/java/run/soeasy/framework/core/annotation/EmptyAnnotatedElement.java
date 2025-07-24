package run.soeasy.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 空注解元素实现，用于表示不包含任何注解的元素。
 * 该类作为{@link AnnotatedElement}接口的空实现，
 * 所有注解查询方法均返回空值或空数组，表明其代表的元素上没有任何注解。
 *
 * <p>该类实现了{@link Serializable}接口，
 * 序列化版本UID为{@value #serialVersionUID}。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>无注解表示：用于在反射API中表示没有任何注解的元素</li>
 *   <li>单例行为：所有方法返回相同的空结果，不持有状态</li>
 *   <li>性能优化：避免为无注解元素创建复杂的注解处理逻辑</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射API中作为默认的无注解元素实现</li>
 *   <li>注解处理时表示注解缺失的情况</li>
 *   <li>优化性能：避免为无注解元素创建不必要的注解对象</li>
 * </ul>
 *
 * @author soeasy.run
 * @see AnnotatedElement
 * @see Serializable
 */
class EmptyAnnotatedElement implements AnnotatedElement, Serializable {
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取该元素上指定类型的注解。
     * 由于此类表示不包含任何注解的元素，
     * 因此该方法始终返回{@code null}。
     *
     * @param <T> 注解类型，必须是{@link Annotation}的子类
     * @param annotationClass 注解类的Class对象，指定要查找的注解类型
     * @return 始终返回{@code null}，表示该元素上没有任何注解
     */
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }

    /**
     * 获取该元素上存在的所有注解。
     * 由于此类表示不包含任何注解的元素，
     * 因此该方法始终返回空数组。
     *
     * @return 空的注解数组，类型为{@link Annotation}[]
     */
    @Override
    public Annotation[] getAnnotations() {
        return AnnotationUtils.EMPTY;
    }

    /**
     * 获取该元素上直接声明的所有注解。
     * 由于此类表示不包含任何注解的元素，
     * 因此该方法始终返回空数组。
     *
     * @return 空的注解数组，类型为{@link Annotation}[]
     */
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return AnnotationUtils.EMPTY;
    }
}