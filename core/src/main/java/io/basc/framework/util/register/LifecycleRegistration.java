package io.basc.framework.util.register;

import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Registration;

/**
 * 容器中的注册都存在生命周期
 * 
 * @author shuchaowen
 *
 */
public interface LifecycleRegistration extends Registration, Lifecycle {
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
