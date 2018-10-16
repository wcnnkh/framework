package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.util.ClassUtils;

public class AnnotationBeanInfoConfiguration implements BeanInfoConfiguration {
	private volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();
	private final BeanFactory beanFactory;

	public AnnotationBeanInfoConfiguration(BeanFactory beanFactory, String packageNames) {
		this.beanFactory = beanFactory;
		for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				Bean bean = getBean(clz.getName());
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					beanMap.put(i.getName(), bean);
				}

				if (!service.value().equals("")) {
					beanMap.put(service.value(), bean);
				}
			}
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}

	public Bean getBean(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		Bean bean = beanMap.get(realName);
		if (bean == null) {// 这个在配置文件里面找不到
			// 试试这个名字是不是一个类名
			try {
				synchronized (beanMap) {
					bean = beanMap.get(realName);
					if (bean == null) {
						Class<?> clz = Class.forName(realName);
						bean = new AnnotationBean(beanFactory, clz);
						beanMap.put(realName, bean);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bean;
	}

	public boolean contains(String name) {
		return beanMap.containsKey(ClassUtils.getCGLIBRealClassName(name));
	}
}
