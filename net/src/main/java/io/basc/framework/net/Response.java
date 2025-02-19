package io.basc.framework.net;

import java.io.Closeable;
import java.io.IOException;

/**
 * 一个返回的定义
 * 
 * @author shuchaowen
 *
 */
public interface Response extends Message, Closeable {
	public static interface ResponseWrapper<W extends Response> extends Response, MessageWrapper<W> {
		@Override
		default void close() throws IOException {
			getSource().close();
		}
	}

	@Override
	void close() throws IOException;
}
