package run.soeasy.framework.core.convert;

import lombok.NonNull;

/**
 * 类型转换能力接口，定义类型转换可行性判断的标准。
 * 该函数式接口用于判断源类型是否可转换为目标类型，
 * 支持多种参数形式以适应不同场景的类型检查需求。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式设计：可作为lambda表达式或方法引用使用</li>
 *   <li>多形态参数：支持Class与TypeDescriptor混合参数形式</li>
 *   <li>默认实现：提供三种参数组合的默认实现，避免重复代码</li>
 *   <li>空值保护：通过{@link NonNull}注解确保参数非空</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <pre>
 * // 注册自定义类型转换器
 * ConvertService registry = new DefaultConvertService();
 * registry.addConverter((Convertable) (srcType, targetType) -> {
 *     return srcType.getType() == String.class && targetType.getType() == Integer.class;
 * });
 * </pre>
 *
 * @author soeasy.run
 * @see TypeDescriptor
 * @see Converter
 * @see ConvertService
 */
@FunctionalInterface
public interface Convertable {

    /**
     * 判断源类是否可转换为目标类（基于Class参数）
     * 
     * @param sourceClass 源类型Class对象，不可为null
     * @param targetClass 目标类型Class对象，不可为null
     * @return true表示可转换，false表示不可转换
     */
    default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
        return canConvert(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 判断源类是否可转换为目标类型描述符（混合参数形式）
     * 
     * @param sourceClass 源类型Class对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
        return canConvert(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
    }

    /**
     * 判断源类型描述符是否可转换为目标类（混合参数形式）
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetClass 目标类型Class对象，不可为null
     * @return true表示可转换，false表示不可转换
     */
    default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
        return canConvert(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
    }

    /**
     * 判断源类型描述符是否可转换为目标类型描述符（核心方法）
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor);
}