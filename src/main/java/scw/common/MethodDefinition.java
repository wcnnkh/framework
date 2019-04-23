package scw.common;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.common.utils.ClassUtils;

/**
 * 一个方法的定义
 * 
 * @author shuchaowen
 *
 */
public final class MethodDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> belongClass;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Class<?> returnType;
	private transient Method method;

	/**
	 * 用于序列化
	 */
	protected MethodDefinition() {
	}

	public MethodDefinition(Class<?> belongClass, Method method) {
		this.belongClass = belongClass;
		this.methodName = method.getName();
		this.method = method;
		this.parameterTypes = method.getParameterTypes();
		this.returnType = method.getReturnType();
		if (!Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
		}
	}

	public Method getMethod() throws NoSuchMethodException, SecurityException {
		if (method == null) {
			method = belongClass.getDeclaredMethod(methodName, parameterTypes);
			if (!Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
		}
		return method;
	}

	public Object invoke(Object obj, Object[] args) throws Throwable {
		return getMethod().invoke(obj, args);
	}

	/**
	 * 获取这个方法所属的类
	 * 
	 * @return
	 */
	public Class<?> getBelongClass() {
		return belongClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes.clone();
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public int getParameterCount() {
		return parameterTypes == null ? 0 : parameterTypes.length;
	}

	public String[] getParameterNames() throws NoSuchMethodException, SecurityException {
		return ClassUtils.getParameterName(getMethod());
	}
}
