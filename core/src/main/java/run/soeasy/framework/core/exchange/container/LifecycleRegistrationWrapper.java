package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 生命周期注册包装器接口，用于包装其他生命周期注册对象。
 * <p>
 * 该接口继承自{@link LifecycleRegistration}和{@link RegistrationWrapper}，
 * 提供对源注册对象的透明包装，并默认实现生命周期方法的转发。
 * </p>
 *
 * @param <W> 包装的目标注册类型，需继承LifecycleRegistration
 * @author soeasy.run
 */
@FunctionalInterface
public interface LifecycleRegistrationWrapper<W extends LifecycleRegistration>
		extends LifecycleRegistration, RegistrationWrapper<W> {
	/**
	 * 启动源注册对象的生命周期
	 * <p>
	 * 默认为转发调用{@link #getSource() getSource().start()}
	 */
	@Override
	default void start() {
		getSource().start();
	}

	/**
	 * 停止源注册对象的生命周期
	 * <p>
	 * 默认为转发调用{@link #getSource() getSource().stop()}
	 */
	@Override
	default void stop() {
		getSource().stop();
	}

	/**
	 * 检查源注册对象的生命周期是否正在运行
	 * <p>
	 * 默认为转发调用{@link #getSource() getSource().isRunning()}
	 * 
	 * @return 源注册对象的运行状态
	 */
	@Override
	default boolean isRunning() {
		return getSource().isRunning();
	}
}