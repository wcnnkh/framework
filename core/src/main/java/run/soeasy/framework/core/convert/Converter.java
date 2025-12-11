package run.soeasy.framework.core.convert;

import lombok.NonNull;

/**
 * Converter接口定义了对象类型转换的标准API，是类型转换系统的核心接口。 实现此接口的类负责将对象从一种类型转换为另一种类型。
 * 
 * <p>
 * Converter接口提供了多种转换方法的重载形式，以支持不同场景下的类型转换需求。 它还继承了Convertable接口以获取类型转换能力的判断功能。
 * 
 * <p>
 * 此接口采用函数式接口设计，允许通过lambda表达式实现简单的转换器。
 * 
 * @see Convertible
 * @see TypeDescriptor
 * @see ConversionException
 */
@FunctionalInterface
public interface Converter extends Convertible {
	/**
	 * 返回一个基于类型兼容性的转换器实例。 该转换器仅支持Java类型系统中允许的直接赋值转换。
	 * 
	 * @return AssignableConverter的单例实例
	 */
	public static Converter assignable() {
		return AssignableConverter.INSTANCE;
	}

	/**
	 * 判断源类型是否可以转换为目标类型。 此默认实现返回true，表示所有类型转换都被允许。 具体实现应根据转换器的实际能力重写此方法。
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不能为null
	 * @param targetTypeDescriptor 目标类型描述符，不能为null
	 * @return 总是返回true（默认实现）
	 */
	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return true;
	}

	/**
	 * 将源对象转换为目标类的实例。 此方法通过类型描述符推断源类型，并调用核心转换方法。
	 * 
	 * @param <T>         目标类型
	 * @param source      源对象，不能为null
	 * @param targetClass 目标类，不能为null
	 * @return 转换后的目标类型实例
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	@SuppressWarnings("unchecked")
	default <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source,
				source == null ? TypeDescriptor.valueOf(Object.class) : TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetClass));
	}

	/**
	 * 将指定类型的源对象转换为目标类型描述符所表示的类型。
	 * 
	 * @param source               源对象
	 * @param sourceClass          源对象的类，不能为null
	 * @param targetTypeDescriptor 目标类型描述符，不能为null
	 * @return 转换后的对象
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	default Object convert(Object source, @NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return convert(source, TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	/**
	 * 将源对象转换为目标类型描述符所表示的类型。 此方法通过类型描述符推断源类型。
	 * 
	 * @param source               源对象
	 * @param targetTypeDescriptor 目标类型描述符，不能为null
	 * @return 转换后的对象
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	default Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return convert(source, source == null ? TypeDescriptor.valueOf(Object.class) : TypeDescriptor.forObject(source),
				targetTypeDescriptor);
	}

	/**
	 * 将源类型描述符所表示的源对象转换为目标类的实例。
	 * 
	 * @param <T>                  目标类型
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符，不能为null
	 * @param targetClass          目标类，不能为null
	 * @return 转换后的目标类型实例
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	@SuppressWarnings("unchecked")
	default <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}

	/**
	 * 将源对象从源类型转换为目标类型的核心转换方法。 所有其他转换方法最终都会调用此方法。
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符，不能为null
	 * @param targetTypeDescriptor 目标类型描述符，不能为null
	 * @return 转换后的对象
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException;
}