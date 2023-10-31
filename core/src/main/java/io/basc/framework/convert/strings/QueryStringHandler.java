package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

public interface QueryStringHandler {
	/**
	 * 写入
	 * 
	 * @param writeCount 写入计数器
	 * @param key
	 * @param value
	 * @param target
	 * @throws IOException
	 */
	void write(LongAdder writeCount, String key, String value, Appendable target) throws IOException;

	/**
	 * 读取
	 * 
	 * @param readCount 读取计数器
	 * @param source
	 * @param consumer
	 * @throws IOException
	 */
	void read(LongAdder readCount, Readable source, BiConsumer<String, String> consumer) throws IOException;
}
