package scw.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import scw.core.parameter.ParameterConfig;

public abstract class AbstractMethodActionWrapper implements MethodAction {

	public abstract MethodAction getTargetMethodAction();

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return getTargetMethodAction().getAnnotation(type);
	}
	
	@Override
	public String toString() {
		return getTargetMethodAction().toString();
	}

	public Class<?> getTargetClass() {
		return getTargetMethodAction().getTargetClass();
	}

	public Method getMethod() {
		return getTargetMethodAction().getMethod();
	}

	public ParameterConfig[] getParameterConfigs() {
		return getTargetMethodAction().getParameterConfigs();
	}

}
