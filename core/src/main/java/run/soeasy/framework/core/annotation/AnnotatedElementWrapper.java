package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 带注解元素包装器函数式接口，继承自{@link AnnotatedElement}和{@link Wrapper}，
 * 采用装饰器模式对带注解的元素（如类、方法、字段等）进行包装，
 * 支持在不修改原始元素的前提下为其添加额外的注解处理逻辑。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强注解处理能力的场景，如注解值修改、条件注解过滤、注解元数据增强等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始带注解元素并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将注解查询调用转发给被包装的源元素，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的带注解元素类型，需实现{@link AnnotatedElement}</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>注解值修改：在运行时动态修改注解的属性值</li>
 *   <li>条件注解过滤：根据运行时条件决定是否返回某个注解</li>
 *   <li>注解元数据增强：为注解添加额外的元数据信息</li>
 *   <li>注解组合：将多个注解组合为一个逻辑注解</li>
 *   <li>兼容性处理：为不支持重复注解的旧版本JDK提供重复注解支持</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AnnotatedElement
 * @see Wrapper
 */
@FunctionalInterface
public interface AnnotatedElementWrapper<W extends AnnotatedElement> extends AnnotatedElement, Wrapper<W> {

    /**
     * 获取被包装元素上的指定类型注解
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getAnnotation(Class)}方法，
     * 子类可覆盖此方法添加额外逻辑，如条件注解过滤或注解值修改。
     * </p>
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 存在则返回该类型注解，否则返回null
     */
    @Override
    default <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return getSource().getAnnotation(annotationClass);
    }

    /**
     * 获取被包装元素上直接声明的所有注解
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getDeclaredAnnotations()}方法，
     * 子类可覆盖此方法添加额外逻辑，如注解过滤或添加虚拟注解。
     * </p>
     * 
     * @return 直接声明的注解数组
     */
    @Override
    default Annotation[] getDeclaredAnnotations() {
        return getSource().getDeclaredAnnotations();
    }

    /**
     * 获取被包装元素上的所有注解（包括继承的）
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getAnnotations()}方法，
     * 子类可覆盖此方法添加额外逻辑，如注解增强或元数据合并。
     * </p>
     * 
     * @return 所有注解数组
     */
    @Override
    default Annotation[] getAnnotations() {
        return getSource().getAnnotations();
    }

    /**
     * 获取被包装元素上的指定类型的所有注解（包括继承的）
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getAnnotationsByType(Class)}方法，
     * 子类可覆盖此方法添加额外逻辑，如重复注解处理或条件注解过滤。
     * </p>
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 存在则返回该类型的所有注解，否则返回空数组
     */
    @Override
    default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return getSource().getAnnotationsByType(annotationClass);
    }

    /**
     * 获取被包装元素上直接声明的指定类型注解
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getDeclaredAnnotation(Class)}方法，
     * 子类可覆盖此方法添加额外逻辑，如注解代理或虚拟注解生成。
     * </p>
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 存在则返回该类型注解，否则返回null
     */
    @Override
    default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return getSource().getDeclaredAnnotation(annotationClass);
    }

    /**
     * 获取被包装元素上直接声明的指定类型的所有注解
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#getDeclaredAnnotationsByType(Class)}方法，
     * 子类可覆盖此方法添加额外逻辑，如注解合并或条件注解过滤。
     * </p>
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 存在则返回该类型的所有注解，否则返回空数组
     */
    @Override
    default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return getSource().getDeclaredAnnotationsByType(annotationClass);
    }

    /**
     * 判断被包装元素上是否存在指定类型的注解
     * <p>
     * 该默认实现将调用转发给被包装的源元素的{@link AnnotatedElement#isAnnotationPresent(Class)}方法，
     * 子类可覆盖此方法添加额外逻辑，如基于条件的注解判断。
     * </p>
     * 
     * @param annotationClass 注解类
     * @return 存在则返回true，否则返回false
     */
    @Override
    default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getSource().isAnnotationPresent(annotationClass);
    }
}