package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * 注解工具类，提供注解处理的实用方法。
 * 该类包含一组静态方法，用于操作、查找和合成注解实例，
 * 是框架中注解处理的核心工具类。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>注解查找：在注解集合中查找指定类型的注解</li>
 *   <li>注解合成：基于现有注解生成新的合成注解</li>
 *   <li>空注解处理：提供空注解数组常量以避免重复创建</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Annotation
 * @see SynthesizedAnnotation
 */
@UtilityClass
public class AnnotationUtils {
    /**
     * 空注解数组常量，用于表示没有注解的情况。
     * 避免在多个地方创建空数组实例，优化内存使用。
     */
    public static final Annotation[] EMPTY = new Annotation[0];

    /**
     * 在给定的注解数组中查找指定类型的注解。
     * 如果找到多个相同类型的注解，则将它们合成为一个合成注解实例。
     *
     * <p>该方法会遍历所有注解，找出与指定类型匹配的注解：
     * <ul>
     *   <li>如果未找到匹配的注解，返回{@code null}</li>
     *   <li>如果找到一个匹配的注解，直接返回该注解</li>
     *   <li>如果找到多个匹配的注解，使用{@link SynthesizedAnnotation}合成一个新的注解实例</li>
     * </ul>
     *
     * @param <A> 注解类型，必须是{@link Annotation}的子类
     * @param annotationType 要查找的注解类型的Class对象
     * @param annotations 要搜索的注解数组
     * @return 单个注解实例（可能是合成的），或{@code null}（如果未找到）
     * @throws IllegalArgumentException 如果传入的注解类型为{@code null}
     * @see SynthesizedAnnotation#synthesize(Class, Iterable)
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Class<A> annotationType, Annotation... annotations) {
        List<A> list = null;
        for (Annotation ann : annotations) {
            if (ann.annotationType() == annotationType) {
                list = new ArrayList<>(annotations.length);
                list.add((A) ann);
            }
        }
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return SynthesizedAnnotation.synthesize(annotationType, list);
    }
}