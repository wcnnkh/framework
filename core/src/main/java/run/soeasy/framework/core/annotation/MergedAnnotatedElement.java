package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * 合并注解元素实现类，实现{@link AnnotatedElement}接口，
 * 用于将多个注解元素（如类、方法、字段等）合并为一个逻辑注解元素，
 * 提供统一的注解访问接口，是框架中处理复合注解元素的核心组件。
 * <p>
 * 该类通过聚合多个{@link AnnotatedElement}实例，实现注解信息的合并查询，
 * 支持在多个元素中查找注解、检查注解存在性等操作，适用于需要处理多层注解的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注解元素合并：将多个注解元素的注解信息合并为统一视图</li>
 *   <li>分层查询策略：按顺序查询聚合的元素，返回首个匹配结果</li>
 *   <li>高效集合操作：使用流式处理优化多元素注解查询性能</li>
 *   <li>等值比较支持：基于聚合的元素集合实现hashCode和equals方法</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>复合注解处理：合并类及其父类的注解信息</li>
 *   <li>多层注解查询：同时查询方法、类和包上的注解</li>
 *   <li>AOP切面：合并目标对象和切面的注解信息</li>
 *   <li>元数据聚合：聚合多个元素的元数据注解</li>
 *   <li>插件系统：合并框架核心和插件的注解配置</li>
 * </ul>
 *
 * @author soeasy.run
 * @see AnnotatedElement
 * @see AnnotatedElementUtils
 */
@RequiredArgsConstructor
@Getter
public class MergedAnnotatedElement implements AnnotatedElement {
    /** 要合并的注解元素集合 */
    @NonNull
    private final Iterable<? extends AnnotatedElement> annotatedElements;

    /**
     * 获取指定类型的注解（从第一个元素开始查询）
     * <p>
     * 按顺序遍历所有注解元素，返回第一个包含该注解的元素的注解实例
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 找到的注解实例，未找到返回null
     */
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return AnnotatedElementUtils.getAnnotation(annotatedElements, annotationClass,
                (e) -> e.getAnnotation(annotationClass));
    }

    /**
     * 获取所有注解（合并所有元素的注解）
     * <p>
     * 合并所有注解元素的注解数组，去重后按顺序返回
     * 
     * @return 合并后的注解数组
     */
    @Override
    public Annotation[] getAnnotations() {
        return AnnotatedElementUtils.getAnnotations(annotatedElements, (e) -> e.getAnnotations())
                .toArray(new Annotation[0]);
    }

    /**
     * 获取指定类型的所有注解（合并所有元素的注解）
     * <p>
     * 合并所有注解元素的指定类型注解，去重后按顺序返回
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 合并后的指定类型注解数组
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return (T[]) AnnotatedElementUtils
                .getAnnotations(annotatedElements, (e) -> e.getAnnotationsByType(annotationClass)).toArray();
    }

    /**
     * 获取直接声明的指定类型注解（从第一个元素开始查询）
     * <p>
     * 按顺序遍历所有注解元素，返回第一个元素直接声明的该注解实例
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 找到的注解实例，未找到返回null
     */
    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return AnnotatedElementUtils.getAnnotation(annotatedElements, annotationClass,
                (e) -> e.getDeclaredAnnotation(annotationClass));
    }

    /**
     * 获取直接声明的所有注解（合并所有元素的注解）
     * <p>
     * 合并所有注解元素直接声明的注解数组，去重后按顺序返回
     * 
     * @return 合并后的直接声明注解数组
     */
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return AnnotatedElementUtils.getAnnotations(annotatedElements, (e) -> e.getDeclaredAnnotations())
                .toArray(new Annotation[0]);
    }

    /**
     * 获取直接声明的指定类型所有注解（合并所有元素的注解）
     * <p>
     * 合并所有注解元素直接声明的指定类型注解，去重后按顺序返回
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 合并后的直接声明指定类型注解数组
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return (T[]) AnnotatedElementUtils
                .getAnnotations(annotatedElements, (e) -> e.getDeclaredAnnotationsByType(annotationClass)).toArray();
    }

    /**
     * 检查是否存在指定类型的注解
     * <p>
     * 只要有一个注解元素存在该注解即返回true
     * 
     * @param annotationClass 注解类
     * @return 存在返回true，否则返回false
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        Iterator<? extends AnnotatedElement> iterator = annotatedElements.iterator();
        if (iterator == null) {
            return false;
        }

        while (iterator.hasNext()) {
            AnnotatedElement annotatedElement = iterator.next();
            if (annotatedElement == null) {
                continue;
            }

            if (annotatedElement.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算合并注解元素的哈希值
     * <p>
     * 哈希值基于聚合的注解元素集合计算
     * 
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return CollectionUtils.hashCode(annotatedElements);
    }

    /**
     * 判断与其他对象的相等性
     * <p>
     * 当且仅当对象为MergedAnnotatedElement且聚合的注解元素集合相等时返回true
     * 
     * @param obj 待比较对象
     * @return 相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof MergedAnnotatedElement) {
            return CollectionUtils.equals(annotatedElements, ((MergedAnnotatedElement) obj).annotatedElements,
                    ObjectUtils::equals);
        }
        return false;
    }
}