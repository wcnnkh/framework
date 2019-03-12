package scw.common;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.common.utils.ClassUtils;

public final class MethodConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> clz;
	private String name;
	private Class<?>[] parameterTypes;
	private Class<?> returnType;

	/**
	 * 用户序列化
	 */
	protected MethodConfig() {
	}

	public MethodConfig(Method method) {
		this.clz = method.getDeclaringClass();
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.returnType = method.getReturnType();
	}

	public MethodConfig(Class<?> clz, Method method) {
		this.clz = clz;
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.returnType = method.getReturnType();
	}

	private transient Method method;

	public Method getMethod() throws NoSuchMethodException, SecurityException {
		if (method == null) {
			method = clz.getDeclaredMethod(name, parameterTypes);
		}
		return method;
	}

	private transient String[] parameterNames;

	public String[] getParameterNames() throws NoSuchMethodException, SecurityException {
		if (parameterNames == null) {
			Method method = getMethod();
			if (method != null) {
				parameterNames = ClassUtils.getParameterName(method);
			}
		}
		return parameterNames;
	}

	public int getParameterCount() {
		return parameterTypes.length;
	}

	public Class<?> getClz() {
		return clz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}
}
