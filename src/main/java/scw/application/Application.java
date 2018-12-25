package scw.application;

import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;

public interface Application {
	/**
	 * 获取实例工厂
	 * @return
	 */
	BeanFactory getBeanFactory();
	
	PropertiesFactory getPropertiesFactory();
	
	/**
	 * 初始化
	 */
	void init();
	
	/**
	 * 销毁
	 */
	void destroy();
}
