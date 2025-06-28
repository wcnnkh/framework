package run.soeasy.framework.core.convert;

import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 可逆转换器接口 支持类型S到T和T到S的双向转换，自动推导类型映射关系
 * 
 * 核心特性： 1. 双向转换支持（to/from方法） 2. 自动类型映射推导 3. 条件转换支持（继承自ConditionalConverter）
 */
public interface ReversibleConverter<S, T> extends ConditionalConverter {

	/**
	 * 获取类型映射关系 基于泛型参数自动解析源类型和目标类型
	 * 
	 * @return 类型映射对象，包含S->T的映射关系
	 */
	default TypeMapping getTypeMapping() {
		// 解析当前接口的泛型类型参数
		ResolvableType resolvableType = ResolvableType.forType(getClass());
		resolvableType = resolvableType.as(ReversibleConverter.class);

		// 获取泛型参数的原始类型
		Class<?> sourceType = resolvableType.getActualTypeArgument(0).getRawType();
		Class<?> targetType = resolvableType.getActualTypeArgument(1).getRawType();

		return new TypeMapping(sourceType, targetType);
	}

	/**
	 * 获取可转换的类型映射集合 包含正向映射(S->T)和反向映射(T->S)
	 * 
	 * @return 包含两个TypeMapping的集合
	 */
	@Override
	default Set<TypeMapping> getConvertibleTypeMappings() {
		TypeMapping typeMapping = getTypeMapping();
		Set<TypeMapping> typeMappings = new HashSet<>(2, 1);
		typeMappings.add(typeMapping); // 正向映射
		typeMappings.add(typeMapping.reversed()); // 反向映射
		return typeMappings;
	}

	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
				|| Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	/**
	 * 执行类型转换 自动判断转换方向并调用相应的转换方法
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换后的对象
	 * @throws ConversionException 转换失败时抛出
	 */
	@SuppressWarnings("unchecked")
	@Override
	default Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		TypeMapping typeMapping = getTypeMapping();
		// 检查是否为S->T的转换
		if (typeMapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return to((S) source, sourceTypeDescriptor, targetTypeDescriptor);
		}
		// 检查是否为T->S的转换
		else if (typeMapping.canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
			return from((T) source, sourceTypeDescriptor, targetTypeDescriptor);
		}

		// 如果类型直接兼容，无需转换
		if (Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return source;
		}

		// 复杂转换逻辑：先转成中间类型再转换
		TypeDescriptor contentType = new TypeDescriptor(ResolvableType.forType(typeMapping.getValue()),
				typeMapping.getValue(), getClass());
		T target = typeMapping.getValue().isInstance(source) ? (T) source
				: to((S) source, sourceTypeDescriptor, contentType);
		return from(target, contentType, targetTypeDescriptor);
	}

	/**
	 * 执行S->T的转换
	 * 
	 * @param source               源对象(S类型)
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换后的T类型对象
	 * @throws ConversionException 转换失败时抛出
	 */
	T to(S source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) throws ConversionException;

	/**
	 * 执行T->S的转换
	 * 
	 * @param source               源对象(T类型)
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换后的S类型对象
	 * @throws ConversionException 转换失败时抛出
	 */
	S from(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException;
}