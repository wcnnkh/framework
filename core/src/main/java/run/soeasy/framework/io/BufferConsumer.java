package run.soeasy.framework.io;

/**
 * 缓冲区消费者
 * 
 * @author soeasy.run
 *
 * @param <T>
 */
@FunctionalInterface
public interface BufferConsumer<T, E extends Throwable> {
	void accept(T buffer, int offset, int len) throws E;
}
