package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.utils.ArrayUtils;
import scw.util.attribute.SimpleAttributes;

public class ProxyContext extends SimpleAttributes<Object, Object> {
	private static final String HASH_CODE_METHOD = "hashCode";
	private static final String TO_STRING_METHOD = "toString";
	private static final String EQUALS_METHOD = "equals";
	private static final long serialVersionUID = 1L;
	private final Object proxy;
	private final Class<?> targetClass;
	private final Method method;
	private final Object[] args;
	private final long createTime;

	public ProxyContext(Object proxy, Class<?> targetClass, Method method, Object[] args) {
		this.proxy = proxy;
		this.targetClass = targetClass;
		this.method = method;
		this.args = args;
		this.createTime = System.currentTimeMillis();
	}

	public final Object getProxy() {
		return proxy;
	}

	public final Class<?> getTargetClass() {
		return targetClass;
	}

	public final Method getMethod() {
		return method;
	}

	public final Object[] getArgs() {
		return args;
	}

	public final long getCreateTime() {
		return createTime;
	}

	@Override
	public String toString() {
		return method.toString();
	}

	public boolean isHashCodeMethod() {
		return ArrayUtils.isEmpty(args) && method.getName().equals(HASH_CODE_METHOD);
	}

	public boolean isToStringMethod() {
		return ArrayUtils.isEmpty(args) && method.getName().equals(TO_STRING_METHOD);
	}

	public boolean isEqualsMethod() {
		if (args != null && args.length == 1 && method.getName().equals(EQUALS_METHOD)) {
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
		return System.identityHashCode(proxy);
	}

	public String invokeToString() {
		return proxy.getClass().getName() + "@" + Integer.toHexString(invokeHashCode());
	}

	public boolean invokeEquals() {
		if (args == null || args[0] == null) {
			return false;
		}

		return args[0].equals(proxy);
	}

	public Object invokeIgnoreMethod() {
		if (isHashCodeMethod()) {
			return invokeHashCode();
		}

		if (isToStringMethod()) {
			return invokeToString();
		}

		if (isEqualsMethod()) {
			return invokeEquals();
		}

		throw new UnsupportedOperationException(method.toString());
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @return
	 */
	public boolean isWriteReplaceMethod() {
		return ArrayUtils.isEmpty(args) && proxy instanceof Serializable
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
