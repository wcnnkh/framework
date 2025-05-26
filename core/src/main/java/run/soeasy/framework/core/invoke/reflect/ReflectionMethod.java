package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.invoke.InvodableElement;
import run.soeasy.framework.core.type.ReflectionUtils;

public class ReflectionMethod extends ReflectionExecutable<Method> implements InvodableElement, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Class<?> declaringClass;
	private Class<?>[] parameterTypes;

	public ReflectionMethod(@NonNull Method method) {
		super(method);
	}

	@Override
	public void setSource(Method source) {
		synchronized (this) {
			this.name = source.getName();
			this.declaringClass = source.getDeclaringClass();
			this.parameterTypes = source.getParameterTypes();
			super.setSource(source);
		}
	}

	@Override
	public @NonNull Method getSource() {
		Method method = super.getSource();
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					method = ReflectionUtils.findMethod(declaringClass, name, parameterTypes).first();
					super.setSource(method);
				}
			}
		}
		return method;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) {
		return ReflectionUtils.invoke(getSource(), target, args);
	}
}
