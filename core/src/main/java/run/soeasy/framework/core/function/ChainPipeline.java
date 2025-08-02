package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import lombok.NonNull;

class ChainPipeline<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
		extends ChainThrowingSupplier<S, V, E, T, W> implements Pipeline<V, T> {

	private final AtomicBoolean closed = new AtomicBoolean();

	@NonNull
	protected final ThrowingRunnable<? extends E> closeable;

	public ChainPipeline(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			boolean singleton, ThrowingRunnable<? extends E> closeable) {
		super(source, mapper, endpoint, throwingMapper, singleton);
		this.closeable = closeable;
	}

	@Override
	public Pipeline<V, T> closeable() {
		return this;
	}

	/**
	 * 关闭资源，执行注册的关闭操作。
	 *
	 * @throws T 可能抛出的目标异常类型
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void close() throws T {
		if (closed.compareAndSet(false, true)) {
			try {
				closeable.run();
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			}
		}
	}

	/**
	 * 检查资源是否已关闭。 在单例模式下，资源在首次获取后即被标记为关闭；非单例模式下始终返回false。
	 *
	 * @return 资源是否已关闭
	 */
	@Override
	public boolean isClosed() {
		return closed.get();
	}

	/**
	 * 添加额外的映射转换，返回新的MappingThrowingSupplier实例。 新实例会先应用当前映射函数，再应用指定的映射函数。
	 *
	 * @param <R>    新的目标值类型
	 * @param mapper 额外的映射函数
	 * @return 新的MappingThrowingSupplier实例
	 */
	@Override
	public <R> ChainPipeline<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainPipeline<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper, singleton,
				closeable);
	}
}
