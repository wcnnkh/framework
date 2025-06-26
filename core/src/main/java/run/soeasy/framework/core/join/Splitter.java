package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.io.CharSequenceReader;

/**
 * 字符串分割器接口 定义从字符流中解析元素的能力，支持流式处理和惰性求值
 * 
 * 实现类需实现核心方法： - split(Readable)：从字符流中解析元素并返回Stream
 * 
 * 默认实现提供了从CharSequence和String解析的能力
 * 
 * @param <E> 解析出的元素类型
 */
@FunctionalInterface
public interface Splitter<E> {
	/**
	 * 从字符流中解析元素
	 * 
	 * 核心方法，实现类需定义具体的解析逻辑
	 * 
	 * @param readable 字符输入流
	 * @return 解析出的元素流
	 * @throws IOException 当读取流失败时抛出
	 */
	Stream<E> split(@NonNull Readable readable) throws IOException;

	/**
	 * 从字符序列中解析元素
	 * 
	 * 默认实现使用CharSequenceReader直接从CharSequence读取， 避免转换为String的开销
	 * 
	 * @param charSequence 字符序列
	 * @return 解析出的元素流
	 * @throws IllegalStateException 如果发生意外的IO异常
	 */
	default Stream<E> split(@NonNull CharSequence charSequence) {
		CharSequenceReader reader = new CharSequenceReader(charSequence);
		try {
			return split(reader);
		} catch (IOException e) {
			// CharSequenceReader理论上不会抛出IOException
			throw new IllegalStateException("Unexpected IOException from CharSequenceReader", e);
		}
	}
}