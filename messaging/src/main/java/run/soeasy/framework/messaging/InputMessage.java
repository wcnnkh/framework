package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.InputSource;

/**
 * 输入消息接口，继承{@link Message}和{@link InputSource}接口，
 * 表示可读取内容的消息，提供消息内容的解码、读取管道及缓冲处理能力，
 * 是处理输入消息（如HTTP请求消息）的核心抽象。
 * 
 * <p>核心功能：
 * - 判断消息内容是否已解码（基于字符集名称是否存在）；
 * - 提供字符流读取管道（{@link #getReaderPipeline()}），自动处理解码逻辑；
 * - 支持消息内容的缓冲处理（{@link #buffered()}），便于重复读取。
 * 
 * @author soeasy.run
 * @see Message
 * @see InputSource
 * @see BufferingInputMessage
 */
public interface InputMessage extends Message, InputSource {

    /**
     * 判断消息内容是否已解码
     * 
     * <p>若消息包含非空字符集名称（{@link #getCharsetName()}），则视为已解码；否则视为未解码。
     * 
     * @return 已解码返回true，否则返回false
     */
    @Override
    default boolean isDecoded() {
        return StringUtils.isNotEmpty(getCharsetName());
    }

    /**
     * 获取字符流读取管道，用于按字符流方式读取消息内容
     * 
     * <p>若消息未指定字符集（{@link #getCharsetName()}为空），则先调用{@link #decode(String)}进行解码（使用默认字符集），
     * 再返回解码后的字符流管道；否则直接返回基于当前字符集的字符流管道。
     * 
     * @return 字符流读取管道（非空），可能抛出{@link IOException}
     */
    @Override
    default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
        String charsetName = getCharsetName();
        if (StringUtils.isEmpty(charsetName)) {
            return decode(charsetName).getReaderPipeline();
        }
        return InputSource.super.getReaderPipeline();
    }

    /**
     * 创建当前输入消息的缓冲包装实例，支持内容的重复读取
     * 
     * <p>返回的{@link BufferingInputMessage}会缓存消息内容，避免原始输入流只能读取一次的限制，
     * 适用于需要多次读取消息内容的场景。
     * 
     * @return 缓冲包装后的输入消息（非空）
     */
    default InputMessage buffered() {
        return new BufferingInputMessage<>(this);
    }
}