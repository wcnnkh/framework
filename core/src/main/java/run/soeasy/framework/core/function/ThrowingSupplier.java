package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的供应者接口，扩展了Java标准库的Supplier接口，允许在获取结果时抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的供应操作场景， 并提供了丰富的链式操作方法用于异常处理和资源管理。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>函数式接口：可作为lambda表达式或方法引用的目标类型</li>
 * <li>异常处理：允许在get()方法中抛出指定类型的异常E</li>
 * <li>链式操作：提供map、throwing等方法实现函数式编程风格的链式调用</li>
 * <li>资源管理：通过closeable、onClose等方法支持资源关闭回调</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>需要抛出特定异常的资源获取操作</li>
 * <li>结合try-with-resources模式的资源供应</li>
 * <li>需要异常转换的函数式编程场景</li>
 * <li>需要统一处理异常的供应操作链</li>
 * </ul>
 *
 * @param <T> 供应结果的类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see java.util.function.Supplier
 * @see ThrowingFunction
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

	/**
	 * 获取供应的结果，可能抛出异常E。 该方法是函数式接口的抽象方法，必须由实现类提供具体实现。
	 *
	 * @return 供应的结果
	 * @throws E 可能抛出的指定类型异常
	 */
	T get() throws E;

	/**
	 * 创建一个可关闭的管道，自动注册默认的关闭回调。 返回的Pipeline会在使用完毕后调用默认的忽略回调，适用于需要资源清理的场景。
	 *
	 * @return 配置了默认关闭回调的Pipeline实例
	 */
	default Pipeline<T, E> closeable() {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, ThrowingRunnable.ignore());
	}

	/**
	 * 创建一个可关闭的管道，注册自定义的关闭回调函数。 当Pipeline使用完毕时，会调用指定的consumer处理资源。
	 *
	 * @param consumer 关闭时执行的回调函数，不可为null
	 * @return 配置了自定义关闭回调的Pipeline实例
	 */
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), consumer, Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	/**
	 * 创建一个可关闭的管道，注册自定义的关闭操作。 当Pipeline使用完毕时，会调用指定的closeable操作。
	 *
	 * @param closeable 关闭时执行的操作，不可为null
	 * @return 配置了自定义关闭操作的Pipeline实例
	 */
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, closeable);
	}

	/**
	 * 对供应的结果进行映射转换，返回新的ThrowingSupplier。 该方法支持函数式编程的map操作，将T类型的结果转换为R类型。
	 *
	 * @param <R>    映射后的结果类型
	 * @param mapper 映射函数，不可为null
	 * @return 映射后的ThrowingSupplier实例
	 */
	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappingThrowingSupplier<>(this, mapper, ThrowingConsumer.ignore(), Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	/**
	 * 转换异常类型，返回新的ThrowingSupplier。 该方法允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
	 *
	 * @param <R>            新的异常类型，必须是Throwable的子类
	 * @param throwingMapper 异常转换函数，不可为null
	 * @return 异常类型转换后的ThrowingSupplier实例
	 */
	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				throwingMapper, false, ThrowingRunnable.ignore());
	}

	/**
	 * 创建单例模式的Pipeline，确保结果只被获取一次。 返回的Pipeline会缓存结果，后续调用将返回相同的结果，适用于单例场景。
	 *
	 * @return 单例模式的Pipeline实例
	 */
	default Pipeline<T, E> singleton() {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), true, ThrowingRunnable.ignore());
	}

	/**
	 * 将结果包装为ThrowingOptional，支持Optional风格的操作。
	 * 返回的ThrowingOptional允许对可能为null的结果进行安全操作，并处理异常。
	 *
	 * @return ThrowingOptional实例
	 */
	default ThrowingOptional<T, E> optional() {
		return new MappingThrowingOptional<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, ThrowingRunnable.ignore());
	}

	public static <A, B extends Throwable> ThrowingSupplier<A, B> cast(Class<A> requriedType, Class<B> throwingType,
			@NonNull ThrowingSupplier<? extends A, ? extends B> throwingSupplier) {
		return throwingSupplier.map(requriedType::cast).throwing(throwingType::cast);
	}
}