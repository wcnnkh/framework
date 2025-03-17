package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.execution.Executable;

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
