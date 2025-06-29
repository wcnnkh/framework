package run.soeasy.framework.core.convert.strings;

import java.io.IOException;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.io.IOUtils;

/**
 * 字符串转换器接口（泛型版本）。
 * 定义类型T与字符串的双向转换规则，支持流式IO操作。
 * 
 * 继承自{@link ReversibleConverter}，表示：
 * - 正向转换：T -&gt; String（{@link #to}方法）
 * - 反向转换：String -&gt; T（{@link #from}方法）
 * 
 * @param <T> 待转换的目标类型
 * @author soeasy.run
 */
public interface StringConverter<T> extends ReversibleConverter<T, String> {

    /**
     * 从{@link Readable}解析为T类型。
     * 支持从字符输入流解析对象。
     * 
     * @param readable             字符输入流（不可为null）
     * @param targetTypeDescriptor 目标类型描述符（不可为null）
     * @return 解析后的T类型对象
     * @throws ConversionException 解析失败时抛出
     * @throws IOException         读取失败时抛出
     */
    default T from(Readable readable, TypeDescriptor targetTypeDescriptor) throws ConversionException, IOException {
        String content = IOUtils.toCharSequence(readable).toString();
        return from(content, TypeDescriptor.valueOf(String.class), targetTypeDescriptor);
    }

    /**
     * 将T类型格式化为{@link Appendable}。
     * 支持将对象格式化为字符输出流。
     * 
     * @param source               T类型源对象（不可为null）
     * @param sourceTypeDescriptor 源类型描述符（不可为null）
     * @param appendable           字符输出流（不可为null）
     * @throws ConversionException 格式化失败时抛出
     * @throws IOException         写入失败时抛出
     */
    default void to(T source, TypeDescriptor sourceTypeDescriptor, Appendable appendable)
            throws ConversionException, IOException {
        String content = to(source, sourceTypeDescriptor, TypeDescriptor.valueOf(String.class));
        appendable.append(content);
    }
}