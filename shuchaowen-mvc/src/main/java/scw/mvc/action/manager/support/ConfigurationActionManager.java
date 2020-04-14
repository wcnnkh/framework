package scw.mvc.action.manager.support;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.Constants;
import scw.core.Init;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.manager.ActionLookup;
import scw.mvc.action.manager.ActionManager;
import scw.mvc.action.manager.DefaultActionManager;
import scw.mvc.annotation.Controller;
import scw.mvc.parameter.ParameterFilter;
import scw.util.value.property.PropertyFactory;

@Configuration(value = ActionManager.class, order = Integer.MIN_VALUE)
public class ConfigurationActionManager extends DefaultActionManager implements
		Init {
	private static final long serialVersionUID = 1L;
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private final Collection<ActionFilter> actionFilters;
	private final Collection<ParameterFilter> parameterFilters;

	public ConfigurationActionManager(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.actionFilters = InstanceUtils.getConfigurationList(
				ActionFilter.class, beanFactory, propertyFactory);
		this.parameterFilters = InstanceUtils.getConfigurationList(
				ParameterFilter.class, beanFactory, propertyFactory);
		addAll(InstanceUtils.getConfigurationList(ActionLookup.class,
				beanFactory, propertyFactory, ActionManager.class));
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public void init() {
		for (Class<?> clz : ClassUtils.getClassSet(
				Constants.SYSTEM_PACKAGE_NAME,
				MVCUtils.getScanAnnotationPackageName())) {
			if (ignore(clz)) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				if (ignore(method)) {
					continue;
				}
				register(clz, method);
			}
		}
	}

	protected boolean ignore(Class<?> clazz) {
		Controller clzController = clazz.getAnnotation(Controller.class);
		if (clzController == null) {
			return true;
		}

		if (!getBeanFactory().isInstance(clazz)) {
			logger.warn("[{}] not create instance", clazz);
			return true;
		}
		return false;
	}

	protected boolean ignore(Method method) {
		Controller methodController = method.getAnnotation(Controller.class);
		if (methodController == null) {
			return true;
		}
		return false;
	}

	protected void register(Class<?> clazz, Method method) {
		register(new ControllerHttpAction(beanFactory, clazz, method,
				actionFilters, parameterFilters));
	}
}
