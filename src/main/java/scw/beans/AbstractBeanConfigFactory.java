package scw.beans;

import java.util.HashMap;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Map<String, Bean> beanMap = new HashMap<String, Bean>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	public void putBean(String name, Bean bean) {
		if (beanMap.containsKey(name)) {
			throw new AlreadyExistsException(name);
		}

		beanMap.put(name, bean);
		if (bean.getNames() != null) {
			for (String n : bean.getNames()) {
				if (nameMappingMap.containsKey(n)) {
					throw new AlreadyExistsException(n);
				}
				nameMappingMap.put(n, bean.getId());
			}
		}
	}

	public Map<String, Bean> getBeanMap() {
		return beanMap;
	}

	public Map<String, String> getNameMappingMap() {
		return nameMappingMap;
	}
}
