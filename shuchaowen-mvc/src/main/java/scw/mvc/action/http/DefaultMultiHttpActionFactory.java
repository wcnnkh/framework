package scw.mvc.action.http;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.annotation.Controller;
import scw.util.value.property.PropertyFactory;

public final class DefaultMultiHttpActionFactory extends MultiHttpActionFactory {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultMultiHttpActionFactory.class);

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Collection<Class<?>> classes) {
		for (Class<?> clz : classes) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			if (!beanFactory.isInstance(clz)) {
				logger.debug("[{}] not create instance", clz);
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Controller methodController = method
						.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				HttpAction httpAction = new HttpAnnotationAction(beanFactory,
						propertyFactory, clz, method);
				scanning(httpAction);
			}
		}
	}

}
