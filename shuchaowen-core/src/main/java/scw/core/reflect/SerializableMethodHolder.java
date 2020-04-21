package scw.core.reflect;

import java.lang.reflect.Method;

public class SerializableMethodHolder extends AbstractSerializableMethodHolder {
	private static final long serialVersionUID = 1L;
	private Class<?> belongClass;
	private Class<?>[] parameterTypes;

	public SerializableMethodHolder(Class<?> belongClass, Method method) {
		super(method == null? null:method.getName());//训练化时可能会调用此方法，所以这里要求可以为空
		this.belongClass = belongClass;
		this.parameterTypes = method.getParameterTypes();
	}

	public Class<?> getBelongClass() {
		return belongClass;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
}
