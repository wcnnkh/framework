package scw.mvc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.json.JSONUtils;
import scw.result.Result;

public abstract class AbstractChannel implements Channel, Destroy {
	private final long createTime;
	private volatile Map<String, Object> beanMap;
	private final BeanFactory beanFactory;

	public AbstractChannel(BeanFactory beanFactory) {
		this.createTime = System.currentTimeMillis();
		this.beanFactory = beanFactory;
	}

	public void destroy() {
		if (beanMap == null) {
			return;
		}

		List<String> idList = new ArrayList<String>(beanMap.keySet());
		ListIterator<String> iterator = idList.listIterator(idList.size());
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
			if (beanDefinition == null) {
				continue;
			}

			Object bean = beanMap.get(name);
			if (bean == null) {
				continue;
			}

			try {
				beanDefinition.destroy(bean);
			} catch (Exception e) {
				getLogger().error(e, "销毁bean异常：" + name);
			}
		}
	}

	public final <T> T getBean(Class<T> type) {
		return getBean(type.getName());
	}

	private Object getBean(BeanDefinition beanDefinition, Constructor<?> constructor) {
		return beanFactory.getInstance(beanDefinition.getId(), constructor.getParameterTypes(),
				MVCUtils.getParameterValues(this, ParameterUtils.getParameterConfigs(constructor)));
	}

	@SuppressWarnings("unchecked")
	public final <T> T getBean(String name) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			if (ReflectionUtils.isInstance(beanDefinition.getType(), false)) {
				Constructor<?> constructor = MVCUtils.getModelConstructor(beanDefinition.getType());
				if (constructor == null) {
					return null;
				}

				return (T) getBean(beanDefinition, constructor);
			} else {
				return beanFactory.getInstance(beanDefinition.getId());
			}
		}

		Object bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
		if (bean == null) {
			if (!ReflectionUtils.isInstance(beanDefinition.getType(), false)) {
				synchronized (this) {
					bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
					if (bean == null) {
						bean = beanFactory.getInstance(beanDefinition.getId());

						if (beanMap == null) {
							beanMap = new LinkedHashMap<String, Object>(8);
						}
						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			} else {
				Constructor<?> constructor = MVCUtils.getModelConstructor(beanDefinition.getType());
				if (constructor == null) {
					return null;
				}

				synchronized (this) {
					bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
					if (bean == null) {
						bean = getBean(beanDefinition, constructor);
						if (beanMap == null) {
							beanMap = new LinkedHashMap<String, Object>(8);
						}
						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			}
		}
		return (T) bean;
	}

	public boolean isLogEnabled() {
		return getLogger().isDebugEnabled();
	}

	public void log(Object format, Object... args) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug(format, args);
		}
	}

	public long getCreateTime() {
		return createTime;
	}

	public void write(Object obj) throws Throwable {
		if (obj != null && getLogger().isErrorEnabled() && obj instanceof Result && ((Result) obj).isError()) {
			getLogger().error("fail:{}, result={}", this.toString(), JSONUtils.toJSONString(obj));
		}
	}
}
