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
	@Override
	void close() throws IOException;
}
