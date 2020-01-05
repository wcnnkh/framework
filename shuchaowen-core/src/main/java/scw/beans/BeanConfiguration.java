package scw.beans;

import java.util.Collection;
import java.util.Map;

import scw.core.Destroy;
import scw.core.Init;

public interface BeanConfiguration {
	/**
	 * ID对应的bean
	 * 
	 * @return
	 */
	Map<String, BeanDefinition> getBeanMap();

	/**
	 * 名称映射关系
	 * 
	 * @return
	 */
	Map<String, String> getNameMappingMap();

	/**
	 * 容器关闭时调用
	 * 
	 * @return
	 */
	Collection<Destroy> getDestroys();

	/**
	 * 容器初始化时调用
	 * 
	 * @return
	 */
	Collection<Init> getInits();
}
