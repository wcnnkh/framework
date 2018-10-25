package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.beans.xml.XmlBeansConfiguration;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class ConfigurationBeanFactory implements BeanFactory {
	private final AnnotationBeanInfoConfiguration annotationBeanInfoConfiguration;
	private XmlBeansConfiguration xmlBeansConfiguration;
	private volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	
	public ConfigurationBeanFactory(String config) throws Exception {
		this.xmlBeansConfiguration = new XmlBeansConfiguration(this, config);
		this.annotationBeanInfoConfiguration = new AnnotationBeanInfoConfiguration(this, xmlBeansConfiguration.getPackageNames());
		registerSingleton(this.getClass(), this);
	}
	
	public String getPackageNames() {
		return xmlBeansConfiguration.getPackageNames();
	}
	
	//注册一个单例
	public void registerSingleton(Class<?> type, Object bean){
		registerSingleton(type.getName(), bean);
	}
	
	public void registerSingleton(String name, Object bean){
		String realName = ClassUtils.getCGLIBRealClassName(name);
		if(singletonMap.containsKey(realName)){
			throw new ShuChaoWenRuntimeException("singleton Already exist");//单例已经存在
		}
		
		synchronized (singletonMap) {
			if(singletonMap.containsKey(realName)){
				throw new ShuChaoWenRuntimeException("singleton Already exist");//单例已经存在
			}
			
			singletonMap.put(realName, bean);
			Bean b = getBean(realName);
			if(b != null){
				try {
					b.autowrite(bean);
					b.init(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Bean getBeanInfoByRealName(String realName){
		Bean beanInfo = xmlBeansConfiguration == null? null:xmlBeansConfiguration.getBean(realName);
		if (beanInfo == null) {
			beanInfo = annotationBeanInfoConfiguration.getBean(realName);
		}
		return beanInfo;
	}

	public Bean getBean(String name) {
		return getBeanInfoByRealName(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		T t = (T) singletonMap.get(realName);
		if(t != null){
			return t;
		}

		if (!contains(realName)) {
			if (name.equals(BeanFactory.class.getName())) {
				return (T) this;
			}
		}

		Bean beanInfo = getBeanInfoByRealName(realName);
		try {
			return get(beanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T get(Bean beanInfo) throws Exception {
		Object bean;
		String name = ClassUtils.getCGLIBRealClassName(beanInfo.getType().getName());
		if (beanInfo.isSingleton()) {
			bean = singletonMap.get(name);
			if (bean == null) {
				synchronized (singletonMap) {
					bean = singletonMap.get(name);
					if (bean == null) {
						bean = beanInfo.newInstance();
						singletonMap.put(name, bean);
						beanInfo.autowrite(bean);
						beanInfo.init(bean);
					}
				}
			}
		} else {
			bean = beanInfo.newInstance();
			beanInfo.autowrite(bean);
			beanInfo.init(bean);
		}
		return (T) bean;
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
	}

	public boolean contains(String name) {
		return annotationBeanInfoConfiguration.contains(name) || (xmlBeansConfiguration != null && xmlBeansConfiguration.contains(name));
	}
	
	public void init() {
		try {
			BeanUtils.initStatic(this, ClassUtils.getClasses(xmlBeansConfiguration.getPackageNames()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		HashSet<Class<?>> tagSet = new HashSet<Class<?>>();
		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			if(contains(entry.getKey())){
				if(tagSet.contains(entry.getValue().getClass())){
					continue;
				}
				
				try {
					Bean beanInfo = getBean(entry.getKey());
					if(beanInfo != null){
						tagSet.add(entry.getValue().getClass());
						beanInfo.destroy(entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			BeanUtils.destroyStaticMethod(ClassUtils.getClasses(xmlBeansConfiguration.getPackageNames()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
