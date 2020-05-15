package scw.net.http.server.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.http.server.mvc.MVCUtils;
import scw.net.http.server.mvc.annotation.Controller;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class DefaultActionManager implements ActionManager {
	protected transient final Logger logger = LoggerUtils.getLogger(getClass());
	private final Map<Method, Action> actionMap = new HashMap<Method, Action>();
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private final Collection<ActionFilter> actionFilters;

	public DefaultActionManager(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		this(beanFactory, propertyFactory, MVCUtils
				.getScanAnnotationPackageName());
	}

	public DefaultActionManager(BeanFactory beanFactory,
			PropertyFactory propertyFactory, String scanAnnotationPackageName) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.actionFilters = InstanceUtils.getConfigurationList(
				ActionFilter.class, beanFactory, propertyFactory);
		for (Class<?> clz : ResourceUtils.getPackageScan().getClasses(
				Constants.SYSTEM_PACKAGE_NAME, scanAnnotationPackageName)) {
			if (!isSupport(clz)) {
				continue;
			}
			for (Method method : clz.getDeclaredMethods()) {
				if (!isSupport(method)) {
					continue;
				}

				Action action = builder(clz, method);
				if (action != null) {
					if(logger.isTraceEnabled()){
						logger.trace("register action: {}", action);
					}
					actionMap.put(method, action);
				}
			}
		}
	}

	public Collection<Action> getActions() {
		return Collections.unmodifiableCollection(actionMap.values());
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public Collection<ActionFilter> getActionFilters() {
		return actionFilters;
	}

	protected boolean isSupport(Class<?> clazz) {
		Controller clzController = clazz.getAnnotation(Controller.class);
		if (clzController == null) {
			return false;
		}

		return getBeanFactory().isInstance(clazz);
	}

	protected boolean isSupport(Method method) {
		return method.getAnnotation(Controller.class) != null;
	}

	protected Action builder(Class<?> clazz, Method method) {
		if (isSupport(clazz) && isSupport(method)) {
			return new DefaultHttpAction(getBeanFactory(), clazz, method,
					getActionFilters());
		}
		return null;
	}

	public Action getAction(Class<?> clazz, Method method) {
		return actionMap.get(method);
	}
}
