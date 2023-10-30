package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiPredicate;

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
	 * @param predicate 返回false则不再继续读取
	 * @throws IOException
	 */
	void read(LongAdder readCount, Readable source, BiPredicate<String, String> predicate) throws IOException;
}
