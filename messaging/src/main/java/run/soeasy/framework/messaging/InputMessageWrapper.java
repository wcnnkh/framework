package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.InputSourceWrapper;

/**
 * 输入消息包装器接口，继承{@link InputMessage}、{@link MessageWrapper}和{@link InputSourceWrapper}，
 * 作为函数式接口为输入消息提供包装能力，将核心操作委托给被包装的{@link InputMessage}实例，
 * 便于在不修改原始消息的情况下扩展功能（如添加日志、加密、缓冲等装饰）。
 * 
 * <p>该接口提供以下默认实现：
 * - {@link #getReaderPipeline()}：委托给被包装消息的对应方法，获取字符流处理管道；
 * - {@link #isDecoded()}：委托给被包装消息的解码状态判断；
 * - {@link #buffered()}：委托给被包装消息的缓冲方法，获取缓冲后的消息实例。
 * 
 * @param <W> 被包装的输入消息类型（需实现{@link InputMessage}）
 * @author soeasy.run
 * @see InputMessage
 * @see MessageWrapper
 * @see InputSourceWrapper
 */
@FunctionalInterface
public interface InputMessageWrapper<W extends InputMessage>
		extends InputMessage, MessageWrapper<W>, InputSourceWrapper<W> {

	/**
	 * 获取字符流处理管道，委托给被包装的输入消息的{@link InputMessage#getReaderPipeline()}
	 * 
	 * @return 字符流处理管道（非空）
	 */
	@Override
	default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}

	/**
	 * 判断消息是否已解码，委托给被包装的输入消息的{@link InputMessage#isDecoded()}
	 * 
	 * @return 已解码返回true，否则返回false
	 */
	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}

	/**
	 * 获取缓冲后的输入消息，委托给被包装的输入消息的{@link InputMessage#buffered()}
	 * 
	 * @return 缓冲后的输入消息（非空）
	 */
	@Override
	default InputMessage buffered() {
		return getSource().buffered();
	}
}