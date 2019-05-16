package scw.servlet.context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.servlet.Request;

public class WrapperRequestBeanContext implements RequestBeanContext {
	private volatile Map<String, Object> wrapperMap;
	private Request request;

	public WrapperRequestBeanContext(Request request) {
		this.request = request;
	}

	public <T> T getBean(Class<T> type) {
		return getBean(type, type.getName(), null);
	}

	public <T> T getBean(Class<T> type, String name) {
		return getBean(type, name, name + ".");
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> type, String name, String prefix) {
		Object obj = null;
		if (wrapperMap == null) {
			synchronized (this) {
				if (wrapperMap == null) {
					wrapperMap = new HashMap<String, Object>(4);
					try {
						obj = wrapperObject(type, prefix == null ? null : prefix + ".");
						wrapperMap.put(name, obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			obj = wrapperMap.get(name);
			if (obj == null) {
				synchronized (this) {
					obj = wrapperMap.get(name);
					if (obj == null) {
						try {
							obj = wrapperObject(type, prefix == null ? null : prefix + ".");
							wrapperMap.put(name, obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return (T) obj;
	}

	public void destroy() {
	}

	private <T> T wrapperObject(Class<T> type, String prefix) throws Exception {
		T t = type.newInstance();
		Class<?> clz = type;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				String key = prefix == null ? field.getName() : prefix + field.getName();
				if (String.class.isAssignableFrom(field.getType())
						|| ClassUtils.isPrimitiveOrWrapper(field.getType())) {
					// 如果是基本数据类型
					Object v = request.getParameter(field.getType(), key);
					if (v != null) {
						ReflectUtils.setFieldValue(clz, field, t, v);
					}
				} else {
					ReflectUtils.setFieldValue(clz, field, t, wrapperObject(field.getType(), key + "."));
				}
			}
			clz = clz.getSuperclass();
		}
		return t;
	}
}
