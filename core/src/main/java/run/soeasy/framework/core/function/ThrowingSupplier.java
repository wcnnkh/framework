package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的供应者接口，作为函数式接口支持获取可能抛出指定异常的结果， 并提供丰富的链式操作方法用于资源管理、结果转换和异常处理。
 * 相比Java标准库的{@link java.util.function.Supplier}，该接口允许在获取结果时声明抛出特定异常，
 * 适用于文件操作、网络请求、数据库访问等可能产生检查型异常的场景。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>函数式接口：仅包含一个抽象方法{@link #get()}，可作为lambda表达式或方法引用的目标类型</li>
 * <li>资源管理：通过{@link #closeable()}和{@link #onClose(ThrowingRunnable)}方法支持资源的关闭</li>
 * <li>结果转换：通过{@link #map(ThrowingFunction)}实现结果的链式映射</li>
 * <li>异常处理：通过{@link #throwing(Function)}将异常类型转换为目标类型</li>
 * <li>空值安全：通过{@link #optional()}将结果包装为支持异常的Optional容器</li>
 * </ul>
 *
 * @param <T> 供应结果的类型
 * @param <E> 可能抛出的异常类型，必须是{@link Throwable}的子类
 * @see Pipeline
 * @see ThrowingFunction
 * @see ThrowingOptional
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

	/**
	 * 获取供应的结果，可能抛出指定类型的异常。 作为接口的核心方法，实现类需在此方法中定义具体的结果获取逻辑（如资源加载、数据计算等）。
	 *
	 * @return 供应的结果对象，类型为T
	 * @throws E 可能抛出的异常，类型为E（由具体实现决定）
	 */
	T get() throws E;

	/**
	 * 创建一个支持资源关闭的管道（Pipeline），用于管理资源生命周期。
	 * 该Pipeline会关联当前供应者，后续可通过Pipeline的方法注册关闭操作或进行结果转换， 确保资源在使用后被正确释放。
	 *
	 * @return 包含当前供应者的Pipeline实例，可进行后续的资源关闭操作
	 */
	default Pipeline<T, E> closeable() {
		return new ChainPipeline<>(this, ThrowingFunction.identity(), Function.identity(), null,
				ThrowingRunnable.ignore());
	}

	/**
	 * 注册一个针对供应结果的资源关闭回调，并返回包含该回调的Pipeline。
	 * 当Pipeline关闭时，会调用该回调对供应的结果（T类型）执行清理操作（如关闭流、释放连接等）。
	 *
	 * @param closer 资源关闭时的消费操作，接收供应结果T并可能抛出异常E，非空
	 * @return 包含当前供应者和关闭回调的Pipeline实例
	 */
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> closer) {
		return new ChainPipeline<>(this, ThrowingFunction.identity(), Function.identity(), closer,
				ThrowingRunnable.ignore());
	}

	/**
	 * 注册一个无参的资源关闭回调，并返回包含该回调的Pipeline。 当Pipeline关闭时，会执行该回调进行额外的清理操作（如日志记录、全局资源汇总等）。
	 *
	 * @param closeable 无参的关闭操作，可能抛出异常E，非空
	 * @return 包含当前供应者和关闭回调的Pipeline实例
	 */
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return new ChainPipeline<>(this, ThrowingFunction.identity(), Function.identity(), null, closeable);
	}

	/**
	 * 对供应结果进行映射转换，返回新的ThrowingSupplier实例。
	 * 新实例会先通过当前供应者获取结果，再应用映射函数转换为目标类型R，保持原有的异常类型E。
	 *
	 * @param <R>    映射后的结果类型
	 * @param mapper 用于转换结果的函数，接收当前结果T并返回目标类型R，可能抛出异常E，非空
	 * @return 新的ThrowingSupplier，其结果为映射后的类型R
	 */
	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new ChainThrowingSupplier<>(this, ThrowingRunnable.ignore(), mapper, Function.identity());
	}

	/**
	 * 转换异常类型，将原有异常E转换为新的异常类型R。 通过异常转换函数，可将原始操作抛出的异常E统一转换为业务相关的异常类型R，便于异常处理。
	 *
	 * @param <R>            转换后的异常类型，必须是Throwable的子类
	 * @param throwingMapper 用于转换异常的函数，接收原始异常E并返回目标异常R，非空
	 * @return 新的ThrowingSupplier，其异常类型为R
	 */
	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new ChainThrowingSupplier<>(this, ThrowingRunnable.ignore(), ThrowingFunction.identity(),
				throwingMapper);
	}

	/**
	 * 将供应结果包装为支持异常处理的ThrowingOptional容器。 该容器可安全处理结果为null的场景，并保留异常转换和链式操作能力。
	 *
	 * @return 包含当前供应者的ThrowingOptional实例
	 */
	default ThrowingOptional<T, E> optional() {
		return new ChainThrowingOptional<>(this, ThrowingRunnable.ignore(), ThrowingFunction.identity(),
				Function.identity());
	}
}