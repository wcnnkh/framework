package io.basc.framework.util.io;

/**
 * 缓冲区读取
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
@FunctionalInterface
public interface BufferProcessor<T, E extends Throwable> {
	void process(T buffer, int offset, int len) throws E;
}
