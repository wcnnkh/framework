package scw.application;

import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.Init;
import scw.util.value.property.PropertyFactory;

public interface Application extends Init, Destroy{
	/**
	 * 获取实例工厂
	 * @return
	 */
	BeanFactory getBeanFactory();
	
	PropertyFactory getPropertyFactory();
}
