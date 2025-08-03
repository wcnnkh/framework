package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Pipeline接口的链式实现类，负责资源的获取、转换、缓存及线程安全的关闭操作。
 * 整合了值的供应、映射转换、异常处理和资源清理逻辑，适用于需要安全管理资源生命周期的场景， 确保资源在使用后被正确释放，且支持链式转换操作。
 *
 * @param <S> 源值类型（原始供应者提供的基础值类型）
 * @param <V> 目标值类型（经过当前流水线转换后的值类型）
 * @param <E> 源异常类型（原始资源操作可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（提供源值的{@link ThrowingSupplier}实现类）
 * @see Pipeline
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ChainPipeline<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		implements Pipeline<V, T> {
	/**
	 * 源值供应者，提供原始值的获取逻辑，是流水线的数据源
	 */
	private final W source;

	/**
	 * 源值到目标值的映射转换函数，负责将S类型转换为V类型，不可为null
	 */
	@NonNull
	private final ThrowingFunction<? super S, ? extends V, T> mapper;

	/**
	 * 异常转换函数，将源异常（E类型）转换为目标异常（T类型），用于统一异常处理
	 */
	private final Function<? super E, ? extends T> throwingMapper;

	/**
	 * 原子布尔值，用于标记流水线是否已关闭，通过CAS操作确保线程安全的状态管理
	 */
	private final AtomicBoolean closed;

	/**
	 * 源值的缓存供应者，首次获取后缓存源值，避免重复获取，volatile修饰保证线程可见性
	 */
	private volatile Supplier<? extends S> supplier;

	/**
	 * 源值的清理消费者，在关闭时对缓存的源值执行清理操作（如释放资源）
	 */
	private final ThrowingConsumer<? super S, ? extends E> closer;

	/**
	 * 流水线关闭时执行的额外操作，包含补充清理逻辑。
	 */
	private final ThrowingRunnable<? extends E> closeable;

	/**
	 * 公共构造方法，初始化流水线的核心组件。 自动创建原子布尔值（初始为未关闭状态）和空缓存供应者，绑定源值供应、转换、清理及关闭逻辑。
	 *
	 * @param source         源值供应者，提供原始值
	 * @param mapper         源值到目标值的转换函数
	 * @param throwingMapper 异常转换函数
	 * @param closer         源值清理消费者（可为null，此时不执行额外清理）
	 * @param closeable      关闭时的额外操作（不可为null）
	 */
	public ChainPipeline(W source, ThrowingFunction<? super S, ? extends V, T> mapper,
			Function<? super E, ? extends T> throwingMapper, ThrowingConsumer<? super S, ? extends E> closer,
			ThrowingRunnable<? extends E> closeable) {
		this(source, mapper, throwingMapper, new AtomicBoolean(), null, closer, closeable);
	}

	/**
	 * 获取经过转换的目标值，支持线程安全的延迟初始化和缓存。 首次调用时通过源供应者获取值并缓存，后续调用直接使用缓存值；同步处理确保多线程环境下的安全初始化。
	 *
	 * @return 转换后的目标值（V类型）
	 * @throws T 转换过程中可能抛出的目标异常（经{@code throwingMapper}转换后）
	 */
	@Override
	public V get() throws T {
		S value;
		if (this.closer == null) {
			value = run(this.source);
		} else {
			if (supplier == null) {
				synchronized (source) {
					S singleton = run(source);
					this.supplier = () -> singleton;
				}
			}
			value = supplier.get();
		}
		return this.mapper.apply(value);
	}

	@SuppressWarnings("unchecked")
	private S run(W source) throws T {
		try {
			return source.get();
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		}
	}

	/**
	 * 关闭流水线，执行资源清理操作，线程安全且确保仅执行一次。 步骤：1. 通过CAS判断是否已关闭，未关闭则继续；2. 对缓存的源值执行清理（若存在）； 3.
	 * 执行额外的关闭操作；4. 所有异常均通过{@code throwingMapper}转换为目标异常。
	 *
	 * @throws T 关闭过程中可能抛出的目标异常
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void close() throws T {
		if (closed.compareAndSet(false, true)) {
			try {
				if (closer != null) {
					synchronized (source) {
						if (supplier != null) {
							try {
								closer.accept(supplier.get());
							} catch (Throwable e) {
								throw throwingMapper.apply((E) e);
							}
						}
					}
				}
			} finally {
				try {
					closeable.run();
				} catch (Throwable e) {
					throw throwingMapper.apply((E) e);
				}
			}
		}
	}

	/**
	 * 判断流水线是否已关闭。 基于{@link #closed}的原子状态，反映是否执行过关闭操作。
	 *
	 * @return true表示已关闭，false表示未关闭
	 */
	@Override
	public boolean isClosed() {
		return closed.get();
	}

	/**
	 * 添加新的映射转换步骤，返回包含组合转换逻辑的新流水线实例。 新实例继承当前流水线的源供应者、异常处理、清理及关闭逻辑，仅追加新的映射函数。
	 *
	 * @param <R>    新的目标值类型（新增映射后的类型）
	 * @param mapper 新增的映射函数，接收当前目标值（V类型）并返回新值（R类型），不可为null
	 * @return 新的{@link ChainPipeline}实例，包含组合后的转换逻辑
	 */
	@Override
	public <R> Pipeline<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainPipeline<>(this.source, this.mapper.andThen(mapper), throwingMapper, closed, supplier, closer,
				closeable);
	}
}