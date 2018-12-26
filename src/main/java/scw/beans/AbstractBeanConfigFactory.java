package scw.beans;

import java.util.HashMap;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Map<String, Bean> beanMap = new HashMap<String, Bean>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	protected void putBean(String name, Bean bean) {
		if (beanMap.containsKey(name)) {
			throw new AlreadyExistsException(name);
		}
		beanMap.put(name, bean);
	}

	public boolean registerNameMapping(String name, String mappingName) {
		if (nameMappingMap.containsKey(name)) {
			throw new AlreadyExistsException(name);
		}

		nameMappingMap.put(name, mappingName);
		return true;
	}

	public Map<String, Bean> getBeanMap() {
		return beanMap;
	}

	public Map<String, String> getNameMappingMap() {
		return nameMappingMap;
	}
}
