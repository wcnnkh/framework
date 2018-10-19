package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanMethodParameter;
import shuchaowen.core.beans.BeanProperties;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.KeyAlreadyExistsException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class XmlPropertiesFactory implements PropertiesFactory {
	private final BeanFactory beanFactory;
	private final Map<String, XmlProperties> propertiesMap = new HashMap<String, XmlProperties>();
	private final Map<String, Object> propertiesValueMap = new HashMap<String, Object>();
	private final Map<String, BeanProperties> xmlPropertiesMap = new HashMap<String, BeanProperties>();

	public XmlPropertiesFactory(BeanFactory beanFactory, List<XmlProperties> properties) {
		this.beanFactory = beanFactory;
		if (properties != null) {
			for (XmlProperties p : properties) {
				if(!StringUtils.isNull(p.getId())){
					propertiesMap.put(p.getId(), p);
				}

				String prefix = (p.getPrefix() == null ? "" : p.getPrefix());
				if(p.getProperties() != null){
					for (Entry<Object, Object> entry : p.getProperties().entrySet()) {
						String key = prefix + entry.getKey();
						if (propertiesValueMap.containsKey(key)) {
							throw new KeyAlreadyExistsException(key);
						}

						propertiesValueMap.put(prefix + entry.getKey(), entry.getValue());
					}
				}

				for (BeanProperties beanProperties : p.getOtherPropertiesMap().values()) {
					String key = prefix + beanProperties.getName();
					if (xmlPropertiesMap.containsKey(key)) {
						throw new KeyAlreadyExistsException(key);
					}

					xmlPropertiesMap.put(key, beanProperties);
				}
			}
		}
	}
	
	public List<BeanProperties> getBeanPropertiesList(String name){
		XmlProperties xmlProperties = propertiesMap.get(name);
		if(xmlProperties == null){
			return null;
		}
		
		List<BeanProperties> list = new ArrayList<BeanProperties>();
		if(xmlProperties.getProperties() != null){
			for(Entry<Object, Object> entry : xmlProperties.getProperties().entrySet()){
				list.add(new BeanProperties(EParameterType.value, entry.getKey().toString(), entry.getValue().toString()));
			}
		}
		
		list.addAll(xmlProperties.getOtherPropertiesMap().values());
		return list;
	}
	
	public List<BeanMethodParameter> getBeanMethodParameterList(String name){
		XmlProperties xmlProperties = propertiesMap.get(name);
		if(xmlProperties == null){
			return null;
		}
		
		List<BeanMethodParameter> list = new ArrayList<BeanMethodParameter>();
		if(xmlProperties.getProperties() != null){
			for(Entry<Object, Object> entry : xmlProperties.getProperties().entrySet()){
				list.add(new BeanMethodParameter(EParameterType.value, null, entry.getKey().toString(), entry.getValue().toString()));
			}
		}
		
		for(Entry<String, BeanProperties> entry : xmlProperties.getOtherPropertiesMap().entrySet()){
			list.add(new BeanMethodParameter(entry.getValue().getType(), null, entry.getKey(), entry.getValue().getValue()));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperties(String name, Class<T> type) throws Exception {
		if (propertiesMap.containsKey(name)) {
			ClassInfo fieldClassInfo = ClassUtils.getClassInfo(type);
			Object obj = beanFactory.get(type);
			for (FieldInfo fieldInfo : fieldClassInfo.getFieldMap().values()) {
				XmlProperties xmlProperties = propertiesMap.get(name);
				Object v = xmlProperties.getValue(beanFactory, this, fieldInfo.getName(), fieldInfo.getType());
				if (v != null) {
					fieldInfo.set(obj, v);
				}
			}
			return (T) obj;
		} else if (propertiesValueMap.containsKey(name)) {
			return StringUtils.conversion(propertiesValueMap.get(name).toString(), type);
		}else if(xmlPropertiesMap.containsKey(name)){
			BeanProperties beanProperties = xmlPropertiesMap.get(name);
			Object v = null;
			switch (beanProperties.getType()) {
			case value:
				v = StringUtils.conversion(beanProperties.getValue(), type);
				break;
			case ref:
				v = beanFactory.get(type);
			case property:
				v = getProperties(name, type);
			default:
				break;
			}
			return (T) v;
		}
		
		String v = ConfigUtils.getSystemProperty(name);
		if(v != null){
			return StringUtils.conversion(v, type);
		}
		return null;
	}
}
