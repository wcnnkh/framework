package scw.application;

import scw.beans.BeanFactory;
import scw.beans.Destroy;
import scw.beans.Init;
import scw.value.property.PropertyFactory;

public interface Application extends Init, Destroy{
	public void init();
	
	public void destroy();
	
	/**
	 * 获取实例工厂
	 * @return
	 */
	BeanFactory getBeanFactory();
	
	PropertyFactory getPropertyFactory();
}