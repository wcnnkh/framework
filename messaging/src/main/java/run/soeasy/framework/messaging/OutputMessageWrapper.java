package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.OutputSourceWrapper;

/**
 * 可输出消息包装器接口，继承自{@link OutputMessage}、{@link MessageWrapper}和{@link OutputSourceWrapper}，
 * 采用委托模式对{@link OutputMessage}实例进行包装，通过统一委托源消息实现所有接口方法，
 * 是简化可输出消息扩展的函数式接口，适用于需要在不修改原始消息的前提下增强输出消息功能的场景。
 * 
 * <p>作为函数式接口，其核心抽象方法为{@link #getSource()}，用于获取被包装的原始可输出消息，
 * 所有默认方法均通过该方法委托给源消息的对应实现，确保包装器与源消息的行为一致，同时支持灵活扩展。
 * 
 * @param <W> 被包装的可输出消息类型（需继承自{@link OutputMessage}）
 * @author soeasy.run
 * @see OutputMessage
 * @see MessageWrapper
 * @see OutputSourceWrapper
 */
@FunctionalInterface
public interface OutputMessageWrapper<W extends OutputMessage>
		extends OutputMessage, MessageWrapper<W>, OutputSourceWrapper<W> {

    /**
     * 判断消息是否已编码（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的编码状态，等同于{@code getSource().isEncoded()}。
     * 
     * @return 源消息的编码状态（已编码返回true，否则返回false）
     */
	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}

    /**
     * 获取字符输出流管道（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的字符输出流管道，等同于{@code getSource().getWriterPipeline()}，
     * 确保输出操作通过源消息的管道执行。
     * 
     * @return 源消息的字符输出流管道（非空）
     */
	@Override
	default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}

    /**
     * 设置消息的内容类型（委托给源消息）
     * 
     * <p>默认实现：调用被包装消息的{@code setContentType(MediaType)}方法，
     * 确保内容类型设置作用于源消息。
     * 
     * @param contentType 内容类型（非空）
     */
	@Override
	default void setContentType(MediaType contentType) {
		getSource().setContentType(contentType);
	}

    /**
     * 设置消息的内容长度（委托给源消息）
     * 
     * <p>默认实现：调用被包装消息的{@code setContentLength(long)}方法，
     * 确保内容长度设置作用于源消息。
     * 
     * @param contentLength 内容长度（字节数）
     */
	@Override
	default void setContentLength(long contentLength) {
		getSource().setContentLength(contentLength);
	}

    /**
     * 设置消息的字符集名称（委托给源消息）
     * 
     * <p>默认实现：调用被包装消息的{@code setCharsetName(String)}方法，
     * 确保字符集设置作用于源消息。
     * 
     * @param charsetName 字符集名称（可为null）
     */
	@Override
	default void setCharsetName(String charsetName) {
		getSource().setCharsetName(charsetName);
	}
}