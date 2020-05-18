package scw.mvc.beans;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.mvc.HttpChannel;
import scw.mvc.annotation.ChannelConstructor;

public abstract class AbstractHttpChannelBeanManager implements HttpChannelBeanManager,
		Destroy {
	private volatile Map<String, Object> beanMap;

	public abstract BeanFactory getBeanFactory();

	public abstract HttpChannel getChannel();

	public void destroy() {
		if (beanMap == null) {
			return;
		}

		List<String> idList = new ArrayList<String>(beanMap.keySet());
		ListIterator<String> iterator = idList.listIterator(idList.size());
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			BeanDefinition beanDefinition = getBeanFactory().getDefinition(
					name);
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
				getChannel().getLogger().error(e, "销毁bean异常：" + name);
			}
		}
	}

	public final <T> T getBean(Class<T> type) {
		return getBean(type.getName());
	}

	protected abstract Object[] getBeanArgs(ParameterDescriptor[] parameterConfigs);

	protected Constructor<?> getModelConstructor(Class<?> type) {
		Constructor<?>[] constructors = type.getDeclaredConstructors();
		if(constructors == null || constructors.length == 0){
			return null;
		}
		
		Constructor<?> constructor = null;
		if (constructors.length == 1) {
			constructor = constructors[0];
		} else {
			for (int i = 0; i < constructors.length; i++) {
				constructor = constructors[i];
				ChannelConstructor model = constructor.getAnnotation(ChannelConstructor.class);
				if (model != null) {
					break;
				}
			}
		}
		return constructor;
	}

	private Object getBeanInstance(String id) {
		return beanMap == null ? null : beanMap.get(id);
	}

	protected Map<String, Object> createBeanMap() {
		return new LinkedHashMap<String, Object>(8);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getBean(String name) {
		BeanDefinition beanDefinition = getBeanFactory()
				.getDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			return (T) (getBeanFactory().isInstance(beanDefinition.getId()) ? getBeanFactory()
					.getInstance(beanDefinition.getId()) : null);
		}

		Object bean = getBeanInstance(beanDefinition.getId());
		if (bean == null) {
			// 如果可能通过beanFactory实例化
			if (getBeanFactory().isInstance(name)) {
				synchronized (this) {
					bean = getBeanInstance(beanDefinition.getId());
					if (bean == null) {
						bean = getBeanFactory().getInstance(name);

						if (beanMap == null) {
							beanMap = createBeanMap();
						}

						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			} else {
				Constructor<?> constructor = getModelConstructor(beanDefinition
						.getTargetClass());
				if (constructor == null) {
					return null;
				}

				synchronized (this) {
					bean = getBeanInstance(beanDefinition.getId());
					if (bean == null) {
						bean = getBeanFactory().getInstance(
								beanDefinition.getId(),
								constructor.getParameterTypes(),
								getBeanArgs(ParameterUtils
										.getParameterDescriptors(constructor)));
						if (beanMap == null) {
							beanMap = createBeanMap();
						}
						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			}
		}
		return (T) bean;
	}
}
