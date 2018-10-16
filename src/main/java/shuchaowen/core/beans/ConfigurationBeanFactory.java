package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.beans.xml.XmlBeanInfoConfiguration;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class ConfigurationBeanFactory implements BeanFactory {
	private final AnnotationBeanInfoConfiguration annotationBeanInfoConfiguration;
	private final XmlBeanInfoConfiguration xmlBeanInfoConfiguration;
	private volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	private String packageNames;

	/**
	 * @param config
	 *            配置文件
	 * @param packageNames
	 *            注解扫描路径
	 * @throws Exception 
	 */
	public ConfigurationBeanFactory(ConfigFactory configFactory, String config, String packageNames) throws Exception {
		this.packageNames = packageNames;
		this.annotationBeanInfoConfiguration = new AnnotationBeanInfoConfiguration(this, packageNames);
		this.xmlBeanInfoConfiguration = new XmlBeanInfoConfiguration(this, configFactory, config);
		registerSingleton(ConfigurationBeanFactory.class, this);
	}
	
	//注册一个单例
	public void registerSingleton(Class<?> type, Object bean){
		registerSingleton(type.getName(), bean);
	}
	
	private void registerSingleton(String name, Object bean){
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
			try {
				b.wrapper(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Bean getBeanInfoByRealName(String realName){
		Bean beanInfo = xmlBeanInfoConfiguration.getBean(realName);
		if (beanInfo == null) {
			beanInfo = annotationBeanInfoConfiguration.getBean(realName);
		}
		return beanInfo;
	}

	public Bean getBean(String name) {
		return getBeanInfoByRealName(ClassUtils.getCGLIBRealClassName(name));
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
						beanInfo.wrapper(bean);
					}
				}
			}
		} else {
			bean = beanInfo.newInstance();
			beanInfo.wrapper(bean);
		}
		return (T) bean;
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
	}

	public boolean contains(String name) {
		return annotationBeanInfoConfiguration.contains(name) || xmlBeanInfoConfiguration.contains(name);
	}

	public void init() {
		try {
			BeanUtils.initStatic(this, ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			if(contains(entry.getKey())){
				try {
					Bean beanInfo = getBean(entry.getKey());
					beanInfo.destroy(entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			BeanUtils.destroyStaticMethod(ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}
}
