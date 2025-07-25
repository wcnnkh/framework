package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 对象属性转换器接口，定义了将源对象属性值传输到目标对象的标准API。
 * <p>
 * 与{@link run.soeasy.framework.core.convert.Converter}不同，Transformer直接修改目标对象而非创建新实例，
 * 适用于对象属性映射、数据绑定等需要原地修改的场景。接口采用函数式设计，支持通过lambda表达式快速实现简单转换逻辑。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>属性传输：将源对象属性值转换并设置到目标对象</li>
 *   <li>类型判断：提供多维度的类型转换可行性判断</li>
 *   <li>重载支持：多种参数组合的transform方法，适配不同场景</li>
 *   <li>单例实例：通过{@link #ignore()}提供忽略转换的默认实现</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * // 简单属性复制转换器
 * Transformer beanTransformer = (s, sType, t, tType) -&gt; {
 *     tType.getObject().getDeclaredField("name").set(t, sType.getObject().getDeclaredField("name").get(s));
 *     return true;
 * };
 * }</pre>
 *
 * @author soeasy.run
 * @see run.soeasy.framework.core.convert.Converter
 * @see TypeDescriptor
 */
@FunctionalInterface
public interface Transformer {

    /**
     * 返回忽略所有转换请求的单例转换器。
     * <p>
     * 该转换器的{@link #transform(Object, Object)}方法始终返回false，
     * 适用于需要占位或禁用转换的场景。
     * 
     * @return 忽略转换的单例实例
     * @see IgnoreTransformer
     */
    public static Transformer ignore() {
        return IgnoreTransformer.INSTANCE;
    }

    /**
     * 判断源类与目标类之间是否支持类型转换。
     * <p>
     * 内部通过{@link TypeDescriptor#valueOf(Class)}转换为类型描述符后，
     * 调用{@link #canTransform(TypeDescriptor, TypeDescriptor)}执行核心判断。
     * 
     * @param sourceClass 源类型Class，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 若支持转换返回true，否则false
     * @throws NullPointerException 若参数为null
     */
    default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
        return canTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 判断源类与目标类型描述符之间是否支持转换。
     * <p>
     * 将源类转换为类型描述符后，调用核心判断方法，
     * 适用于目标类型需要更精确描述的场景（如泛型类型）。
     * 
     * @param sourceClass 源类型Class，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若支持转换返回true，否则false
     * @throws NullPointerException 若参数为null
     */
    default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
        return canTransform(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
    }

    /**
     * 判断源类型描述符与目标类之间是否支持转换。
     * <p>
     * 将目标类转换为类型描述符后，调用核心判断方法，
     * 适用于源类型需要更精确描述的场景（如泛型类型）。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 若支持转换返回true，否则false
     * @throws NullPointerException 若参数为null
     */
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
        return canTransform(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 类型转换可行性判断的核心方法。
     * <p>
     * <b>默认实现始终返回true</b>，具体实现需根据转换器能力重写此方法。
     * 建议实现中检查源类型与目标类型的兼容性（如继承关系、转换器注册等）。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 类型转换是否可行（默认返回true）
     * @throws NullPointerException 若参数为null
     */
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return true;
    }

    /**
     * 执行对象属性转换的便捷方法。
     * <p>
     * 自动推断源类型和目标类型，调用核心转换方法{@link #transform(Object, TypeDescriptor, Object, TypeDescriptor)}。
     * 适用于源类型和目标类型可通过对象实例自动推断的简单场景。
     * 
     * @param source 源对象，不可为null
     * @param target 目标对象，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default boolean transform(@NonNull Object source, @NonNull Object target) {
        return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
    }

    /**
     * 指定目标类型的属性转换方法。
     * <p>
     * 自动推断源类型，调用核心转换方法，
     * 适用于目标类型需要显式指定的场景（如目标对象为泛型类型）。
     * 
     * @param source 源对象，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default boolean transform(@NonNull Object source, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return transform(source, TypeDescriptor.forObject(source), target, targetTypeDescriptor);
    }

    /**
     * 指定目标类型Class的属性转换方法。
     * <p>
     * 自动推断源类型，将目标Class转换为类型描述符后调用核心方法，
     * 适用于通过Class指定目标类型的场景。
     * 
     * @param <T> 目标类型泛型参数
     * @param source 源对象，不可为null
     * @param target 目标对象，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default <T> boolean transform(@NonNull Object source, @NonNull T target, @NonNull Class<? extends T> targetClass) {
        return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 指定源类型描述符的属性转换方法。
     * <p>
     * 自动推断目标类型，调用核心转换方法，
     * 适用于源类型需要显式指定的场景（如源对象为泛型类型）。
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target) {
        return transform(source, sourceTypeDescriptor, target, TypeDescriptor.forObject(target));
    }

    /**
     * 核心属性转换方法。
     * <p>
     * 所有重载的transform方法最终均调用此方法，
     * 由具体实现定义属性转换的核心逻辑（如反射赋值、类型转换等）。
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor);

    /**
     * 指定源类型描述符和目标类型Class的属性转换方法。
     * <p>
     * 将目标Class转换为类型描述符后调用核心方法，
     * 适用于源类型和目标类型均需要显式指定的场景。
     * 
     * @param <T> 目标类型泛型参数
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default <T> boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull T target, @NonNull Class<? extends T> targetClass) {
        return transform(source, sourceTypeDescriptor, target, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 指定源类型Class的属性转换方法。
     * <p>
     * 将源Class转换为类型描述符后调用核心方法，
     * 适用于通过Class指定源类型的场景。
     * 
     * @param <S> 源类型泛型参数
     * @param source 源对象，不可为null
     * @param sourceClass 源类型Class，不可为null
     * @param target 目标对象，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
    }

    /**
     * 指定源类型Class和目标类型描述符的属性转换方法。
     * <p>
     * 将源Class转换为类型描述符后调用核心方法，
     * 适用于源类型通过Class指定、目标类型通过描述符指定的场景。
     * 
     * @param <S> 源类型泛型参数
     * @param source 源对象，不可为null
     * @param sourceClass 源类型Class，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, targetTypeDescriptor);
    }

    /**
     * 指定源类型Class和目标类型Class的属性转换方法。
     * <p>
     * 将源Class和目标Class均转换为类型描述符后调用核心方法，
     * 适用于通过Class显式指定源类型和目标类型的场景。
     * 
     * @param <S> 源类型泛型参数
     * @param <T> 目标类型泛型参数
     * @param source 源对象，不可为null
     * @param sourceClass 源类型Class，不可为null
     * @param target 目标对象，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 转换是否成功
     * @throws NullPointerException 若参数为null
     */
    default <S, T> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
            @NonNull Class<? extends T> targetClass) {
        return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.valueOf(targetClass));
    }
}