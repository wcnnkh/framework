package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.utils.ArrayUtils;

public abstract class ProxyInvoker implements MethodInvoker{
	private static final String HASH_CODE_METHOD = "hashCode";
	private static final String TO_STRING_METHOD = "toString";
	private static final String EQUALS_METHOD = "equals";
	private final Class<?> targetClass;
	private final Method method;

	ProxyInvoker(Class<?> targetClass, Method method) {
		this.targetClass = targetClass;
		this.method = method;
	}

	public abstract Object getProxy();

	public final Class<?> getTargetClass() {
		return targetClass;
	}

	public final Method getMethod() {
		return method;
	}
	
	@Override
	public String toString() {
		return method.toString();
	}

	public boolean isHashCodeMethod() {
		return ArrayUtils.isEmpty(method.getParameterTypes()) && method.getName().equals(HASH_CODE_METHOD);
	}

	public boolean isToStringMethod() {
		return ArrayUtils.isEmpty(method.getParameterTypes()) && method.getName().equals(TO_STRING_METHOD);
	}

	public boolean isEqualsMethod() {
		Class<?>[] types = method.getParameterTypes();
		if (types.length == 1 && method.getName().equals(EQUALS_METHOD)) {
			return method.getParameterTypes()[0] == Object.class;
		}
		return false;
	}

	/**
	 * 一般来说，进行动态代理时都会忽略hashcode、toString、equals方法
	 * 
	 * @return
	 */
	public boolean isIgnoreMethod() {
		return isHashCodeMethod() && isToStringMethod() && isEqualsMethod();
	}

	public int invokeHashCode() {
		return System.identityHashCode(getProxy());
	}

	public String invokeToString() {
		return getProxy().getClass().getName() + "@" + Integer.toHexString(invokeHashCode());
	}

	public boolean invokeEquals(Object[] args) {
		if (args == null || args[0] == null) {
			return false;
		}

		return args[0].equals(getProxy());
	}

	public Object invokeIgnoreMethod(Object[] args) {
		if (isHashCodeMethod()) {
			return invokeHashCode();
		}

		if (isToStringMethod()) {
			return invokeToString();
		}

		if (isEqualsMethod()) {
			return invokeEquals(args);
		}

		throw new UnsupportedOperationException(method.toString());
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @return
	 */
	public boolean isWriteReplaceMethod() {
		return ArrayUtils.isEmpty(method.getParameterTypes()) && getProxy() instanceof Serializable
				&& method.getName().equals(WriteReplaceInterface.WRITE_REPLACE_METHOD);
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @param writeReplaceInterface
	 *            原始类型是否应该实现{@see WriteReplaceInterface}
	 * @return
	 */
	public boolean isWriteReplaceMethod(boolean writeReplaceInterface) {
		if (isWriteReplaceMethod()) {
			if (writeReplaceInterface) {
				return WriteReplaceInterface.class.isAssignableFrom(targetClass);
			} else {
				return !WriteReplaceInterface.class.isAssignableFrom(targetClass);
			}
		}
		return false;
	}

}
