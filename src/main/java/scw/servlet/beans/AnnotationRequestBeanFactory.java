package scw.servlet.beans;

import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;

public final class AnnotationRequestBeanFactory implements RequestBeanFactory {
	private volatile Map<String, AnnotationRequestBean> beanMap = new HashMap<String, AnnotationRequestBean>();
	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private final String[] filterNames;

	public AnnotationRequestBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			String[] filterNames) {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.filterNames = filterNames;
	}

	public RequestBean get(String name) {
		AnnotationRequestBean bean = beanMap.get(name);
		if (bean == null) {
			synchronized (beanMap) {
				bean = beanMap.get(name);
				if (bean == null && contains(name)) {
					try {
						bean = new AnnotationRequestBean(beanFactory, propertiesFactory, ClassUtils.forName(name),
								filterNames);
						if (beanMap.containsKey(bean.getId())) {
							throw new AlreadyExistsException(bean.getId());
						}

						beanMap.put(bean.getId(), bean);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bean;
	}

	public boolean contains(String name) {
		if (beanMap.containsKey(name)) {
			return true;
		}
		try {
			Class<?> clz = ClassUtils.forName(name);
			if (ReflectUtils.isInstance(clz)) {
				return true;
			}
		} catch (ClassNotFoundException e) {
		}
		return false;
	}

}
