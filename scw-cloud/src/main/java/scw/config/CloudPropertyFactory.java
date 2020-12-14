package scw.config;

import scw.value.property.BasePropertyFactory;

/**
 * 分布式配置中心
 * @author asus1
 *
 */
public interface CloudPropertyFactory extends BasePropertyFactory{
	boolean put(String key, String value);
	
	boolean remove(String key);
}
