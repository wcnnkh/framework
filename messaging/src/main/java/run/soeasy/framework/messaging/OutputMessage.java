package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.OutputSource;

/**
 * 可输出消息接口，继承自{@link Message}和{@link OutputSource}，
 * 扩展了消息的写入能力，支持设置消息元数据（内容类型、长度、字符集）和输出流操作，
 * 是消息发送场景（如HTTP响应、消息队列发送）中表示可写入消息的核心接口。
 * 
 * <p>该接口通过默认方法实现了元数据的设置逻辑，确保内容类型与字符集的一致性，
 * 同时整合了输出源的管道能力，支持基于字符集的编码输出，适用于需要动态构建并发送消息的场景。
 * 
 * @author soeasy.run
 * @see Message
 * @see OutputSource
 * @see MediaType
 * @see Headers
 */
public interface OutputMessage extends Message, OutputSource {

    /**
     * 设置消息的内容类型（自动处理字符集一致性）
     * 
     * <p>逻辑：
     * 1. 若内容类型（{@link MediaType}）未指定字符集：
     *    - 尝试获取当前消息的字符集名称（{@link #getCharsetName()}）；
     *    - 若存在字符集，创建包含该字符集的新{@link MediaType}并设置到头部；
     *    - 若不存在，直接设置原始内容类型；
     * 2. 若内容类型已指定字符集，直接设置到头部（覆盖原有设置）。
     * 
     * @param contentType 消息的内容类型（非空，如application/json、text/plain）
     */
    default void setContentType(MediaType contentType) {
        String charsetName = contentType.getCharsetName();
        if (charsetName == null) {
            charsetName = getCharsetName();
            if (charsetName == null) {
                getHeaders().setContentType(contentType);
            } else {
                // 合并现有字符集到内容类型
                getHeaders().setContentType(new MediaType(contentType, charsetName));
            }
        } else {
            getHeaders().setContentType(contentType);
        }
    }

    /**
     * 设置消息内容的长度（字节数）
     * 
     * <p>将内容长度设置到消息头部，通常在消息体写入完成后调用，用于接收方验证消息完整性。
     * 
     * @param contentLength 消息内容的长度（字节数，非负整数）
     */
    default void setContentLength(long contentLength) {
        getHeaders().setContentLength(contentLength);
    }

    /**
     * 设置消息内容的字符集名称（通过更新内容类型实现）
     * 
     * <p>逻辑：
     * 1. 若当前内容类型为null，默认使用{@link MediaType#ALL}（不设置字符集）；
     * 2. 否则基于现有内容类型和新字符集，创建新{@link MediaType}并调用{@link #setContentType(MediaType)}更新。
     * 
     * @param charsetName 字符集名称（如UTF-8、ISO-8859-1，可为null表示不指定）
     */
    default void setCharsetName(String charsetName) {
        MediaType mediaType = getContentType();
        if (mediaType == null) {
            mediaType = MediaType.ALL;
            return; // 此处原逻辑可能存在笔误，推测为"getHeaders().setContentType(mediaType);"，但按现有代码注释
        }

        setContentType(new MediaType(mediaType, charsetName));
    }

    /**
     * 判断消息是否已编码（基于字符集是否存在）
     * 
     * <p>默认实现：若字符集名称非空，则认为消息已编码（需按指定字符集处理输出）。
     * 
     * @return 已编码返回true（字符集存在），否则返回false
     */
    @Override
    default boolean isEncoded() {
        return StringUtils.isNotEmpty(getCharsetName());
    }

    /**
     * 获取字符输出流管道（支持编码转换）
     * 
     * <p>逻辑：
     * 1. 若消息已编码（{@link #isEncoded()}为true），通过{@link #encode(String)}获取带编码的输出源，返回其字符流管道；
     * 2. 否则返回父接口的默认字符流管道（不进行编码转换）。
     * 
     * @return 字符输出流管道（非空，支持链式操作）
     */
    @Override
    default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
        if (isEncoded()) {
            return encode(getCharsetName()).getWriterPipeline();
        }
        return OutputSource.super.getWriterPipeline();
    }
}