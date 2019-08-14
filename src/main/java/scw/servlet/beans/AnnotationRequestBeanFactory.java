package scw.servlet.beans;

import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;

public final class AnnotationRequestBeanFactory implements RequestBeanFactory {
	private volatile Map<String, AnnotationRequestBean> beanMap = new HashMap<String, AnnotationRequestBean>();
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private final String[] filterNames;
	private final ValueWiredManager valueWiredManager;

	public AnnotationRequestBeanFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			String[] filterNames) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.filterNames = filterNames;
		this.valueWiredManager = valueWiredManager;
	}

	public RequestBean get(String name) {
		AnnotationRequestBean bean = beanMap.get(name);
		if (bean == null && !beanMap.containsKey(name)) {
			synchronized (beanMap) {
				bean = beanMap.get(name);
				if (bean == null && !beanMap.containsKey(name)) {
					if (!isBean(name)) {
						beanMap.put(name, null);
						return null;
					}

					try {
						bean = new AnnotationRequestBean(valueWiredManager, beanFactory, propertyFactory, ClassUtils.forName(name),
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

	private boolean isBean(String name) {
		try {
			Class<?> clz = ClassUtils.forName(name);
			if (ReflectUtils.isInstance(clz) && AnnotationRequestBean.getConstructor(clz) != null) {
				return true;
			}
		} catch (ClassNotFoundException e) {
		}
		return false;
	}
}
