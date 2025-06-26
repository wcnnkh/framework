package run.soeasy.framework.io;

import java.io.IOException;
import java.io.StringReader;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.string.StringConverter;

/**
 * 字符流格式化接口 扩展自StringConverter，提供基于字符流的对象格式化与解析能力
 * 
 * 实现类需实现以下核心方法： - format(Appendable, Object, TypeDescriptor)：将对象格式化为字符流 -
 * parse(Readable, TypeDescriptor)：从字符流解析对象
 * 
 * 默认实现会自动关联字符串转换和字符流转换的逻辑： - format(Object) 委托给 format(Appendable) -
 * parse(String) 委托给 parse(Readable)
 */
public interface CharacterStreamFormat extends StringConverter {

	/**
	 * 将对象格式化为字符流并写入Appendable 这是格式化的核心方法，实现类必须实现此方法
	 * 
	 * @param appendable           目标字符输出流
	 * @param source               待格式化的对象
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @throws IOException 当写入Appendable失败时
	 */
	void format(@NonNull Appendable appendable, Object source, @NonNull TypeDescriptor sourceTypeDescriptor)
			throws IOException;

	/**
	 * 将对象格式化为字符串 默认实现委托给 format(Appendable) 方法
	 * 
	 * @param source               待格式化的对象
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @return 格式化后的字符串
	 * @throws ConversionException 如果格式化失败
	 */
	@Override
	default String format(Object source, @NonNull TypeDescriptor sourceTypeDescriptor) {
		StringBuilder builder = new StringBuilder();
		try {
			format(builder, source, sourceTypeDescriptor);
		} catch (IOException e) {
			// 将IO异常包装为运行时异常，因为StringConverter接口不声明IOException
			throw new IllegalStateException("Unexpected IOException during formatting", e);
		}
		return builder.toString();
	}

	/**
	 * 从字符流解析数据为目标类型对象 这是解析的核心方法，实现类必须实现此方法
	 * 
	 * @param readable             源字符输入流
	 * @param targetTypeDescriptor 目标类型的描述符
	 * @return 解析后的目标类型对象
	 * @throws IOException         当读取Readable失败时
	 * @throws ConversionException 如果解析失败
	 */
	Object parse(@NonNull Readable readable, @NonNull TypeDescriptor targetTypeDescriptor)
			throws IOException, ConversionException;

	/**
	 * 从字符串解析数据为目标类型对象 默认实现委托给 parse(Readable) 方法
	 * 
	 * @param source               源字符串
	 * @param targetTypeDescriptor 目标类型的描述符
	 * @return 解析后的目标类型对象
	 * @throws ConversionException 如果解析失败
	 */
	@Override
	default Object parse(String source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		StringReader reader = new StringReader(source);
		try {
			return parse(reader, targetTypeDescriptor);
		} catch (IOException e) {
			// 理论上StringReader不会抛出IOException，但为了代码健壮性保留此处理
			throw new IllegalStateException("Unexpected IOException during parsing", e);
		} finally {
			reader.close();
		}
	}
}