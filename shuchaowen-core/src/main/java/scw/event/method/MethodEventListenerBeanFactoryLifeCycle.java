package scw.event.method;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryLifeCycle;
import scw.beans.BeanUtils;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.RegisterMethodEventListener;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.ClassScanner;
import scw.value.ValueFactory;
import scw.value.property.PropertyFactory;

@Configuration
public class MethodEventListenerBeanFactoryLifeCycle implements BeanFactoryLifeCycle {
	private static Logger logger = LoggerUtils.getLogger(MethodEventListenerBeanFactoryLifeCycle.class);

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		if (!beanFactory.isInstance(MethodEventDispatcher.class)) {
			return;
		}

		MethodEventDispatcher dispatcher = beanFactory.getInstance(MethodEventDispatcher.class);
		for (Class<?> clz : ClassScanner.getInstance().getClasses(getScanAnnotationPackageName(propertyFactory))) {
			if (!MethodEventListener.class.isAssignableFrom(clz)) {
				continue;
			}

			RegisterMethodEventListener registerMethodEventListener = clz
					.getAnnotation(RegisterMethodEventListener.class);
			if (registerMethodEventListener == null) {
				continue;
			}

			MethodEventListener eventListener = (MethodEventListener) beanFactory.getInstance(clz);
			dispatcher.registerListener(registerMethodEventListener.value(), eventListener);
			logger.debug("register MethodEventListener: {}", clz);
		}
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		// ignore
	}

	public String getScanAnnotationPackageName(ValueFactory<String> propertyFactory) {
		return propertyFactory.getValue("scw.scan.method.event.package", String.class,
				BeanUtils.getScanAnnotationPackageName(propertyFactory));
	}

}
