package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.transform.property.TypedProperties;

/**
 * 合成注解接口，作为动态合成注解的标记接口，提供静态工厂方法用于生成合成注解实例，
 * 是框架中动态注解生成的核心入口，支持基于现有注解集合或属性集合合成新的注解实例。
 * <p>
 * 该接口定义了合成注解的标准契约，合成的注解实例通过Java动态代理实现，
 * 允许在运行时动态构造注解，而无需在编译时定义具体注解类，适用于需要动态生成注解的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>动态合成：无需编译时定义，运行时基于现有注解或属性生成新注解</li>
 *   <li>两种合成方式：支持基于注解集合合并或属性集合映射</li>
 *   <li>代理实现：使用Java动态代理生成注解实例，方法调用委托给属性访问</li>
 *   <li>类型安全：通过泛型约束确保合成注解的类型一致性</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>动态注解生成：根据运行时条件动态构造自定义注解</li>
 *   <li>测试框架：动态生成测试所需的临时注解</li>
 *   <li>注解代理：为现有注解添加动态属性或行为</li>
 *   <li>元编程：基于属性集合生成注解元数据</li>
 *   <li>插件系统：插件动态生成框架可识别的注解</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Annotation
 * @see MergedAnnotation
 * @see CustomizeAnnotationPropertyMapping
 */
public interface SynthesizedAnnotation extends Annotation {

    /**
     * 基于现有注解集合合成新的注解实例
     * <p>
     * 该方法通过合并多个同类型注解生成一个逻辑注解，属性值按顺序合并，
     * 适用于需要组合多个注解属性的场景，如处理JDK 8+的重复注解或自定义组合注解。
     * </p>
     * 
     * @param <A> 合成的注解类型，必须是{@link Annotation}的子类
     * @param annotationType 目标注解类型
     * @param annotations 要合并的注解集合
     * @return 合成的注解实例
     * @see MergedAnnotation
     */
    public static <A extends Annotation> A synthesize(Class<A> annotationType, Iterable<? extends A> annotations) {
        return new MergedAnnotation<>(annotationType, annotations).synthesize();
    }

    /**
     * 基于属性集合合成新的注解实例
     * <p>
     * 该方法将属性集合映射为注解的属性值，支持任意{@link TypedProperties}实现，
     * 适用于将配置属性、元数据等转换为注解表示的场景，如动态构造注解实例。
     * </p>
     * 
     * @param <A> 合成的注解类型，必须是{@link Annotation}的子类
     * @param annotationType 目标注解类型
     * @param properties 属性集合
     * @return 合成的注解实例
     * @see CustomizeAnnotationPropertyMapping
     */
    public static <A extends Annotation> A synthesize(Class<A> annotationType, TypedProperties properties) {
        return new CustomizeAnnotationPropertyMapping<>(annotationType, properties).synthesize();
    }
}