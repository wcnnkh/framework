package run.soeasy.framework.core.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 流水线接口，继承自{@link ThrowingSupplier}，提供资源的链式处理、自动关闭及异常转换能力。
 * 用于管理资源从获取、转换到释放的完整生命周期，支持通过链式调用组合多个操作，确保资源在使用后被正确释放，
 * 特别适用于文件、网络连接、数据库资源等需要显式关闭的场景。
 *
 * @param <T> 流水线处理的资源类型
 * @param <E> 操作中可能抛出的异常类型，必须是{@link Throwable}的子类
 * @see ThrowingSupplier
 * @see ThrowingFunction
 * @see ThrowingOptional
 */
public interface Pipeline<T, E extends Throwable> extends ThrowingSupplier<T, E> {

	/**
	 * 创建一个空的流水线实例，不包含任何资源或操作。
	 * 空流水线的{@link #get()}方法通常返回null，{@link #close()}方法无实际操作。
	 *
	 * @param <T> 资源类型（空流水线中无实际意义）
	 * @param <E> 异常类型（空流水线中无实际意义）
	 * @return 空的流水线实例
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Pipeline<T, E> empty() {
		return (Pipeline<T, E>) EmptyPipeline.INSTANCE;
	}

	/**
	 * 基于资源供应者创建流水线实例，将供应者包装为流水线以支持后续的链式操作。
	 *
	 * @param <T>      资源类型
	 * @param <E>      异常类型
	 * @param supplier 资源供应者，提供资源的获取逻辑，非空
	 * @return 新的流水线实例，关联指定的资源供应者
	 */
	public static <T, E extends Throwable> Pipeline<T, E> forSupplier(ThrowingSupplier<T, E> supplier) {
		return supplier.closeable();
	}

	/**
	 * 为实现{@link Closeable}接口的资源创建流水线，自动绑定资源关闭逻辑。
	 * 流水线会在关闭时调用{@link Closeable#close()}方法释放资源，无需手动注册关闭操作。
	 *
	 * @param <T>               资源类型，必须实现{@link Closeable}接口
	 * @param closeableSupplier 可关闭资源的供应者，提供资源的获取逻辑，非空
	 * @return 支持自动关闭的流水线实例，异常类型固定为{@link IOException}
	 */
	public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
			ThrowingSupplier<T, IOException> closeableSupplier) {
		return closeableSupplier.onClose(Closeable::close).closeable();
	}

	/**
	 * 创建支持自动关闭的资源供应者，获取资源后会在适当时候自动调用当前流水线的{@link #close()}方法。
	 * 适用于需要将流水线转换为普通供应者，但仍需保证资源释放的场景。
	 *
	 * @return 支持自动关闭的{@link ThrowingSupplier}实例
	 */
	default ThrowingSupplier<T, E> autoCloseable() {
		return new ChainThrowingSupplier<>(this, this::close, ThrowingFunction.identity(), Function.identity());
	}

	/**
	 * 将流水线的资源结果包装为支持异常处理的{@link ThrowingOptional}容器。
	 * 包装后的Optional保留原有的资源获取和关闭逻辑，同时支持空值安全的链式操作。
	 *
	 * @return 包含当前流水线资源的{@link ThrowingOptional}实例
	 */
	@Override
	default ThrowingOptional<T, E> optional() {
		return new ChainThrowingOptional<>(this, this::close, ThrowingFunction.identity(), Function.identity());
	}

	/**
	 * 关闭当前流水线，释放关联的资源。 且保证关闭操作仅执行一次（线程安全）。
	 *
	 * @throws E 关闭过程中可能抛出的异常
	 */
	void close() throws E;

	/**
	 * 返回当前流水线实例，支持方法链编程风格，明确标识当前实例具备自动关闭能力。
	 *
	 * @return 当前流水线实例本身
	 */
	@Override
	default Pipeline<T, E> closeable() {
		return this;
	}

	/**
	 * 判断当前流水线是否已关闭。 已关闭的流水线通常无法再获取资源，且所有关闭回调均已执行。
	 *
	 * @return true表示已关闭，false表示未关闭
	 */
	boolean isClosed();

	/**
	 * 对流水线中的资源进行映射转换，返回包含转换后资源的新流水线。 新流水线会继承原流水线的关闭逻辑，确保原始资源在使用后被正确释放。
	 *
	 * @param <R>    转换后的资源类型
	 * @param mapper 资源转换函数，接收当前资源（T类型）并返回转换后的资源（R类型），可能抛出异常E，非空
	 * @return 包含转换后资源的新流水线实例
	 */
	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new ChainPipeline<>(this, mapper, Function.identity(), null, this::close);
	}

	/**
	 * 注册针对资源的关闭消费回调，返回包含该回调的新流水线。 当流水线关闭时，会先执行原有关闭逻辑，再调用该回调对资源（T类型）进行清理。
	 *
	 * @param consumer 资源关闭时的消费操作，接收资源T并可能抛出异常E，非空
	 * @return 包含新关闭回调的流水线实例
	 */
	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return new ChainPipeline<>(this, ThrowingFunction.identity(), Function.identity(), consumer, this::close);
	}

	/**
	 * 注册无参的关闭回调，返回包含该回调的新流水线。 当流水线关闭时，会先执行原有关闭逻辑，再执行该回调（如日志记录、额外资源释放等）。
	 *
	 * @param closeable 无参的关闭操作，可能抛出异常E，非空
	 * @return 包含新关闭回调的流水线实例
	 */
	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		ThrowingRunnable<E> close = this::close;
		return new ChainPipeline<>(this, ThrowingFunction.identity(), Function.identity(), null,
				close.andThen(closeable));
	}

	/**
	 * 转换流水线操作可能抛出的异常类型，返回支持新异常类型的流水线。 通过异常转换函数将原始异常（E类型）转换为目标异常（R类型），便于统一异常处理。
	 *
	 * @param <R>            新的异常类型，必须是{@link Throwable}的子类
	 * @param throwingMapper 异常转换函数，接收原始异常E并返回目标异常R，非空
	 * @return 异常类型转换后的流水线实例
	 */
	@Override
	default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new ChainPipeline<>(this, ThrowingFunction.identity(), throwingMapper, null, this::close);
	}
}