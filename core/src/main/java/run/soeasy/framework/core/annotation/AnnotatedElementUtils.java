package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.UnsafeArrayList;

/**
 * 注解元素工具类，提供操作多个{@link AnnotatedElement}的静态方法，
 * 支持在多个注解元素中查询注解、合并注解数组等操作，是框架中处理复合注解元素的核心工具类。
 * <p>
 * 该类采用函数式接口作为处理器，实现注解查询逻辑的灵活定制，
 * 支持将多个元素的注解信息合并为统一结果，适用于需要处理多层注解的复杂场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>多元素注解查询：在多个注解元素中查询指定类型的注解</li>
 *   <li>注解合并：自动合并多个元素的同类型注解</li>
 *   <li>函数式处理：通过Function接口定制注解提取逻辑</li>
 *   <li>空安全处理：内置空元素常量{@link #EMPTY_ANNOTATED_ELEMENT}处理空场景</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>复合注解提取：从类、接口、方法等多个元素中提取注解</li>
 *   <li>多层注解合并：合并父类、接口、当前类的同类型注解</li>
 *   <li>AOP切面注解聚合：聚合目标对象和切面的注解信息</li>
 *   <li>插件系统注解整合：合并框架核心和插件的注解配置</li>
 *   <li>动态注解合成：基于多个元素的注解动态生成合成注解</li>
 * </ul>
 *
 * @author soeasy.run
 * @see AnnotatedElement
 * @see SynthesizedAnnotation
 */
@UtilityClass
public class AnnotatedElementUtils {
    /** 空注解元素实例，用于空场景处理 */
    public static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new EmptyAnnotatedElement();

    /**
     * 在多个注解元素中查询指定类型的注解（支持合并多个注解）
     * <p>
     * 执行流程：
     * <ol>
     *   <li>按顺序遍历所有注解元素</li>
     *   <li>对每个元素应用处理器函数提取注解</li>
     *   <li>若首次找到注解则记录，后续找到的同类型注解将被收集</li>
     *   <li>若收集到多个注解，使用{@link SynthesizedAnnotation#synthesize(Class, Iterable)}合成新注解</li>
     * </ol>
     * 
     * @param <A> 注解元素类型，继承自{@link AnnotatedElement}
     * @param <T> 注解类型，继承自{@link Annotation}
     * @param annotatedElements 注解元素集合，不可为null
     * @param annotationClass 目标注解类，不可为null
     * @param processor 注解提取处理器，参数为注解元素，返回对应注解
     * @return 找到的注解实例，若有多个则返回合成注解，未找到返回null
     */
    public static <A extends AnnotatedElement, T extends Annotation> T getAnnotation(
            @NonNull Iterable<? extends A> annotatedElements, @NonNull Class<T> annotationClass,
            @NonNull Function<? super A, ? extends T> processor) {
        T annotation = null;
        List<T> list = null;
        for (A annotatedElement : annotatedElements) {
            if (annotatedElement == null) {
                continue;
            }

            T ann = processor.apply(annotatedElement);
            if (ann == null) {
                continue;
            }

            if (annotation == null) {
                annotation = ann;
            } else {
                if (list == null) {
                    list = new ArrayList<>(2);
                    list.add(annotation);
                }
                list.add(ann);
            }
        }

        if (list == null) {
            return annotation;
        }

        if (list.size() == 1) {
            return list.get(0);
        }
        return SynthesizedAnnotation.synthesize(annotationClass, list);
    }

    /**
     * 在多个注解元素中收集指定类型的注解数组（合并所有元素的注解）
     * <p>
     * 执行流程：
     * <ol>
     *   <li>按顺序遍历所有注解元素</li>
     *   <li>对每个元素应用处理器函数提取注解数组</li>
     *   <li>收集所有非空数组的注解到统一列表</li>
     *   <li>返回合并后的注解列表，元素顺序与遍历顺序一致</li>
     * </ol>
     * 
     * @param <A> 注解元素类型，继承自{@link AnnotatedElement}
     * @param <T> 注解类型
     * @param annotatedElements 注解元素集合，不可为null
     * @param processor 注解数组提取处理器，参数为注解元素，返回对应注解数组
     * @return 合并后的注解列表，若无注解返回空列表
     */
    public static <A extends AnnotatedElement, T> List<T> getAnnotations(
            @NonNull Iterable<? extends A> annotatedElements, @NonNull Function<? super A, ? extends T[]> processor) {
        T[] first = null;
        List<T> list = null;
        for (A annotatedElement : annotatedElements) {
            if (annotatedElement == null) {
                continue;
            }

            T[] array = processor.apply(annotatedElement);
            if (array == null || array.length == 0) {
                continue;
            }

            if (first == null) {
                first = array;
            } else {
                if (list == null) {
                    list = new ArrayList<>(4);
                    list.addAll(new UnsafeArrayList<>(first));
                }
                list.addAll(new UnsafeArrayList<>(array));
            }
        }
        return list == null ? (first == null ? Collections.emptyList() : new UnsafeArrayList<>(first)) : list;
    }
}