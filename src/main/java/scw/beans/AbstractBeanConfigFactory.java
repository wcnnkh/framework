package scw.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.Destroy;
import scw.core.exception.AlreadyExistsException;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();
	private LinkedList<Destroy> destroys = new LinkedList<Destroy>();

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

	public void addDestroy(Destroy destroy) {
		destroys.add(destroy);
	}

	public Map<String, BeanDefinition> getBeanMap() {
		return beanMap;
	}

	public Map<String, String> getNameMappingMap() {
		return nameMappingMap;
	}

	@SuppressWarnings("unchecked")
	public Collection<Destroy> getDestroys() {
		return (Collection<Destroy>) destroys.clone();
	}
}
