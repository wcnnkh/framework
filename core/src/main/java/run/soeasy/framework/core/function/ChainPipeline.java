package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import lombok.NonNull;

/**
 * Pipeline接口的链式实现类，继承自{@link ChainThrowingSupplier}，支持资源的链式转换与线程安全的关闭操作。
 * 整合了值的获取、转换、异常处理及资源关闭逻辑，适用于需要按步骤处理资源并确保最终释放的场景。
 *
 * @param <S> 源值类型（原始供应者提供的基础值类型）
 * @param <V> 目标值类型（经过当前流水线转换后的值类型）
 * @param <E> 源异常类型（原始操作可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（提供原始值的{@link ThrowingSupplier}子类）
 * @see Pipeline
 * @see ChainThrowingSupplier
 */
class ChainPipeline<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
		extends ChainThrowingSupplier<S, V, E, T, W> implements Pipeline<V, T> {

	/**
	 * 用于标记资源是否已关闭的原子布尔值，确保线程安全的关闭状态管理（CAS操作）
	 */
	private final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * 资源关闭时执行的操作，包含需要在关闭阶段完成的清理逻辑，不可为null
	 */
	@NonNull
	protected final ThrowingRunnable<? extends E> closeable;

	/**
	 * 构造链式流水线实例，初始化值转换、异常处理及关闭操作等核心组件。
	 *
	 * @param source          源值供应者，提供原始值的获取逻辑，不可为null
	 * @param mapper          源值到目标值的转换函数，不可为null
	 * @param endpoint        资源清理消费者，在值使用后执行额外清理（可为null）
	 * @param throwingMapper  异常转换函数，将源异常转换为目标异常，不可为null
	 * @param singleton       是否启用单例模式（值仅获取一次并缓存）
	 * @param closeable       关闭时执行的操作，包含资源释放逻辑，不可为null
	 */
	public ChainPipeline(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			boolean singleton, ThrowingRunnable<? extends E> closeable) {
		super(source, mapper, endpoint, throwingMapper, singleton);
		this.closeable = closeable;
	}

	/**
	 * 返回当前流水线实例，支持链式编程风格，标识当前实例具备自动关闭能力。
	 *
	 * @return 当前{@link ChainPipeline}实例本身
	 */
	@Override
	public Pipeline<V, T> closeable() {
		return this;
	}

	/**
	 * 关闭资源，执行注册的关闭操作（线程安全，确保仅执行一次）。
	 * 通过{@link AtomicBoolean#compareAndSet(boolean, boolean)}保证关闭逻辑仅触发一次，
	 * 执行{@link #closeable}中定义的清理操作，并通过异常转换函数处理可能的异常。
	 *
	 * @throws T 关闭过程中可能抛出的目标异常（经{@code throwingMapper}转换后）
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
	 * 检查当前流水线是否已关闭。
	 * 基于{@link #closed}的原子状态判断，反映资源是否已执行关闭操作。
	 *
	 * @return true表示已关闭，false表示未关闭
	 */
	@Override
	public boolean isClosed() {
		return closed.get();
	}

	/**
	 * 添加新的映射转换步骤，返回包含组合转换逻辑的新流水线实例。
	 * 新实例会先执行当前流水线的转换逻辑，再应用新的映射函数，保持原有关闭操作和异常处理策略。
	 *
	 * @param <R>    新的目标值类型（经过新增映射后的类型）
	 * @param mapper 新增的映射函数，接收当前目标值{@code V}并返回新值{@code R}，不可为null
	 * @return 新的{@link ChainPipeline}实例，包含组合后的转换逻辑
	 */
	@Override
	public <R> ChainPipeline<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainPipeline<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper, singleton,
				closeable);
	}
}