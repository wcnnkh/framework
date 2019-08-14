package scw.core.instance.support;

import java.util.HashMap;
import java.util.Map;

import scw.core.Init;

/**
 * 单例工厂
 * @author asus1
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionSingleInstanceFactory extends ReflectionInstanceFactory {
	private volatile Map<String, Object> singleMap = new HashMap<String, Object>();

	private void addSingle(String name, Object value) {
		if (value instanceof Init) {
			((Init) value).init();
		}
		singleMap.put(name, value);
	}

	@Override
	public <T> T getInstance(Class<T> type) {
		Object bean = singleMap.get(type.getName());
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(type.getName());
				if (bean == null) {
					bean = super.getInstance(type);
					if (bean != null) {
						addSingle(type.getName(), bean);
					}
				}
			}
		}
		return (T) bean;
	}

	@Override
	public <T> T getInstance(String name) {
		Object bean = singleMap.get(name);
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(name);
				if (bean == null) {
					bean = super.getInstance(name);
					if (bean != null) {
						addSingle(name, bean);
					}
				}
			}
		}
		return (T) bean;
	}

	@Override
	public <T> T getInstance(Class<T> type, Object... params) {
		Object bean = singleMap.get(type.getName());
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(type.getName());
				if (bean == null) {
					bean = super.getInstance(type, params);
					if (bean != null) {
						addSingle(type.getName(), bean);
					}
				}
			}
		}
		return (T) bean;
	}

	@Override
	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes,
			Object... params) {
		Object bean = singleMap.get(type.getName());
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(type.getName());
				if (bean == null) {
					bean = super.getInstance(type, parameterTypes, params);
					if (bean != null) {
						addSingle(type.getName(), bean);
					}
				}
			}
		}
		return (T) bean;
	}

	@Override
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
		Object bean = singleMap.get(name);
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(name);
				if (bean == null) {
					bean = super.getInstance(name, parameterTypes, params);
					if (bean != null) {
						addSingle(name, bean);
					}
				}
			}
		}
		return (T) bean;
	}

	@Override
	public <T> T getInstance(String name, Object... params) {
		Object bean = singleMap.get(name);
		if (bean == null) {
			synchronized (singleMap) {
				bean = singleMap.get(name);
				if (bean == null) {
					bean = super.getInstance(name, params);
					if (bean != null) {
						addSingle(name, bean);
					}
				}
			}
		}
		return (T) bean;
	}
}
