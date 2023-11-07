package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

public interface QueryStringHandler {
	/**
	 * 读取
	 * 
	 * @param readSize 已读取大小
	 * @param source
	 * @param consumer
	 * @throws IOException
	 */
	void read(LongAdder readSize, Readable source, BiConsumer<String, String> consumer) throws IOException;

	/**
	 * 写入
	 * 
	 * @param writtenSize 已写入大小
	 * @param key
	 * @param value
	 * @param target
	 * @throws IOException
	 */
	void write(LongAdder writtenSize, String key, String value, Appendable target) throws IOException;
}
