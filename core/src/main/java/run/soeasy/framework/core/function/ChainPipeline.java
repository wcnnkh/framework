package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Pipeline接口的链式实现类，专注于资源的生命周期管理，支持线程安全的获取、转换、缓存及关闭操作。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>链式转换：通过{@link #map(ThrowingFunction)}实现多步类型转换，保留上游资源管理逻辑</li>
 *   <li>线程安全：使用{@link AtomicBoolean}标记关闭状态（CAS操作），同步块保证缓存初始化安全</li>
 *   <li>资源安全：通过清理函数（{@link ThrowingConsumer}）和额外关闭操作（{@link ThrowingRunnable}）确保资源释放</li>
 *   <li>异常统一：通过异常转换函数将源异常（E）转换为目标异常（T），简化异常处理</li>
 * </ul>
 * 
 * <p>适用场景：需要安全管理资源（如文件流、网络连接）生命周期的场景，支持在资源使用后自动释放，
 * 同时支持多步转换操作且保持类型安全。
 *
 * @param <S> 源值类型（原始资源供应者提供的基础数据类型）
 * @param <V> 目标值类型（当前流水线转换后的数据类型）
 * @param <E> 源异常类型（资源操作原始异常，如IO异常）
 * @param <T> 目标异常类型（统一对外抛出的异常类型，由{@code throwingMapper}转换而来）
 * @param <W> 源供应者类型（提供原始资源的{@link ThrowingSupplier}实现，支持抛出E类型异常）
 * @see Pipeline
 * @see ThrowingSupplier
 * @see ThrowingFunction
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ChainPipeline<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		implements Pipeline<V, T> {
	/**
	 * 原始资源的供应者，负责提供源值（S类型）。
	 * <p>是整个流水线的数据源，所有转换操作的原始数据均来自此供应者的{@link ThrowingSupplier#get()}方法，
	 * 调用时可能抛出E类型异常。
	 */
	private final W source;

	/**
	 * 源值到目标值的转换函数，将S类型转换为V类型。
	 * <p>不可为null，是链式转换的核心组件，支持在转换过程中抛出T类型异常，后续可通过{@link #map(ThrowingFunction)}追加转换步骤。
	 */
	@NonNull
	private final ThrowingFunction<? super S, ? extends V, T> mapper;

	/**
	 * 异常转换函数，将源异常（E类型）转换为目标异常（T类型）。
	 * <p>用于统一异常输出类型，将资源操作中产生的原始异常（如IOExcepiton）转换为业务层面的异常（如ServiceException）。
	 */
	private final Function<? super E, ? extends T> throwingMapper;

	/**
	 * 标记流水线是否已关闭的原子布尔值。
	 * <p>通过CAS操作（{@link AtomicBoolean#compareAndSet(boolean, boolean)}）保证线程安全，
	 * 初始值为{@code false}，关闭后设为{@code true}，防止重复执行关闭逻辑。
	 */
	private final AtomicBoolean closed;

	/**
	 * 源值的缓存供应者，用于存储首次获取的源值（S类型）。
	 * <p>仅在存在{@link #closer}时生效，通过volatile修饰保证多线程可见性，双重检查锁定实现延迟初始化，
	 * 避免重复获取资源（如重复打开文件流）。
	 */
	private volatile Supplier<? extends S> supplier;

	/**
	 * 源值的清理函数，在流水线关闭时对缓存的源值执行资源释放操作。
	 * <p>可为null：若为null，不执行源值清理；若不为null，在{@link #close()}时调用，用于释放资源（如关闭流、断开连接）。
	 */
	private final ThrowingConsumer<? super S, ? extends E> closer;

	/**
	 * 流水线关闭时的额外操作，用于处理上游资源或补充清理逻辑。
	 * <p>不可为null，通常用于触发上游流水线的关闭操作，确保整个链式调用中的资源都能被释放，
	 * 在{@link #close()}的finally块中执行，保证一定被调用。
	 */
	private final ThrowingRunnable<? extends E> closeable;

	/**
	 * 公共构造方法，初始化流水线核心组件。
	 * <p>自动创建原子布尔值（初始未关闭）和空缓存供应者，绑定资源供应、转换、清理及关闭逻辑，是创建实例的主要入口。
	 *
	 * @param source         源值供应者（提供原始资源）
	 * @param mapper         源值到目标值的转换函数（不可为null）
	 * @param throwingMapper 异常转换函数（将E转换为T）
	 * @param closer         源值清理函数（可为null，不清理源值时使用）
	 * @param closeable      关闭时的额外操作（不可为null，确保上游资源释放）
	 */
	public ChainPipeline(W source, ThrowingFunction<? super S, ? extends V, T> mapper,
			Function<? super E, ? extends T> throwingMapper, ThrowingConsumer<? super S, ? extends E> closer,
			ThrowingRunnable<? extends E> closeable) {
		this(source, mapper, throwingMapper, new AtomicBoolean(), null, closer, closeable);
	}

	/**
	 * 获取经过转换的目标值（V类型），支持缓存和线程安全检查。
	 * <p>行为说明：
	 * <ul>
	 *   <li>若流水线已关闭（{@link #isClosed()}为true），直接抛出{@link IllegalStateException}</li>
	 *   <li>若无清理函数（{@code closer == null}）：直接通过源供应者获取值，不缓存</li>
	 *   <li>若有清理函数（{@code closer != null}）：首次调用时通过双重检查锁定获取并缓存源值，后续直接使用缓存值</li>
	 * </ul>
	 *
	 * @return 转换后的目标值（V类型）
	 * @throws T                转换过程中抛出的目标异常（由mapper或异常转换函数产生）
	 * @throws IllegalStateException 当流水线已关闭时抛出
	 */
	@Override
	public V get() throws T {
		if (isClosed()) {
			throw new IllegalStateException("pipeline has already been operated upon or closed");
		}

		S value;
		if (this.closer == null) {
			value = run(this.source);
		} else {
			if (supplier == null) {
				synchronized (source) {
					if (supplier == null) {
						S singleton = run(source);
						this.supplier = () -> singleton;
					}
				}
			}
			value = supplier.get();
		}
		return this.mapper.apply(value);
	}

	/**
	 * 辅助方法：执行源供应者获取源值，并转换异常类型。
	 * <p>调用{@link ThrowingSupplier#get()}获取源值，若抛出异常则通过{@code throwingMapper}转换为T类型异常。
	 *
	 * @param source 源值供应者
	 * @return 源供应者提供的原始值（S类型）
	 * @throws T 源值获取失败或异常转换失败时抛出的目标异常
	 */
	@SuppressWarnings("unchecked")
	private S run(W source) throws T {
		try {
			return source.get();
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		}
	}

	/**
	 * 关闭流水线，执行资源清理操作，确保线程安全且仅执行一次。
	 * <p>执行流程：
	 * <ol>
	 *   <li>通过CAS操作（{@code closed.compareAndSet(false, true)}）确保关闭逻辑仅执行一次</li>
	 *   <li>若存在清理函数（{@code closer != null}），对缓存的源值执行清理操作（同步块保证线程安全）</li>
	 *   <li>在finally块中执行额外关闭操作（{@code closeable.run()}），确保上游资源释放</li>
	 * </ol>
	 *
	 * @throws T 清理操作或额外关闭操作中抛出的目标异常（经异常转换函数处理）
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
	 * 判断流水线是否已关闭。
	 * <p>基于{@link #closed}的原子状态，线程安全地反映是否执行过关闭操作。
	 *
	 * @return true表示已关闭，false表示未关闭
	 */
	@Override
	public boolean isClosed() {
		return closed.get();
	}

	/**
	 * 追加新的转换步骤，返回包含组合转换逻辑的新流水线实例。
	 * <p>新实例继承当前流水线的资源供应、异常处理、清理及关闭逻辑，仅将当前转换函数与新函数组合（{@code this.mapper.andThen(mapper)}），
	 * 实现链式转换且不影响原流水线状态。
	 *
	 * @param <R>    新的目标值类型（追加转换后的类型）
	 * @param mapper 新的转换函数（接收V类型，返回R类型，不可为null）
	 * @return 新的{@link ChainPipeline}实例，支持R类型的目标值
	 */
	@Override
	public <R> Pipeline<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainPipeline<>(this.source, this.mapper.andThen(mapper), throwingMapper, closed, supplier, closer,
				closeable);
	}
}