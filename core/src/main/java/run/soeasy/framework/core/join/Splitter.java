package run.soeasy.framework.core.join;

import java.nio.CharBuffer;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferReader;

/**
 * 字符串分割器接口 定义从字符流中解析元素的能力，支持流式处理和惰性求值
 * 
 * 实现类需实现核心方法： - split(Readable)：从字符流中解析元素并返回Stream
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
	 * @param reader 字符输入流
	 * @return 解析出的元素流
	 */
	Stream<E> split(@NonNull BufferReader<? super CharBuffer, ? extends Throwable> reader);
}