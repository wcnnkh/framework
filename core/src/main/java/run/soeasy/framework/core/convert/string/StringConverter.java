package run.soeasy.framework.core.convert.string;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 字符串转换器接口 定义对象与字符串之间的相互转换能力，扩展自通用转换器接口
 * 
 * 实现类需提供两个核心方法： - format(): 将对象格式化为字符串 - parse(): 将字符串解析为目标类型对象
 */
public interface StringConverter extends Converter {

	/**
	 * 判断是否可以执行转换 默认实现支持以下两种转换方向： 1. 从任意类型转换为String 2. 从String转换为任意类型
	 * 
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 如果可以转换返回true，否则返回false
	 */
	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		// 支持任意类型与String之间的转换
		return (String.class == sourceTypeDescriptor.getType()) || (String.class == targetTypeDescriptor.getType());
	}

	/**
	 * 执行对象与字符串之间的转换
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换后的对象
	 * @throws ConversionException 如果转换过程中发生错误
	 */
	@Override
	default Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {

		// 如果类型直接兼容，无需转换
		if (Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return source;
		}

		// 执行类型转换
		String charSequence;
		if (source instanceof String) {
			// 源对象已经是String，直接使用
			charSequence = (String) source;
		} else {
			// 调用format方法将对象格式化为String
			charSequence = format(source, sourceTypeDescriptor);
		}

		// 调用parse方法将String解析为目标类型
		return parse(charSequence, targetTypeDescriptor);
	}

	/**
	 * 将对象格式化为字符串
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @return 格式化后的字符串，如果对象为null可以返回null
	 */
	String format(Object source, @NonNull TypeDescriptor sourceTypeDescriptor);

	/**
	 * 将字符串解析为目标类型对象
	 * 
	 * @param source               源字符串
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 解析后的对象
	 * @throws ConversionException 如果解析失败
	 */
	Object parse(String source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException;
}
