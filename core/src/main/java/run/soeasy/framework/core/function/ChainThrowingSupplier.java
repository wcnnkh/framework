package run.soeasy.framework.core.function;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 链式可抛出异常的供应者实现类，用于构建值的获取、转换、异常处理和资源清理的链式操作。
 * 支持单例模式（值仅获取一次并缓存），适用于需要组合多个转换步骤或处理资源生命周期的场景。
 *
 * @param <S> 源值类型（原始供应者提供的值类型）
 * @param <V> 目标值类型（经过映射转换后的值类型）
 * @param <E> 源异常类型（原始供应者可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（必须是ThrowingSupplier的子类型，提供原始值）
 * @see ThrowingSupplier
 */
@RequiredArgsConstructor
public class ChainThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
		implements ThrowingSupplier<V, T> {
	/**
	 * 源供应者，提供原始值的获取逻辑，不可为null
	 */
	@NonNull
	protected final W source;

	/**
	 * 值映射函数，用于将源值（S类型）转换为目标值（V类型），不可为null
	 */
	@NonNull
	protected final ThrowingFunction<? super S, ? extends V, T> mapper;

	/**
	 * 资源清理消费者，在值使用完毕后执行资源释放操作（如关闭流、连接等），可为null
	 */
	protected final ThrowingConsumer<? super S, ? extends E> endpoint;

	/**
	 * 异常转换函数，用于将源异常（E类型）转换为目标异常（T类型），不可为null
	 */
	@NonNull
	protected final Function<? super E, ? extends T> throwingMapper;

	/**
	 * 是否启用单例模式：true表示值仅获取一次并缓存，后续调用直接返回缓存值；false表示每次调用重新获取
	 */
	protected final boolean singleton;

	/**
	 * 单例模式下的缓存供应者，用于存储已获取并转换的值，确保线程安全的延迟初始化
	 */
	protected volatile Supplier<V> singletonSupplier;

	/**
	 * 获取经过映射转换后的目标值，支持单例模式和非单例模式。
	 * <p>单例模式逻辑：首次调用时获取并缓存值，后续调用直接返回缓存值（线程安全）；
	 * 非单例模式：每次调用均重新获取源值并执行转换。
	 *
	 * @return 映射后的目标值（V类型）
	 * @throws T 可能抛出的目标异常（经过throwingMapper转换后的异常）
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
	 * 执行值获取、转换、资源清理的核心逻辑。
	 * <p>步骤：1. 通过源供应者获取原始值；2. 应用映射函数转换为目标值；
	 * 3. 无论转换成功与否，执行资源清理；4. 捕获源异常并转换为目标异常。
	 *
	 * @param supplier 源供应者（提供原始值）
	 * @return 映射后的目标值（V类型）
	 * @throws T 转换过程中可能抛出的目标异常
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
	 * 添加额外的映射转换步骤，返回新的链式供应者实例。
	 * <p>新实例会先执行当前实例的映射逻辑，再应用新的映射函数，形成链式转换。
	 *
	 * @param <R>    新的目标值类型
	 * @param mapper 额外的映射函数（接收当前目标值V，返回新目标值R），不可为null
	 * @return 新的ChainThrowingSupplier实例，包含组合后的映射逻辑
	 */
	@Override
	public <R> ThrowingSupplier<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainThrowingSupplier<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
				singleton);
	}
}