package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * Pool接口的链式实现类，继承自{@link ChainThrowingSupplier}，专注于资源的关闭管理与异常转换。
 * 作为资源池的具体实现，它封装了资源的获取源、异常转换逻辑和资源关闭操作，支持通过链式调用处理资源的映射与生命周期管理。
 *
 * @param <V> 资源类型（池管理的资源类型）
 * @param <S> 源异常类型（原始资源操作可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（提供资源的{@link ThrowingSupplier}子类）
 * @see Pool
 * @see ChainThrowingSupplier
 */
class ChainPool<V, S extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends V, ? extends S>>
		extends ChainThrowingSupplier<V, V, S, T, W> implements Pool<V, T> {
	/**
	 * 资源关闭消费者，用于执行资源的实际关闭逻辑，在{@link #close(Object)}中被调用
	 */
	private final ThrowingConsumer<? super V, ? extends S> closer;

	/**
	 * 构造链式资源池实例，初始化资源供应者、异常转换和关闭逻辑。
	 *
	 * @param source          资源的源供应者，提供资源的获取逻辑，不可为null
	 * @param throwingMapper  异常转换函数，将源异常（S类型）转换为目标异常（T类型），不可为null
	 * @param closer          资源关闭操作的消费者，接收资源并执行关闭逻辑，可为null（此时不执行额外关闭操作）
	 */
	public ChainPool(@NonNull W source, @NonNull Function<? super S, ? extends T> throwingMapper,
			ThrowingConsumer<? super V, ? extends S> closer) {
		super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper, false);
		this.closer = closer;
	}

	/**
	 * 关闭指定资源，执行注册的关闭逻辑并处理异常转换。
	 * 调用{@link #closer}的关闭操作，若过程中抛出异常，则通过{@code throwingMapper}转换为目标异常类型。
	 *
	 * @param source 需要关闭的资源实例
	 * @throws T 关闭过程中可能抛出的目标异常（经{@code throwingMapper}转换后）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void close(V source) throws T {
		try {
			closer.accept(source);
		} catch (Throwable e) {
			throw throwingMapper.apply((S) e);
		}
	}

	/**
	 * 对资源进行映射转换，返回支持自动关闭的{@link Pipeline}。
	 * 调用父类{@link Pool#map(ThrowingFunction)}的默认实现，保持资源池的关闭逻辑与映射转换的链式关联。
	 *
	 * @param <R> 映射后的资源类型
	 * @param mapper 资源转换函数，接收当前资源（V类型）并返回转换后的资源（R类型），不可为null
	 * @return 包含转换后资源的{@link Pipeline}实例，支持后续操作并自动管理资源关闭
	 */
	@Override
	public <R> Pipeline<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return Pool.super.map(mapper);
	}
}