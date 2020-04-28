package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanMethod implements BeanMethod {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected void checkMethod(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			logger.warn("class [{}] method [{}] is a static", method.getDeclaringClass(), method);
		}
	}
}
