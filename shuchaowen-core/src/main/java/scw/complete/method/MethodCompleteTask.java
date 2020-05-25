package scw.complete.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.complete.RelyOnBeanFactoryCompleteTask;
import scw.core.reflect.ReflectionUtils;
import scw.core.reflect.SerializableMethod;

public abstract class MethodCompleteTask extends RelyOnBeanFactoryCompleteTask {
	private static final long serialVersionUID = 1L;
	private final SerializableMethod method;
	private final Object[] args;

	public MethodCompleteTask(Class<?> targetClass, Method method, Object[] args) {
		this.method = new SerializableMethod(targetClass, method);
		this.args = args;
	}

	public abstract Object getInstance();

	public SerializableMethod getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object process() throws Exception {
		Method method = getMethod().getMethod();
		ReflectionUtils.makeAccessible(method);
		if (Modifier.isStatic(method.getModifiers())) {
			return method.invoke(null, getArgs());
		} else {
			return method.invoke(getInstance(), getArgs());
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
