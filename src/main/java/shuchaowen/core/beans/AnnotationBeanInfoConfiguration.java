package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.util.ClassUtils;

public class AnnotationBeanInfoConfiguration implements BeanInfoConfiguration {
	private volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private final BeanFactory beanFactory;

	public AnnotationBeanInfoConfiguration(BeanFactory beanFactory, String packageNames) {
		this.beanFactory = beanFactory;
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					nameMappingMap.put(i.getName(), clz.getName());
				}

				if (!service.value().equals("")) {
					nameMappingMap.put(service.value(), clz.getName());
				}
			}
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}

	public Bean getBean(String name) {
		String n = ClassUtils.getCGLIBRealClassName(name);
		String realName = nameMappingMap.get(n);
		realName = realName == null? n:realName;
		
		Bean bean = beanMap.get(realName);
		if (bean == null) {// 这个在配置文件里面找不到
			// 试试这个名字是不是一个类名
			try {
				synchronized (beanMap) {
					bean = beanMap.get(realName);
					if (bean == null) {
						try {
							Class<?> clz = Class.forName(realName);
							bean = new AnnotationBean(beanFactory, clz);
							beanMap.put(realName, bean);
						} catch (ClassNotFoundException e) {
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bean;
	}

	public boolean contains(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		return nameMappingMap.containsKey(realName) || beanMap.containsKey(realName);
	}
}
