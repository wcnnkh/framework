package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * Transformer接口定义了对象属性传输和转换的标准API，用于将源对象的属性值传输到目标对象。
 * 不同于Converter接口创建新对象，Transformer直接修改现有目标对象。
 * 
 * <p>Transformer接口提供了多种重载的transform方法，支持不同场景下的对象属性传输需求。
 * 它还提供了类型转换能力的判断功能。
 * 
 * <p>此接口采用函数式接口设计，允许通过lambda表达式实现简单的转换器。
 * 
 * @see TypeDescriptor
 */
@FunctionalInterface
public interface Transformer {
    /**
     * 返回一个忽略所有转换请求的转换器实例。
     * 该转换器的transform方法始终返回false，表示转换未执行。
     * 
     * @return IgnoreTransformer的单例实例
     */
    public static Transformer ignore() {
        return IgnoreTransformer.INSTANCE;
    }

    /**
     * 判断源类是否可以转换为目标类。
     * 此方法将类转换为TypeDescriptor后调用核心canTransform方法。
     * 
     * @param sourceClass 源类，不能为null
     * @param targetClass 目标类，不能为null
     * @return 是否可以进行转换
     */
    default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
        return canTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 判断源类是否可以转换为目标类型描述符所表示的类型。
     * 此方法将源类转换为TypeDescriptor后调用核心canTransform方法。
     * 
     * @param sourceClass 源类，不能为null
     * @param targetTypeDescriptor 目标类型描述符，不能为null
     * @return 是否可以进行转换
     */
    default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
        return canTransform(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
    }

    /**
     * 判断源类型描述符所表示的类型是否可以转换为目标类。
     * 此方法将目标类转换为TypeDescriptor后调用核心canTransform方法。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不能为null
     * @param targetClass 目标类，不能为null
     * @return 是否可以进行转换
     */
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
        return canTransform(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 判断源类型是否可以转换为目标类型。
     * 此默认实现返回true，表示所有类型转换都被允许。
     * 具体实现应根据转换器的实际能力重写此方法。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不能为null
     * @param targetTypeDescriptor 目标类型描述符，不能为null
     * @return 总是返回true（默认实现）
     */
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return true;
    }

    /**
     * 将源对象的属性值传输到目标对象。
     * 此方法通过类型描述符推断源和目标类型，并调用核心transform方法。
     * 
     * @param source 源对象，不能为null
     * @param target 目标对象，不能为null
     * @return 转换是否成功
     */
    default boolean transform(@NonNull Object source, @NonNull Object target) {
        return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定目标类型描述符。
     * 此方法通过类型描述符推断源类型，并调用核心transform方法。
     * 
     * @param source 源对象，不能为null
     * @param target 目标对象，不能为null
     * @param targetTypeDescriptor 目标类型描述符，不能为null
     * @return 转换是否成功
     */
    default boolean transform(@NonNull Object source, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return transform(source, TypeDescriptor.forObject(source), target, targetTypeDescriptor);
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定目标类。
     * 此方法通过类型描述符推断源类型，并调用核心transform方法。
     * 
     * @param <T> 目标类型
     * @param source 源对象，不能为null
     * @param target 目标对象，不能为null
     * @param targetClass 目标类，不能为null
     * @return 转换是否成功
     */
    default <T> boolean transform(@NonNull Object source, @NonNull T target, @NonNull Class<? extends T> targetClass) {
        return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定源类型描述符。
     * 此方法通过类型描述符推断目标类型，并调用核心transform方法。
     * 
     * @param source 源对象，不能为null
     * @param sourceTypeDescriptor 源类型描述符，不能为null
     * @param target 目标对象，不能为null
     * @return 转换是否成功
     */
    default boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target) {
        return transform(source, sourceTypeDescriptor, target, TypeDescriptor.forObject(target));
    }

    /**
     * 将源对象的属性值传输到目标对象的核心转换方法。
     * 所有其他转换方法最终都会调用此方法。
     * 
     * @param source 源对象，不能为null
     * @param sourceTypeDescriptor 源类型描述符，不能为null
     * @param target 目标对象，不能为null
     * @param targetTypeDescriptor 目标类型描述符，不能为null
     * @return 转换是否成功
     */
    boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor);

    /**
     * 将源对象的属性值传输到目标对象，并指定源类型描述符和目标类。
     * 
     * @param <T> 目标类型
     * @param source 源对象，不能为null
     * @param sourceTypeDescriptor 源类型描述符，不能为null
     * @param target 目标对象，不能为null
     * @param targetClass 目标类，不能为null
     * @return 转换是否成功
     */
    default <T> boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull T target, @NonNull Class<? extends T> targetClass) {
        return transform(source, sourceTypeDescriptor, target, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定源类。
     * 此方法通过类型描述符推断目标类型，并调用核心transform方法。
     * 
     * @param <S> 源类型
     * @param source 源对象，不能为null
     * @param sourceClass 源类，不能为null
     * @param target 目标对象，不能为null
     * @return 转换是否成功
     */
    default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定源类和目标类型描述符。
     * 
     * @param <S> 源类型
     * @param source 源对象，不能为null
     * @param sourceClass 源类，不能为null
     * @param target 目标对象，不能为null
     * @param targetTypeDescriptor 目标类型描述符，不能为null
     * @return 转换是否成功
     */
    default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, targetTypeDescriptor);
    }

    /**
     * 将源对象的属性值传输到目标对象，并指定源类和目标类。
     * 
     * @param <S> 源类型
     * @param <T> 目标类型
     * @param source 源对象，不能为null
     * @param sourceClass 源类，不能为null
     * @param target 目标对象，不能为null
     * @param targetClass 目标类，不能为null
     * @return 转换是否成功
     */
    default <S, T> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
            @NonNull Class<? extends T> targetClass) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.valueOf(targetClass));
    }
}