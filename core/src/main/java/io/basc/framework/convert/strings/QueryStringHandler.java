package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;

import io.basc.framework.util.Pair;
import io.basc.framework.util.function.ConsumeProcessor;

public interface QueryStringHandler {
	/**
	 * 拼接多个key
	 * 
	 * @param key
	 * @return
	 */
	String joinKey(String... key);

	/**
	 * 写入
	 * 
	 * @param writeCount 写入计数器
	 * @param key
	 * @param value
	 * @param target
	 * @throws IOException
	 */
	void write(LongAdder writeCount, CharSequence key, CharSequence value, Appendable target) throws IOException;

	/**
	 * 读取
	 * 
	 * @param <E>
	 * @param readCount 读取计数器
	 * @param source
	 * @param consumer
	 * @throws IOException
	 * @throws E
	 */
	<E extends Throwable> void read(LongAdder readCount, Readable source,
			ConsumeProcessor<? super Pair<? extends CharSequence, ? extends CharSequence>, ? extends E> consumer)
			throws IOException, E;
}
