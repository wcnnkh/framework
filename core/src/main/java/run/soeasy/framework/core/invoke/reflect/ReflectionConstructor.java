package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import lombok.NonNull;
import run.soeasy.framework.core.reflect.ReflectionUtils;

public class ReflectionConstructor extends AbstractReflectionExecution<Constructor<?>> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> declaringClass;
	private Class<?>[] parameterTypes;

	public ReflectionConstructor(@NonNull Constructor<?> member) {
		super(member);
	}

	@Override
	public synchronized void setSource(@NonNull Constructor<?> source) {
		this.declaringClass = source.getDeclaringClass();
		this.parameterTypes = source.getParameterTypes();
		super.setSource(source);
	}

	@Override
	public @NonNull Constructor<?> getSource() {
		Constructor<?> constructor = super.getSource();
		if (constructor == null) {
			synchronized (this) {
				if (constructor == null) {
					constructor = ReflectionUtils.findConstructor(declaringClass, parameterTypes).first();
					super.setSource(constructor);
				}
			}
		}
		return constructor;
	}

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return ReflectionUtils.newInstance(getSource(), args);
	}

}
