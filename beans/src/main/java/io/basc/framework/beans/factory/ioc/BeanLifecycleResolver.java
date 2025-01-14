package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.Executable;

/**
 * 依赖注入生命周期解析
 * 
 * @author shuchaowen
 *
 */
public interface BeanLifecycleResolver {

	/**
	 * 是否是启动时执行
	 * 
	 * @param executable
	 * @return
	 */
	boolean isStartupExecute(BeanFactory beanFactory, Executable executable);

	/**
	 * 是否在停止时执行
	 * 
	 * @param executable
	 * @return
	 */
	boolean isStoppedExecute(BeanFactory beanFactory, Executable executable);
}
