package run.soeasy.framework.io.buffer;

import java.nio.Buffer;
import lombok.NonNull;
import run.soeasy.framework.core.function.Pool;

/**
 * 缓冲区专用池接口，基于框架通用 {@link Pool} 契约特化。
 *
 * <p>
 * 编码约定：
 * <ul>
 * <li>调用 {@link #get()} 获取缓冲区；</li>
 * <li>必须在 {@code try-finally} 块中调用 {@link #close(Buffer)} 归还，保持统一写法；</li>
 * </ul>
 *
 * <p>
 * 默认实现：{@link NoneBufferPool}，不做对象复用，每次获取新建缓冲区。
 * 后续可按需新增池化实现，业务代码不需要改动。
 *
 * @param <B> 缓冲区类型，{@link Buffer} 的子类
 * @author soeasy.run
 * @see Pool
 * @see NoneBufferPool
 */
public interface BufferPool<B extends Buffer> extends Pool<B, RuntimeException> {
	@Override
	B get();

	@Override
	void close(@NonNull B source);
}
