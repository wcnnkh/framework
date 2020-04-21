package scw.aop;

public abstract class AbstractProxy implements Proxy {
	private final Class<?> targetClass;

	public AbstractProxy(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Object create(Class<?>[] parameterTypes, Object[] params) {
		if ((parameterTypes == null || parameterTypes.length == 0) && (params == null || params.length == 0)) {
			return create();
		}
		return createInternal(parameterTypes, params);
	}

	protected abstract Object createInternal(Class<?>[] parameterTypes, Object[] params);
}
