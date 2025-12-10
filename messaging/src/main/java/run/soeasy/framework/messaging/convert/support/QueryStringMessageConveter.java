package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.messaging.MediaType;

/**
 * 查询字符串消息转换器，继承自{@link TextMessageConverter}，专门用于处理
 * {@link MediaType#APPLICATION_FORM_URLENCODED}类型的消息（即表单编码的查询字符串，如key1=val1&amp;key2=val2），
 * 支持将查询字符串与Java对象进行双向转换（解析为对象或序列化为查询字符串）。
 * 
 * <p>核心特性：
 * - 仅支持application/x-www-form-urlencoded媒体类型，符合表单提交数据的格式规范；
 * - 依赖{@link QueryStringFormat}处理查询字符串的解析与格式化，自动处理URL编码/解码；
 * - 继承文本消息转换器的字符集处理逻辑（默认UTF-8），确保特殊字符（如空格、&amp;、=）正确编码。
 * 
 * @author soeasy.run
 * @see TextMessageConverter
 * @see QueryStringFormat
 * @see MediaType#APPLICATION_FORM_URLENCODED
 */
@Getter
@Setter
public class QueryStringMessageConveter extends TextMessageConverter {

    /**
     * 初始化查询字符串消息转换器，清除默认媒体类型并仅注册{@link MediaType#APPLICATION_FORM_URLENCODED}，
     * 确保仅处理表单编码的查询字符串消息。
     */
    public QueryStringMessageConveter() {
        getMediaTypeRegistry().reset();
        getMediaTypeRegistry().register(MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 重写文本解析逻辑，将查询字符串解析为目标类型的对象
     * 
     * <p>步骤：
     * 1. 基于当前字符集创建{@link QueryStringFormat}实例（处理URL编码/解码）；
     * 2. 关联当前转换器的类型转换器（{@link #getConverter()}）；
     * 3. 调用{@link QueryStringFormat#convert}将查询字符串解析为目标类型对象（如JavaBean、Map）。
     * 
     * @param body 待解析的查询字符串（非空，如"id=1&amp;name=test"，已按字符集解码）
     * @param targetDescriptor 目标类型描述符（非空，指定解析后的对象类型）
     * @param charset 字符集（非空，用于URL解码，与字符串解码一致）
     * @return 解析后的目标类型对象（符合targetDescriptor指定的类型）
     */
    @Override
    protected Object parseObject(String body, TargetDescriptor targetDescriptor, Charset charset) {
        // 创建查询字符串格式化工具（使用当前字符集处理编码）
        QueryStringFormat queryStringFormat = new QueryStringFormat(charset);
        // 关联类型转换器（复用父类的converter）
        queryStringFormat.setConverter(getConverter());
        // 解析查询字符串为目标类型
        return queryStringFormat.convert(body, targetDescriptor.getRequiredTypeDescriptor());
    }

    /**
     * 重写文本序列化逻辑，将对象转换为查询字符串
     * 
     * <p>步骤：
     * 1. 基于当前字符集创建{@link QueryStringFormat}实例（处理URL编码/解码）；
     * 2. 关联当前转换器的类型转换器（{@link #getConverter()}）；
     * 3. 调用{@link QueryStringFormat#convert}将对象序列化为查询字符串（自动URL编码特殊字符）。
     * 
     * @param body 待序列化的对象数据（非空，如JavaBean、Map）
     * @param contentType 媒体类型（非空，应为{@link MediaType#APPLICATION_FORM_URLENCODED}）
     * @param charset 字符集（非空，用于URL编码，后续会按此字符集转为字节数组）
     * @return 序列化后的查询字符串（非空，如"id=1&amp;name=test"，已URL编码）
     * @throws IOException 无实际异常（查询字符串格式化过程不抛出I/O异常）
     */
    @Override
    protected String toString(TypedData<Object> body, MediaType contentType, Charset charset) throws IOException {
        // 创建查询字符串格式化工具（使用当前字符集处理编码）
        QueryStringFormat queryStringFormat = new QueryStringFormat(charset);
        // 关联类型转换器（复用父类的converter）
        queryStringFormat.setConverter(getConverter());
        // 将对象序列化为查询字符串
        return queryStringFormat.convert(body.get(), body.getReturnTypeDescriptor(), String.class);
    }
}