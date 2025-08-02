package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 配置化服务接口，集成服务包含能力与操作结果反馈，支持服务实例的转换与组合管理。
 * <p>
 * 该接口继承自{@link Include}和{@link Receipt}，既可以管理服务实例的生命周期，
 * 又能反馈操作结果状态，适用于需要跟踪服务配置过程并支持动态转换的场景。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>服务包含：继承{@link Include}，支持服务实例的迭代与转换</li>
 * <li>结果反馈：继承{@link Receipt}，提供操作成功/失败的状态标识</li>
 * <li>注册组合：通过{@link #and}方法组合多个注册操作的生命周期</li>
 * <li>流式转换：通过{@link #map}方法支持服务实例流的类型转换</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see Include
 * @see Receipt
 */
public interface Configured<S> extends Include<S>, Receipt {

	/**
	 * 组合注册操作并返回新的Configured实例
	 * <p>
	 * 将当前Configured与另一个注册操作组合，新实例的取消操作会同时取消两者。
	 * 该操作会创建一个新的{@link AndConfigured}实例，包装多个注册的功能。
	 * 
	 * @param registration 要组合的注册操作，不可为null
	 * @return 组合后的Configured实例
	 * @throws NullPointerException 若registration为null
	 */
	@Override
	default Configured<S> and(Registration registration) {
		return new AndConfigured<>(this, registration);
	}

	/**
	 * 获取表示配置失败的Configured实例
	 * <p>
	 * 该实例的{@link #isSuccess()}方法返回false，{@link #isCancelled()}返回true。
	 * 
	 * @param <S> 服务实例类型
	 * @return 失败的Configured实例
	 */
	@SuppressWarnings("unchecked")
	public static <S> Configured<S> failure() {
		return (Configured<S>) Included.FAILURE_CONFIGURED;
	}

	/**
	 * 转换服务实例流并返回新的Configured实例
	 * <p>
	 * 通过流式转换函数对服务实例进行转换，并根据resize参数决定是否调整结果大小。
	 * 该操作会创建一个新的{@link ConvertedConfigured}实例，包装原Configured的功能。
	 * 
	 * @param <U>       转换后的服务实例类型
	 * @param resize    是否调整结果大小（true表示按转换结果调整，false表示保持原大小）
	 * @param converter 流式转换函数，不可为null
	 * @return 转换后的Configured实例
	 * @throws NullPointerException 若converter为null
	 */
	@Override
	default <U> Configured<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedConfigured<>(this, resize, converter);
	}
}