package scw.application;

import scw.beans.BeanFactory;

public interface Application {
	/**
	 * 获取实例工厂
	 * @return
	 */
	BeanFactory getBeanFactory();
	
	/**
	 * 初始化
	 */
	void init();
	
	/**
	 * 销毁
	 */
	void destroy();
}
