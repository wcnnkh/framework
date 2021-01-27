package scw.env;

import scw.io.Resource;
import scw.io.ResourcePatternResolver;
import scw.io.resolver.PropertiesLoader;

public interface EnvironmentResourceLoader extends ResourcePatternResolver, PropertiesLoader{

	/**
	 * 可以使用的资源(并不表示资源已存在),使用优先级从高到低
	 * 
	 * @see Resource#exists()
	 * @param locationPattern
	 * @return
	 */
	Resource[] getResources(String locationPattern);
	
	/**
	 * 资源是否存在
	 * @see Resource#exists()
	 * @see #getResource(String)
	 * @param location
	 * @return
	 */
	boolean exists(String location);
}
