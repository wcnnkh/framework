package scw.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.Destroy;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanConfigFactory implements BeanConfigFactory {
	protected Logger logger = LoggerUtils.getLogger(getClass());
	protected Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();
	private LinkedList<Destroy> destroys = new LinkedList<Destroy>();

	public void addBean(BeanDefinition beanDefinition) {
		String id = beanDefinition.getId();
		if (beanMap.containsKey(id)) {
			logger.warn("Already exist id:{}, definition:{}", id, JSONUtils.toJSONString(beanDefinition));
		} else {
			beanMap.put(id, beanDefinition);
		}

		String[] names = beanDefinition.getNames();
		if (names != null) {
			for (String n : names) {
				if (nameMappingMap.containsKey(n)) {
					logger.warn("Already exist name:{}, definition:{}", n, JSONUtils.toJSONString(beanDefinition));
					continue;
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

	public Collection<Destroy> getDestroys() {
		return Collections.unmodifiableCollection(destroys);
	}
}
