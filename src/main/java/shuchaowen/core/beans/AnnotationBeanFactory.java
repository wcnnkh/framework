package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class AnnotationBeanFactory implements BeanFactory {
	private final AnnotationBeanInfoConfiguration annotationBeanInfoConfiguration;
	private volatile Map<String, Object> singletonMap = new HashMap<String, Object>();

	public AnnotationBeanFactory(String packageNames) {
		this.annotationBeanInfoConfiguration = new AnnotationBeanInfoConfiguration(this, packageNames);
	}

	// 注册一个单例
	public void registerSingleton(Class<?> type, Object bean) {
		registerSingleton(type.getName(), bean);
	}

	public void registerSingleton(String name, Object bean) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		if (singletonMap.containsKey(realName)) {
			throw new ShuChaoWenRuntimeException("singleton Already exist");// 单例已经存在
		}

		synchronized (singletonMap) {
			if (singletonMap.containsKey(realName)) {
				throw new ShuChaoWenRuntimeException("singleton Already exist");// 单例已经存在
			}

			singletonMap.put(realName, bean);
			Bean b = getBean(realName);
			if (b != null) {
				try {
					b.autowrite(bean);
					b.init(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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

		Bean beanInfo = annotationBeanInfoConfiguration.getBean(realName);
		try {
			return get(beanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
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

	public boolean contains(String name) {
		return annotationBeanInfoConfiguration.contains(name);
	}

	public Bean getBean(String name) {
		return annotationBeanInfoConfiguration.getBean(name);
	}
}
