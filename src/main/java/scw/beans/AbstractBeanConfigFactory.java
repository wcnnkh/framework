package scw.beans;

import java.util.HashMap;
import java.util.Map;

import scw.common.exception.AlreadyExistsException;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Map<String, Bean> beanMap = new HashMap<String, Bean>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	public void addBean(Bean bean) {
		String id = bean.getId();
		if (beanMap.containsKey(id)) {
			throw new AlreadyExistsException(id);
		}

		beanMap.put(id, bean);
		String[] names = bean.getNames();
		if (names != null) {
			for (String n : names) {
				if (nameMappingMap.containsKey(n)) {
					throw new AlreadyExistsException(n);
				}
				nameMappingMap.put(n, id);
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
