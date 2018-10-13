package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.util.ClassUtils;

public class ConfigurationBeanFactory implements BeanFactory {
	private volatile Map<String, BeanInfo> beanInfoMap = new HashMap<String, BeanInfo>();
	private volatile Map<Class<?>, Object> singletonMap = new HashMap<Class<?>, Object>();
	private volatile Map<String, BeanInfo> mappingMap = new HashMap<String, BeanInfo>();
	private ConfigFactory configFactory;
	private String packageNames;

	public ConfigurationBeanFactory(String packageNames) {
		// TODO 未配置configFactory
		this.packageNames = packageNames;
		singletonMap.put(ConfigurationBeanFactory.class, this);
		scanningService();
	}

	public ConfigurationBeanFactory(ConfigFactory configFactory, String packageNames) {
		this.configFactory = configFactory;
		this.packageNames = packageNames;

		scanningService();
	}

	private void scanningService() {
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				BeanInfo beanInfo = getBeanInfo(clz.getName());
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					mappingMap.put(i.getName(), beanInfo);
				}

				if (!service.value().equals("")) {
					mappingMap.put(service.value(), beanInfo);
				}
			}
		}
	}

	public BeanInfo getBeanInfo(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		BeanInfo beanInfo = mappingMap.get(realName);
		if(beanInfo != null){
			return beanInfo;
		}
		
		beanInfo = beanInfoMap.get(realName);
		if (beanInfo == null) {// 这个在配置文件里面找不到
			// 试试这个名字是不是一个类名
			try {
				synchronized (beanInfoMap) {
					beanInfo = beanInfoMap.get(realName);
					if(beanInfo == null){
						Class<?> clz = Class.forName(realName);
						beanInfo = new BeanInfo(clz);
						beanInfoMap.put(realName, beanInfo);
					}
				}
			} catch (ClassNotFoundException e) {
				throw new BeansException(e);
			}
		}
		return beanInfo;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		if(!contains(name)){
			if(name.equals(BeanFactory.class.getName())){
				return (T) this;
			}
		}
		
		BeanInfo beanInfo = getBeanInfo(name);
		return get(beanInfo);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(BeanInfo beanInfo) {
		Object bean;
		if (beanInfo.isSingleton()) {
			bean = singletonMap.get(beanInfo.getType());
			if (bean == null) {
				synchronized (singletonMap) {
					bean = singletonMap.get(beanInfo.getType());
					if (bean == null) {
						bean = beanInfo.newInstance(this, configFactory);
						singletonMap.put(beanInfo.getType(), bean);
						beanInfo.wrapper(bean, this);
					}
				}
			}
		} else {
			bean = beanInfo.newInstance(this, configFactory);
			beanInfo.wrapper(bean, this);
		}
		return (T) bean;
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
	}

	public boolean contains(String name) {
		return mappingMap.containsKey(name);
	}

	public void init() {
		try {
			BeanUtils.initStatic(this, ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		for (Entry<Class<?>, Object> entry : singletonMap.entrySet()) {
			try {
				BeanInfo beanInfo = getBeanInfo(entry.getKey().getName());
				beanInfo.destoryMethod(entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			BeanUtils.destroyStaticMethod(ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isProxy(String name) {
		return getBeanInfo(name).isProxy();
	}
}
