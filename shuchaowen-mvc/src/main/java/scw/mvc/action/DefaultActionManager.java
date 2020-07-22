package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.Constants;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.Controller;
import scw.util.ClassScanner;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultActionManager implements ActionManager {
	protected transient final Logger logger = LoggerUtils.getLogger(getClass());
	private final Map<Method, Action> actionMap = new HashMap<Method, Action>();
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;

	public DefaultActionManager(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this(beanFactory, propertyFactory, MVCUtils.getScanAnnotationPackageName(propertyFactory));
	}

	public DefaultActionManager(BeanFactory beanFactory, PropertyFactory propertyFactory,
			String scanAnnotationPackageName) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		for (Class<?> clz : ClassScanner.getInstance().getClasses(Constants.SYSTEM_PACKAGE_NAME,
				scanAnnotationPackageName)) {
			if (!isSupport(clz)) {
				continue;
			}
			for (Method method : clz.getDeclaredMethods()) {
				if (!isSupport(method)) {
					continue;
				}

				Action action = builder(clz, method);
				if (action != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("register action: {}", action);
					}
					
					if(action instanceof AbstractAction){
						((AbstractAction) action).optimization();
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
			return new DefaultAction(getBeanFactory(), clazz, method);
		}
		return null;
	}

	public Action getAction(Class<?> clazz, Method method) {
		return actionMap.get(method);
	}
}
