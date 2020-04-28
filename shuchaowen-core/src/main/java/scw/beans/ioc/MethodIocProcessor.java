package scw.beans.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class MethodIocProcessor extends AbstractIocProcessor {

	public abstract Method getMethod();

	public boolean isGlobal() {
		return Modifier.isStatic(getMethod().getModifiers());
	}

	protected void checkMethod() {
		if (Modifier.isStatic(getMethod().getModifiers())) {
			logger.warn("class [{}] method [{}] is a static", getMethod()
					.getDeclaringClass(), getMethod());
		}
	}
}
