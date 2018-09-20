package shuchaowen.core.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class SingletonFactory implements BeanFactory {
	private Map<String, Object> singletonMap = new HashMap<String, Object>();
	private Map<String, String> nameMappingMap = new HashMap<String, String>();
	private String packageName;

	public SingletonFactory(String packageName) {
		singletonMap.put(SingletonFactory.class.getName(), this);
		nameMappingMap.put(BeanFactory.class.getName(), SingletonFactory.class.getName());
		this.packageName = packageName;
		for (Class<?> clz : ClassUtils.getClasses(packageName)) {
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

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(packageName);
	}

	public void destroy() {
		for (Object obj : singletonMap.values()) {
			if (obj != null) {
				try {
					BeanUtils.wrapperDestoryMethod(this, obj.getClass(), obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object get(final String name) {
		if (singletonMap.containsKey(name)) {
			return singletonMap.get(name);
		} else {
			if (nameMappingMap.containsKey(name)) {
				return get(nameMappingMap.get(name));
			} else {
				synchronized (singletonMap) {
					if (singletonMap.containsKey(name)) {
						return get(name);
					} else {
						try {
							Class<?> type = Class.forName(name);
							Object obj = BeanUtils.getProxy(type);
							singletonMap.put(name, obj);
							wrapper(type, obj);
							return obj;
						} catch (Exception e) {
							throw new ShuChaoWenRuntimeException(e);
						}
					}
				}
			}
		}
	}

	private Object wrapper(Class<?> clz, Object obj) throws Exception {
		return BeanUtils.wrapper(this, clz, obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final Class<T> type) {
		return (T) get(type.getName());
	}

	public boolean contains(String name) {
		if (nameMappingMap.containsKey(name)) {
			return true;
		} else {
			return singletonMap.containsKey(name);
		}
	}
}
