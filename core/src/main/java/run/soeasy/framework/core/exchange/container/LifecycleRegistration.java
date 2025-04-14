package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.Lifecycle;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 容器中的注册都存在生命周期
 * 
 * @author shuchaowen
 *
 */
public interface LifecycleRegistration extends Registration, Lifecycle {

	public static interface LifecycleRegistrationWrapper<W extends LifecycleRegistration>
			extends LifecycleRegistration, RegistrationWrapper<W> {
		@Override
		default void start() {
			getSource().start();
		}

		@Override
		default void stop() {
			getSource().stop();
		}

		@Override
		default boolean isRunning() {
			return getSource().isRunning();
		}
	}

	/**
	 * 在注册成功后调用此方法
	 */
	@Override
	void start();

	/**
	 * 在无效后调用此方法
	 */
	@Override
	void stop();

	/**
	 * 是否已成功调用start
	 */
	@Override
	boolean isRunning();
}
