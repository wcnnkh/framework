package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class XmlPropertiesFactory implements PropertiesFactory {
	private final Map<String, XmlProperties> propertiesMap = new HashMap<String, XmlProperties>();
	private final Map<String, Object> propertiesValueMap = new HashMap<String, Object>();
	private final Map<String, BeanParameter> xmlPropertiesMap = new HashMap<String, BeanParameter>();
	private final BeanFactory beanFactory;
	
	public XmlPropertiesFactory(BeanFactory beanFactory, List<XmlProperties> properties) {
		this.beanFactory = beanFactory;
		if (properties != null) {
			for (XmlProperties p : properties) {
				if (!StringUtils.isNull(p.getId())) {
					propertiesMap.put(p.getId(), p);
				}

				String prefix = (p.getPrefix() == null ? "" : p.getPrefix());
				if (p.getProperties() != null) {
					for (Entry<Object, Object> entry : p.getProperties().entrySet()) {
						String key = prefix + entry.getKey();
						if (propertiesValueMap.containsKey(key)) {
							throw new AlreadyExistsException(key);
						}
						
						propertiesValueMap.put(key, entry.getValue());
					}
				}

				for (BeanParameter beanProperties : p.getOtherPropertiesMap().values()) {
					String key = prefix + beanProperties.getName();
					if (xmlPropertiesMap.containsKey(key)) {
						throw new AlreadyExistsException(key);
					}

					xmlPropertiesMap.put(key, beanProperties);
				}
			}
		}
	}

	public List<BeanParameter> getBeanParameterList(String name) {
		XmlProperties xmlProperties = propertiesMap.get(name);
		if (xmlProperties == null) {
			return null;
		}

		List<BeanParameter> list = new ArrayList<BeanParameter>();
		if (xmlProperties.getProperties() != null) {
			for (Entry<Object, Object> entry : xmlProperties.getProperties().entrySet()) {
				list.add(new BeanParameter(EParameterType.value, null, entry.getKey().toString(),
						entry.getValue().toString(), xmlProperties.getAttrMap()));
			}
		}

		list.addAll(xmlProperties.getOtherPropertiesMap().values());
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperties(String name, Class<T> type) throws Exception {
		if (propertiesMap.containsKey(name)) {
			XmlProperties xmlProperties = propertiesMap.get(name);
			ClassInfo fieldClassInfo = ClassUtils.getClassInfo(type);
			Object obj = beanFactory.get(type);
			for (FieldInfo fieldInfo : fieldClassInfo.getFieldMap().values()) {
				Object v = xmlProperties.getValue(beanFactory, this, fieldInfo.getName(), fieldInfo.getType());
				if (v != null) {
					fieldInfo.set(obj, v);
				}
			}
			return (T) obj;
		} else if (propertiesValueMap.containsKey(name)) {
			return StringUtils.conversion(propertiesValueMap.get(name).toString(), type);
		} else if (xmlPropertiesMap.containsKey(name)) {
			BeanParameter beanProperties = xmlPropertiesMap.get(name);
			return (T) beanProperties.parseValue(beanFactory, this, type);
		}

		String v = ConfigUtils.getSystemProperty(name);
		if (v != null) {
			return StringUtils.conversion(v, type);
		}
		return null;
	}
}
