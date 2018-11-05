package shuchaowen.core.beans.proxy;

import shuchaowen.core.beans.BeanFactory;

public interface ProxyFactory {
	/**
	 * 获取一个代理类
	 * @param beanFactory
	 * @param type 要代理的类
	 * @return
	 */
	<T> T getProxy(BeanFactory beanFactory, Class<T> type) throws Exception;
}
