package run.soeasy.framework.core.function;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChainThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
		implements ThrowingSupplier<V, T> {
	@NonNull
	protected final W source;

	/**
	 * 值映射函数，将源值转换为目标类型。
	 */
	@NonNull
	protected final ThrowingFunction<? super S, ? extends V, T> mapper;

	/**
	 * 资源清理消费者，在值使用完毕后执行清理操作。
	 */
	protected final ThrowingConsumer<? super S, ? extends E> endpoint;

	/**
	 * 异常转换函数，将源异常转换为目标异常类型。
	 */
	@NonNull
	protected final Function<? super E, ? extends T> throwingMapper;

	/**
	 * 是否启用单例模式，true表示值仅获取一次并缓存。
	 */
	protected final boolean singleton;

	/**
	 * 单例模式下的值缓存，使用volatile确保线程可见性。
	 */
	protected volatile Supplier<V> singletonSupplier;

	/**
	 * 获取映射后的值，支持单例模式和非单例模式。 在单例模式下，值会被缓存且仅获取一次；非单例模式下每次调用都会重新获取并映射值。
	 *
	 * @return 映射后的值
	 * @throws T 可能抛出的目标异常类型
	 */
	@Override
	public V get() throws T {
		if (singleton) {
			if (singletonSupplier == null) {
				synchronized (this) {
					if (singletonSupplier == null) {
						V value = run(source);
						singletonSupplier = () -> value;
					}
				}
			}
			return singletonSupplier.get();
		}
		return run(this.source);
	}

	/**
	 * 执行值获取和映射的核心逻辑，包括异常处理和资源清理。 该方法会调用源供应者获取值，应用映射函数，最后执行资源清理操作。
	 *
	 * @param supplier 源供应者
	 * @return 映射后的值
	 * @throws T 可能抛出的目标异常类型
	 */
	@SuppressWarnings("unchecked")
	public V run(W supplier) throws T {
		try {
			S source = supplier.get();
			try {
				return mapper.apply(source);
			} finally {
				endpoint.accept(source);
			}
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		}
	}

	/**
	 * 添加额外的映射转换，返回新的MappingThrowingSupplier实例。 新实例会先应用当前映射函数，再应用指定的映射函数。
	 *
	 * @param <R>    新的目标值类型
	 * @param mapper 额外的映射函数
	 * @return 新的MappingThrowingSupplier实例
	 */
	@Override
	public <R> ThrowingSupplier<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainThrowingSupplier<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
				singleton);
	}
}