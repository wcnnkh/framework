package run.soeasy.framework.core.convert.strings;

import java.io.IOException;
import java.io.StringReader;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 字符串格式化接口（泛型版本）。
 * 继承自{@link StringConverter}，提供具体的字符串转换实现和流式IO支持。
 * 
 * 核心特性：
 * 1. 实现{@link StringConverter}的抽象方法，提供具体转换逻辑
 * 2. 支持字符串与T类型的双向转换
 * 3. 封装流式IO操作，简化使用流程
 * 
 * @param <T> 待转换的目标类型
 * @author soeasy.run
 */
public interface StringFormat<T> extends StringConverter<T> {

    /**
     * 反向转换：字符串转T类型（实现版本）。
     * 封装{@link Readable}接口操作，提供便捷的字符串解析。
     * 
     * @param source               源字符串（不可为null）
     * @param sourceTypeDescriptor 源类型描述符（不可为null）
     * @param targetTypeDescriptor 目标类型描述符（不可为null）
     * @return 解析后的T类型对象
     * @throws ConversionException 解析失败时抛出
     */
    @Override
    default T from(String source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        StringReader reader = new StringReader(source);
        try {
            return from(reader, targetTypeDescriptor);
        } catch (IOException e) {
            throw new IllegalStateException("internal error", e);
        } finally {
            reader.close();
        }
    }

    /**
     * 正向转换：T类型转字符串（实现版本）。
     * 封装{@link Appendable}接口操作，提供便捷的对象格式化。
     * 
     * @param source               T类型源对象（不可为null）
     * @param sourceTypeDescriptor 源类型描述符（不可为null）
     * @param targetTypeDescriptor 目标类型描述符（不可为null）
     * @return 格式化后的字符串
     * @throws ConversionException 格式化失败时抛出
     */
    @Override
    default String to(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        StringBuilder builder = new StringBuilder();
        try {
            to(source, sourceTypeDescriptor, builder);
        } catch (IOException e) {
            throw new IllegalStateException("internal error", e);
        }
        return builder.toString();
    }

    /**
     * 正向转换：T类型格式化为{@link Appendable}。
     * 需实现类提供具体的格式化逻辑。
     * 
     * @param source               T类型源对象（不可为null）
     * @param sourceTypeDescriptor 源类型描述符（不可为null）
     * @param appendable           字符输出目标（不可为null）
     * @throws ConversionException 格式化失败时抛出
     * @throws IOException         写入失败时抛出
     */
    @Override
    void to(T source, TypeDescriptor sourceTypeDescriptor, Appendable appendable)
            throws ConversionException, IOException;

    /**
     * 反向转换：从{@link Readable}解析为T类型。
     * 需实现类提供具体的解析逻辑。
     * 
     * @param readable             字符输入源（不可为null）
     * @param targetTypeDescriptor 目标类型描述符（不可为null）
     * @return 解析后的T类型对象
     * @throws ConversionException 解析失败时抛出
     * @throws IOException         读取失败时抛出
     */
    @Override
    T from(Readable readable, TypeDescriptor targetTypeDescriptor) throws ConversionException, IOException;
}