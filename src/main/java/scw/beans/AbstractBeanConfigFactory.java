package scw.beans;

import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	public void addBean(BeanDefinition beanDefinition) {
		String id = beanDefinition.getId();
		if (beanMap.containsKey(id)) {
			throw new AlreadyExistsException(id);
		}

		beanMap.put(id, beanDefinition);
		String[] names = beanDefinition.getNames();
		if (names != null) {
			for (String n : names) {
				if (nameMappingMap.containsKey(n)) {
					throw new AlreadyExistsException(n);
				}
				nameMappingMap.put(n, id);
			}
		}
	}

	public Map<String, BeanDefinition> getBeanMap() {
		return beanMap;
	}

	public Map<String, String> getNameMappingMap() {
		return nameMappingMap;
	}
}
