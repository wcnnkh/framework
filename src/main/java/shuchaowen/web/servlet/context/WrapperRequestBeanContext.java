package shuchaowen.web.servlet.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.ClassInfo;
import shuchaowen.common.FieldInfo;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.web.servlet.Request;

public class WrapperRequestBeanContext implements RequestBeanContext {
	private volatile Map<String, Object> wrapperMap;
	private Request request;

	public <T> T getBean(Class<T> type) {
		return getBean(type, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> type, String name) {
		Object obj = null;
		if (wrapperMap == null) {
			synchronized (this) {
				if (wrapperMap == null) {
					wrapperMap = new HashMap<String, Object>(4);
					try {
						obj = wrapperObject(type, name == null ? null : name
								+ ".");
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
							obj = wrapperObject(type, name == null ? null
									: name + ".");
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
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		for (Entry<String, FieldInfo> entry : classInfo.getFieldMap()
				.entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			if (fieldInfo.isStatic() || fieldInfo.isFinal()) {
				continue;
			}

			String key = prefix == null ? fieldInfo.getName() : prefix
					+ fieldInfo.getName();
			if (String.class.isAssignableFrom(fieldInfo.getType())
					|| ClassUtils.isBasicType(fieldInfo.getType())) {
				// 如果是基本数据类型
				Object v = request.getParameter(fieldInfo.getType(), key);
				if (v != null) {
					fieldInfo.set(t, v);
				}
			} else {
				fieldInfo.set(t, wrapperObject(fieldInfo.getType(), key + "."));
			}
		}
		return t;
	}
}
